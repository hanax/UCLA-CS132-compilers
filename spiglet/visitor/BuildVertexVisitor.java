package spiglet.visitor;

import java.util.Enumeration;
import java.util.HashMap;
import spiglet.Interval;
import spiglet.ProcBlock;
import spiglet.syntaxtree.*;

/**
 * BuildVertexVisitor
 * 
 * Build the following:
 * - ProcMap: several ProcBlocks
 * - ProcBlock: Graph, nArg, maxParam, tmpMap
 * - Graph: Vertex(nStt), CallPos
 */

public class BuildVertexVisitor extends GJNoArguDepthFirst<String> {
  
  private HashMap<String, ProcBlock> ProcMap;
  private HashMap<String, Integer> LabelMap;
  private ProcBlock CurProc;
  private int nStt;
  
  public BuildVertexVisitor(HashMap<String, ProcBlock> _ProcMap, HashMap<String, Integer> _LabelMap) {
    this.ProcMap = _ProcMap;
    this.nStt = 0;
    this.LabelMap = _LabelMap;
  }

  public String visit(NodeOptional n) {
	  	if (n.present()){
	  		LabelMap.put(n.node.accept(this), nStt);
	  	}
		return null;
  }
  
  //
  // Auto class visitors--probably don't need to be overridden.
  //
  
  public String visit(NodeListOptional n) {
    if ( n.present() ) {
       Integer _count = 0;
       for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
          e.nextElement().accept(this);
          _count++;
       }
       return _count.toString();
    }else return null;
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
  public String visit(Goal n) {
	CurProc = new ProcBlock("MAIN", 0);
	ProcMap.put("MAIN", CurProc);
	
	nStt = 0;
	CurProc.graph.addVertex(nStt ++);	//BEGIN
	
    n.f1.accept(this);
    
	CurProc.graph.addVertex(nStt);	//END
	
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
	nStt = 0;
	CurProc = new ProcBlock(n.f0.f0.toString(), Integer.parseInt(n.f2.accept(this)));
	ProcMap.put(n.f0.f0.toString(), CurProc);
	n.f2.accept(this);
    n.f4.accept(this);
    return null;
  }
  
  /**
   * f0 -> NoOpStmt()
   *       | ErrorStmt()
   *       | CJumpStmt()
   *       | JumpStmt()
   *       | HStoreStmt()
   *       | HLoadStmt()
   *       | MoveStmt()
   *       | PrintStmt()
   */
  public String visit(Stmt n) {
	CurProc.graph.addVertex(nStt);
    n.f0.accept(this);
    nStt ++;
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
    n.f1.accept(this);
    n.f3.accept(this);
    CurProc.graph.sCallPos.add(nStt);
	if (CurProc.maxParam < n.f3.size())
		CurProc.maxParam = n.f3.size();
    return null;
  }
  
  /**
   * f0 -> "BEGIN"
   * f1 -> StmtList()
   * f2 -> "RETURN"
   * f3 -> SimpleExp()
   * f4 -> "END"
   */
  public String visit(StmtExp n) {
	CurProc.graph.addVertex(nStt ++);	//BEGIN
	
    n.f1.accept(this);

	CurProc.graph.addVertex(nStt ++);	//RETURN
	
    n.f3.accept(this);
	
    CurProc.graph.addVertex(nStt);	//END
    return null;
  }
  
	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public String visit(Temp n) {
		Integer id = Integer.parseInt(n.f1.accept(this));
		
		if(!CurProc.tmpMap.containsKey(id)){		
			if(id < CurProc.nArg)
				CurProc.tmpMap.put(id, new Interval(id, 0, nStt));
			else CurProc.tmpMap.put(id, new Interval(id, nStt, nStt));
		}
					
		return id.toString();
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
