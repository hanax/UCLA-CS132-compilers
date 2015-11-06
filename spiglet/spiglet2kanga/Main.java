package spiglet.spiglet2kanga;

import java.util.HashMap;
import spiglet.AssignTMP;
import spiglet.ParseException;
import spiglet.ProcBlock;
import spiglet.SpigletParser;
import spiglet.TokenMgrError;
import spiglet.syntaxtree.Node;
import spiglet.visitor.BuildGraphVisitor;
import spiglet.visitor.BuildVertexVisitor;
import spiglet.visitor.Spiglet2KangaVisitor;

public class Main { 
    public static void main(String[] args) {
    	try {
    		Node root = new SpigletParser(System.in).Goal();
    		
    		HashMap<String, ProcBlock> ProcMap = new HashMap<String, ProcBlock>();
    		HashMap<String, Integer> LabelMap = new HashMap<String, Integer>();
    		
			root.accept(new BuildVertexVisitor(ProcMap, LabelMap));
			root.accept(new BuildGraphVisitor(ProcMap, LabelMap));
    		
			AssignTMP assign_tmp = new AssignTMP(ProcMap);
			assign_tmp.assign();
			
			root.accept(new Spiglet2KangaVisitor(ProcMap));

			/*
			for(ProcBlock p: ProcMap.values()){
				System.out.println(p.procName);
				for(Integer i: p.tmpMap.keySet())
					System.out.println("  TEMP " + i + " (" + p.tmpMap.get(i).begin + ", " + p.tmpMap.get(i).end + ")" + p.tmpMap.get(i).isS);
				for(Integer i: p.graph.sCallPos)
					System.out.println("  Stt " + i);
				for(myVertex v: p.graph.vNode){
					
					System.out.print("  " + v.nStt + " Left: ");
					for(Integer a: v.left)
						System.out.print("TEMP " + a + "; ");
					System.out.println();
					
					System.out.print("    Right: ");
					for(Integer a: v.right)
						System.out.print("TEMP " + a + "; ");
					System.out.println();

					System.out.print("    Pre: ");
					for(myVertex a: v.pre)
						System.out.print("Stt " + a.nStt + "; ");
					System.out.println();

					System.out.print("    Next: ");
					for(myVertex a: v.next)
						System.out.print("Stt " + a.nStt + "; ");
					System.out.println();

					System.out.print("    In: ");
					for(Integer a: v.in)
						System.out.print("TEMP " + a + "; ");
					System.out.println();

					System.out.print("    Out: ");
					for(Integer a: v.out)
						System.out.print("TEMP " + a + "; ");
					System.out.println();
				}
			}System.out.println();*/
			
			
    	}
    	catch(TokenMgrError e){
    		//Handle Lexical Errors
    		e.printStackTrace();
    	}
    	catch (ParseException e){
    		//Handle Grammar Errors
    		e.printStackTrace();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
}