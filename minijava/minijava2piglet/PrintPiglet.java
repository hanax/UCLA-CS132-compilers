package minijava.minijava2piglet;

import minijava.symboltable.MClass;
import minijava.visitor.Minijava2PigletVisitor;

public class PrintPiglet {	
	private static int nTab = 0;
	private static boolean enter = false;

	public static void p(String s) {
		if(enter){
			System.out.println();
			//System.out.println(nTab);
			for(int i = 0; i < nTab; i ++) System.out.print("  ");
		} enter = false;
		System.out.printf(s);
	}
	public static void pln(String s) {
		p(s);
		enter = true;
	}
	public static void pBegin() {
		pln("BEGIN");
		nTab ++;
	}
	public static void pEnd() {
		p("END ");
	}
	public static void pReturn() {
		p("RETURN ");
		nTab --;
	}
	public static void pMain() {
		nTab ++;
		pln("MAIN");
	}
	public static void pProcedure(String class_name, String method_name, int nPara) {
		pln(class_name + "_" + method_name + " [" + nPara + "] ");
		nTab ++;
	}
	public static void pEndProcedure() {
		pln("END");
		nTab --;
		pln("");
	}
	
	public static void printPigletSymbolTable(){
		for(int i = 0; i < Minijava2PigletVisitor.all_classes.mj_classes.size(); i ++){
			MClass c = Minijava2PigletVisitor.all_classes.mj_classes.elementAt(i);
			System.out.println(c.getName());
			System.out.println("-- Method --");
			for(int j = 0; j < Minijava2PigletVisitor.methodMap.get(c.getName()).size(); j ++){
				System.out.println("\t" + Minijava2PigletVisitor.methodMap.get(c.getName()).elementAt(j));
			}
			System.out.println("-- Var --");
			for(int j = 0; j < Minijava2PigletVisitor.varMap.get(c.getName()).size(); j ++){
				System.out.println("\t" + Minijava2PigletVisitor.varMap.get(c.getName()).elementAt(j));
			}
			System.out.println();
		}
	}

}
