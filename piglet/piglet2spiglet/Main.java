package piglet.piglet2spiglet;


import piglet.ParseException;
import piglet.PigletParser;
import piglet.TokenMgrError;
import piglet.syntaxtree.Node;
import piglet.visitor.CountTmpVisitor;
import piglet.visitor.GJDepthFirst;
import piglet.visitor.Piglet2SpigletVisitor;


public class Main { 
 
    public static void main(String[] args) {
    	try {
    		Node root = new PigletParser(System.in).Goal();
    		
    		CountTmpVisitor v1 = new CountTmpVisitor();
    		root.accept(v1, null);
    		PrintSpiglet.nTmp = v1.nTmp;
    		
    		Piglet2SpigletVisitor v = new Piglet2SpigletVisitor();
    		root.accept(v, null);
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