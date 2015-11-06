/**
 * 对整个输入程序进行类型检查
 * 检查内容包括：变量与方法的重复定义，方法的重载错误，类的循环继承，赋值类型不匹配，方法调用参数不匹配，etc
 * @author Alfalfa
 * 2013.10.9
 */

package minijava.typecheck;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.symboltable.MClasses;
import minijava.symboltable.MType;
import minijava.syntaxtree.Node;
import minijava.visitor.BuildSymbolTableVisitor;
import minijava.visitor.CheckVisitor;

/**
 * 类型检查的主函数入口类
 */
public class Main {
	
	/**
	 * 类型检查入口函数
	 * @param args
	 */
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
			
			/* 只打印是否有错误，若错误列表大小大于零，则有错误，否则程序正确 */
			if(PrintMsg.PRINT_LEVEL == 0){
				if(PrintMsg.errors.size() > 0)
					System.out.println("Type error");
				else System.out.println("Program type checked successfully");
			}
				
			/* 打印符号表，传入符号表根结点以遍历 */
			if(PrintMsg.PRINT_LEVEL >= 2){
				PrintMsg.printSymbolTable((MClasses)my_classes);
			}
						
			/* 打印具体错误信息 */
			if(PrintMsg.PRINT_LEVEL >= 1)
				PrintMsg.printAll();
			
		} catch (TokenMgrError e) {

			// Handle Lexical Errors
			e.printStackTrace();
		} catch (ParseException e) {

			// Handle Grammar Errors
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}