package spiglet.visitor;

import java.util.HashMap;
import java.util.Vector;
import spiglet.ProcBlock;
import spiglet.syntaxtree.*;

public class Spiglet2KangaVisitor extends GJNoArguDepthFirst<String> {
	private HashMap<String, ProcBlock> ProcMap;
	private ProcBlock CurProc;
	private HashMap<String, String> LabelMap = new HashMap<String, String>();
	private int nLabel;
	
	public Spiglet2KangaVisitor(HashMap<String, ProcBlock> _ProcMap) {
		this.ProcMap = _ProcMap;
	    this.nLabel = 0;
	}

	String getLabel(String oldLabel){
		String s;
		if(LabelMap.containsKey(oldLabel)){
			s = LabelMap.get(oldLabel);
		}else{
			s = "L" + (nLabel ++) + "_" + oldLabel;
			LabelMap.put(oldLabel, s);
		}
		return s;
	}

	String getTemp(String regName, String tempName){
		if(CurProc.regT.containsKey(tempName)){
			return CurProc.regT.get(tempName);
		}else if(CurProc.regS.containsKey(tempName)){
			return CurProc.regS.get(tempName);
		}else{
			System.out.printf("\tALOAD %s %s\n", regName, CurProc.regSpi.get(tempName));
			return regName;
		}
	}

	void moveTemp(String tempName, String exp){
		if(CurProc.regSpi.containsKey(tempName)){
			System.out.printf("\tMOVE v0 %s\n", exp);
			System.out.printf("\tASTORE %s v0\n", CurProc.regSpi.get(tempName));
		}else{
			tempName = getTemp("", tempName);
			if(!tempName.equals(exp))	// in case of MOVE rx, rx
				System.out.printf("\tMOVE %s %s\n", tempName, exp);
		}
	}

	public String visit(NodeOptional n) {
		if (n.present())	// print new label
			System.out.print(getLabel(n.node.accept(this)));
		return null;
	}

	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public String visit(Goal n) {
		CurProc = ProcMap.get("MAIN");
		System.out.printf("MAIN [%d] [%d] [%d]\n", CurProc.nArg,
				CurProc.maxStack, CurProc.maxParam);
		n.f1.accept(this);
		System.out.println("END");
		n.f3.accept(this);
		return null;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public String visit(Procedure n) {
		String procName = n.f0.accept(this);
		CurProc = ProcMap.get(procName);
		System.out.printf("\n%s [%d] [%d] [%d]\n", procName, CurProc.nArg,
				CurProc.maxStack, CurProc.maxParam);
		n.f4.accept(this);
		return null;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public String visit(NoOpStmt n) {
		System.out.println("\tNOOP");
		return null;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public String visit(ErrorStmt n) {
		System.out.println("\tERROR");
		return null;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Temp() 
	 * f2 -> Label()
	 */
	public String visit(CJumpStmt n) {
		System.out.printf("\tCJUMP %s %s\n",
				getTemp("v0", n.f1.accept(this)), getLabel(n.f2.accept(this)));
		return null;
	}

	/**
	 * f0 -> "JUMP" 
	 * f1 -> Label()
	 */
	public String visit(JumpStmt n) {
		System.out.printf("\tJUMP %s\n", getLabel(n.f1.accept(this)));
		return null;
	}

	/**
	 * f0 -> "HSTORE" 
	 * f1 -> Temp() 
	 * f2 -> IntegerLiteral() 
	 * f3 -> Temp()
	 */
	public String visit(HStoreStmt n) {
		System.out.printf("\tHSTORE %s %s %s\n", getTemp("v0", n.f1.accept(this)),
				n.f2.accept(this), getTemp("v1", n.f3.accept(this)));
		return null;
	}

	/**
	 * f0 -> "HLOAD" 
	 * f1 -> Temp() 
	 * f2 -> Temp() 
	 * f3 -> IntegerLiteral()
	 */
	public String visit(HLoadStmt n) {
		String temp = n.f1.accept(this);
		String exp = getTemp("v1", n.f2.accept(this));
		String offset = n.f3.accept(this);
		if(CurProc.regSpi.containsKey(temp)){
			System.out.printf("\tHLOAD v1 %s %s\n", exp, offset);
			moveTemp(temp, "v1");
		}else{
			System.out.printf("\tHLOAD %s %s %s\n", getTemp("v0", temp), exp, offset);
		}
		return null;
	}

	/**
	 * f0 -> "MOVE" 
	 * f1 -> Temp() 
	 * f2 -> Exp()
	 */
	public String visit(MoveStmt n) {
		moveTemp(n.f1.accept(this), n.f2.accept(this));
		return null;
	}

	/**
	 * f0 -> "PRINT" 
	 * f1 -> SimpleExp()
	 */
	public String visit(PrintStmt n) {
		System.out.printf("\tPRINT %s\n", n.f1.accept(this));
		return null;
	}

	/**
	 * f0 -> Call() | HAllocate() | BinOp() | SimpleExp()
	 */
	public String visit(Exp n) {
		return n.f0.accept(this);
	}

	/**
	 * f0 -> "BEGIN" 
	 * f1 -> StmtList() 
	 * f2 -> "RETURN" 
	 * f3 -> SimpleExp() 
	 * f4 -> "END"
	 */
	public String visit(StmtExp n) {
		int i;
		i = CurProc.nArg > 4 ? CurProc.nArg - 4 : 0;
		if(CurProc.regS.size() != 0){
			for(int j = i; j < i + CurProc.regS.size(); j ++){
				if(j - i > 7) break;
				System.out.println("\tASTORE SPILLEDARG " + j + " s" + (j - i));
			}
		}
		for(i = 0; i < CurProc.nArg && i < 4; i ++)
			if(CurProc.tmpMap.containsKey(i))
				moveTemp("TEMP " + i, "a" + i);
		for(; i < CurProc.nArg; i ++){
			String tempName = "TEMP " + i;
			if(CurProc.tmpMap.containsKey(i)){
				if(CurProc.regSpi.containsKey(tempName)){
					System.out.printf("\tALOAD v0 SPILLEDARG %d\n", i - 4);
					moveTemp(tempName, "v0");
				}else{
					System.out.printf("\tALOAD %s SPILLEDARG %d\n",
							getTemp("", tempName), i - 4);
				}
			}
		}
		n.f1.accept(this);
		
		System.out.println("\tMOVE v0 " + n.f3.accept(this));
		
		i = CurProc.nArg > 4 ? CurProc.nArg - 4 : 0;
		if(CurProc.regS.size() != 0){
			for(int j = i; j < i + CurProc.regS.size(); j ++){
				if(j - i > 7) break;
				System.out.println("\tALOAD s" + (j - i) + " SPILLEDARG " + j);
			}
		}
		System.out.println("END");
		return null;
	}

	/**
	 * f0 -> "CALL" 
	 * f1 -> SimpleExp() 
	 * f2 -> "(" 
	 * f3 -> ( Temp() )* 
	 * f4 -> ")"
	 */
	public String visit(Call n) {
		Vector<Node> v = n.f3.nodes;
		int nParam = v.size();
		int i;
		for(i = 0; i < nParam && i < 4; i ++)
			System.out.printf("\tMOVE a%d %s\n", 
					i,	getTemp("v0", v.get(i).accept(this)));
		for(; i < nParam; i ++)
			System.out.printf("\tPASSARG %d %s\n", 
					i - 3, getTemp("v0", v.get(i).accept(this)));
		System.out.printf("\tCALL %s\n", n.f1.accept(this));
		return "v0";
	}

	/**
	 * f0 -> "HALLOCATE" 
	 * f1 -> SimpleExp()
	 */
	public String visit(HAllocate n) {
		return "HALLOCATE " + n.f1.accept(this);
	}

	/**
	 * f0 -> Operator() 
	 * f1 -> Temp() 
	 * f2 -> SimpleExp()
	 */
	public String visit(BinOp n) {
		return n.f0.accept(this) + getTemp("v1", n.f1.accept(this)) 
				+ " " + n.f2.accept(this);
	}

	/**
	 * f0 -> "LT" | "PLUS" | "MINUS" | "TIMES"
	 */
	public String visit(Operator n) {
		String[] ops = { "LT ", "PLUS ", "MINUS ", "TIMES " };
		return ops[n.f0.which];
	}

	/**
	 * f0 -> Temp() | IntegerLiteral() | Label()
	 */
	public String visit(SimpleExp n) {
		String _ret = n.f0.accept(this);
		if (n.f0.which == 0) 
			_ret = getTemp("v0", _ret);
		return _ret;
	}

	/**
	 * f0 -> "TEMP" 
	 * f1 -> IntegerLiteral()
	 */
	public String visit(Temp n) {
		return "TEMP " + n.f1.accept(this);
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public String visit(IntegerLiteral n) {
		return n.f0.toString();
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public String visit(Label n) {
		return n.f0.toString();
	}

}
