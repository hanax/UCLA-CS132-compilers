package spiglet.visitor;

import java.util.Enumeration;
import java.util.HashMap;
import spiglet.ProcBlock;
import spiglet.myGraph.myVertex;
import spiglet.syntaxtree.*;

/**
 * BuildGraphVisitor
 * 
 * Build the following:
 * - Edges between Vertexes
 */

public class BuildGraphVisitor extends GJNoArguDepthFirst<String> {
  
  private HashMap<String, ProcBlock> ProcMap;
  private HashMap<String, Integer> LabelMap;
  private ProcBlock CurProc;
  private myVertex CurVertex;
  private int nStt;
  private boolean duringCall;
  
  public BuildGraphVisitor(HashMap<String, ProcBlock> _ProcMap, HashMap<String, Integer> _LabelMap) {
    this.ProcMap = _ProcMap;
    this.nStt = 0;
    this.LabelMap = _LabelMap;
    this.duringCall = false;
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
	CurProc = ProcMap.get("MAIN");
	
	nStt = 0;
	CurProc.graph.addEdge(nStt, nStt+1);
	nStt ++;	//BEGIN
	
    n.f1.accept(this);
    
	//END (useless)
	
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
	CurProc = ProcMap.get(n.f0.f0.toString());
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
	CurVertex = CurProc.graph.getVertex(nStt);
    n.f0.accept(this);
	nStt ++;
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
	CurProc.graph.addEdge(nStt, nStt+1);
	nStt ++;	//BEGIN
    n.f1.accept(this);
	
    n.f3.accept(this);
	
	CurProc.graph.addEdge(nStt, nStt+1);
	//RETURN - END (useless)
    return null;
  }
 
  /**
   * f0 -> "NOOP"
   */
  public String visit(NoOpStmt n) {
     CurProc.graph.addEdge(nStt, nStt+1);
     return null;
  }

  /**
   * f0 -> "ERROR"
   */
  public String visit(ErrorStmt n) {
	 CurProc.graph.addEdge(nStt, nStt+1);
     return null;
  }

  /**
   * f0 -> "CJUMP"
   * f1 -> Temp()
   * f2 -> Label()
   */
  public String visit(CJumpStmt n) {     
     CurVertex.right.add(Integer.parseInt(n.f1.accept(this)));
	 CurProc.graph.addEdge(nStt, nStt+1);
	 CurProc.graph.addEdge(nStt, LabelMap.get(n.f2.accept(this)));
     return null;
  }

  /**
   * f0 -> "JUMP"
   * f1 -> Label()
   */
  public String visit(JumpStmt n) {
	 CurProc.graph.addEdge(nStt, LabelMap.get(n.f1.accept(this)));
     return null;
  }

  /**
   * f0 -> "HSTORE"
   * f1 -> Temp()
   * f2 -> IntegerLiteral()
   * f3 -> Temp()
   */
  public String visit(HStoreStmt n) {
     CurVertex.right.add(Integer.parseInt(n.f1.accept(this)));
     CurVertex.right.add(Integer.parseInt(n.f3.accept(this)));
	 CurProc.graph.addEdge(nStt, nStt+1);
     return null;
  }

  /**
   * f0 -> "HLOAD"
   * f1 -> Temp()
   * f2 -> Temp()
   * f3 -> IntegerLiteral()
   */
  public String visit(HLoadStmt n) {
	 CurVertex.left.add(Integer.parseInt(n.f1.accept(this)));
	 CurVertex.right.add(Integer.parseInt(n.f2.accept(this)));
	 CurProc.graph.addEdge(nStt, nStt+1);
     return null;
  }

  /**
   * f0 -> "MOVE"
   * f1 -> Temp()
   * f2 -> Exp()
   */
  public String visit(MoveStmt n) {
     CurVertex.left.add(Integer.parseInt(n.f1.accept(this)));
	 CurProc.graph.addEdge(nStt, nStt+1);
     n.f2.accept(this);
     return null;
  }

  /**
   * f0 -> "PRINT"
   * f1 -> SimpleExp()
   */
  public String visit(PrintStmt n) {
     n.f1.accept(this);
	 CurProc.graph.addEdge(nStt, nStt+1);
     return null;
  }

  /**
   * f0 -> Call()
   *       | HAllocate()
   *       | BinOp()
   *       | SimpleExp()
   */
  public String visit(Exp n) {
    return n.f0.accept(this);
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
     duringCall = true;
     n.f3.accept(this);
     duringCall = false;
     return null;
  }

  /**
   * f0 -> "HALLOCATE"
   * f1 -> SimpleExp()
   */
  public String visit(HAllocate n) {
     return n.f1.accept(this);
  }

  /**
   * f0 -> Operator()
   * f1 -> Temp()
   * f2 -> SimpleExp()
   */
  public String visit(BinOp n) {
     n.f0.accept(this);
     CurVertex.right.add(Integer.parseInt(n.f1.accept(this)));
     n.f2.accept(this);
     return null;
  }

  /**
   * f0 -> "LT"
   *       | "PLUS"
   *       | "MINUS"
   *       | "TIMES"
   */
  public String visit(Operator n) {
     return null;
  }

  /**
   * f0 -> Temp()
   *       | IntegerLiteral()
   *       | Label()
   */
  public String visit(SimpleExp n) {
     String t = n.f0.accept(this);
     if (n.f0.which == 0)	//Temp
       CurVertex.right.add(Integer.parseInt(t));
     return null;
  }

  /**
   * f0 -> "TEMP"
   * f1 -> IntegerLiteral()
   */
  public String visit(Temp n) {
	String t = n.f1.accept(this);
	if(duringCall)
		CurVertex.right.add(Integer.parseInt(t));
    return t;
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
