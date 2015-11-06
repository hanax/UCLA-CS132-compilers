package piglet.visitor;

import java.util.Enumeration;

import piglet.piglet2spiglet.PrintSpiglet;
import piglet.syntaxtree.*;
import minijava.symboltable.MType;

public class Piglet2SpigletVisitor extends GJDepthFirst<String, MType>{

	private int cur_tmp = PrintSpiglet.nTmp + 1;

	public String visit(NodeListOptional n, MType argu) {
		String _ret = "";
		if (n.present()) {
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
				_ret += e.nextElement().accept(this, argu);
			}
		}
		return _ret;
	}

	public String visit(NodeOptional n, MType argu) {
		if (n.present()) {
			int tmp = PrintSpiglet.getT();
			PrintSpiglet.setT(0);
			PrintSpiglet.pln(n.node.accept(this, argu));
			PrintSpiglet.setT(tmp);
		}
		return null;
	}

	//
	// User-generated visitor methods below
	//

	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public String visit(Goal n, MType argu) {
		PrintSpiglet.pln("MAIN");
		PrintSpiglet.addT();
		n.f1.accept(this, argu);
		PrintSpiglet.decT();
		PrintSpiglet.pln("END");
		n.f3.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public String visit(Procedure n, MType argu) {
		PrintSpiglet.pln("");
		PrintSpiglet.pln(n.f0.accept(this, argu) + "[ " + n.f2.accept(this, argu) + "] ");
		PrintSpiglet.pln("BEGIN");
		PrintSpiglet.addT();
		String tmp = n.f4.accept(this, argu);
		PrintSpiglet.decT();
		PrintSpiglet.pln("RETURN " + tmp);
		PrintSpiglet.pln("END");
		return "";
	}

	/**
	 * f0 -> "NOOP"
	 */
	public String visit(NoOpStmt n, MType argu) {
		PrintSpiglet.pln("NOOP");
		return null;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public String visit(ErrorStmt n, MType argu) {
		PrintSpiglet.pln("ERROR");
		return null;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Exp()
	 * f2 -> Label()
	 */
	public String visit(CJumpStmt n, MType argu) {
		PrintSpiglet.pln("CJUMP " + n.f1.accept(this, argu) + n.f2.accept(this, argu));
		return null;
	}
	
	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public String visit(JumpStmt n, MType argu) {
		PrintSpiglet.pln("JUMP " + n.f1.accept(this, argu));
		return null;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Exp()
	 * f2 -> IntegerLiteral()
	 * f3 -> Exp()
	 */
	public String visit(HStoreStmt n, MType argu) {
		PrintSpiglet.pln("HSTORE " + n.f1.accept(this, argu) 
				+ n.f2.accept(this, argu) 
				+ n.f3.accept(this, argu));
		return null;
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 * f3 -> IntegerLiteral()
	 */
	public String visit(HLoadStmt n, MType argu) {
		PrintSpiglet.pln("HLOAD " + n.f1.accept(this, argu) 
				+ n.f2.accept(this, argu)
				+ n.f3.accept(this, argu));
		return null;
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 */
	public String visit(MoveStmt n, MType argu) {
		PrintSpiglet.pln("MOVE " + n.f1.accept(this, argu) + n.f2.accept(this, argu));
		return null;
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> Exp()
	 */
	public String visit(PrintStmt n, MType argu) {
		PrintSpiglet.pln("PRINT " + n.f1.accept(this, argu));
		return null;
	}

	/**
	 * f0 -> StmtExp()
	 *       | Call()
	 *       | HAllocate()
	 *       | BinOp()
	 *       | Temp()
	 *       | IntegerLiteral()
	 *       | Label()
	 */
	public String visit(Exp n, MType argu) {
		String _ret = "TEMP " + cur_tmp++ + " ";
		PrintSpiglet.pln("MOVE " + _ret + n.f0.accept(this, argu));
		return _ret;
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> Exp()
	 * f4 -> "END"
	 */
	public String visit(StmtExp n, MType argu) {
		n.f1.accept(this, argu);
		return n.f3.accept(this, argu);
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> Exp()
	 * f2 -> "("
	 * f3 -> ( Exp() )*
	 * f4 -> ")"
	 */
	public String visit(Call n, MType argu) {
		return "CALL " + n.f1.accept(this, argu) + "( " + n.f3.accept(this, argu) + ") ";
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> Exp()
	 */
	public String visit(HAllocate n, MType argu) {
		return "HALLOCATE " + n.f1.accept(this, argu);
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Exp()
	 * f2 -> Exp()
	 */
	public String visit(BinOp n, MType argu) {
		return n.f0.accept(this, argu) 
				+ n.f1.accept(this, argu) 
				+ n.f2.accept(this, argu);
	}

	/**
	 * f0 -> "LT"
	 *       | "PLUS"
	 *       | "MINUS"
	 *       | "TIMES"
	 */
	public String visit(Operator n, MType argu) {
		String[] ops = { "LT ", "PLUS ", "MINUS ", "TIMES " };
		return ops[n.f0.which];
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public String visit(Temp n, MType argu) {
		return "TEMP " + n.f1.accept(this, argu);
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public String visit(IntegerLiteral n, MType argu) {
		return n.f0.toString() + " ";
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public String visit(Label n, MType argu) {
		return n.f0.toString() + " ";
	}

}
