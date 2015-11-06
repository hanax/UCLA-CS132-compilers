package minijava.minijava2piglet;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.symboltable.MClasses;
import minijava.symboltable.MType;
import minijava.syntaxtree.Node;
import minijava.typecheck.PrintMsg;
import minijava.visitor.BuildSymbolTableVisitor;
import minijava.visitor.CheckVisitor;
import minijava.visitor.GJDepthFirst;
import minijava.visitor.Minijava2PigletVisitor;


public class Main { 
 
    public static void main(String[] args) {
    	try {
    		new MiniJavaParser(System.in);
			Node root = MiniJavaParser.Goal();

			/* 初始化符号表中最大的类 */
			MType my_classes = new MClasses();

			/* 建立符号表，检查重复定义的错误，传入BuildSymbolTableVisitor */
			root.accept(new BuildSymbolTableVisitor(), my_classes);
			
			/* 建立完符号表后，检查其余错误，传入CheckVisitor */
			root.accept(new CheckVisitor(), my_classes);
			
			/* 符号检查，打印是否有错误*/
			if(PrintMsg.errors.size() > 0)
				PrintMsg.printAll();
			
			//PrintMsg.printSymbolTable((MClasses) my_classes);
			
			/* 程序没错误时，进行下一步MiniJava to Piglet操作 */
			if(PrintMsg.errors.size() == 0)
				root.accept(new Minijava2PigletVisitor(), my_classes);
			
			//PrintPiglet.printPigletSymbolTable();
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