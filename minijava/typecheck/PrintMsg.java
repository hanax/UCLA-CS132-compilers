package minijava.typecheck;

import java.util.Vector;
import minijava.symboltable.*;

/**
 * 输出类
 * 存放错误信息、输出错误信息、输出符号表
 * 以后所有的输出动作都放在这里
 */
public class PrintMsg {
	/* 存放错误信息字符串的向量 */
	public static Vector<String> errors = new Vector<String>();
	/** 
	 * PRINT_LEVEL = 0: 简洁模式  只输出是否有错误(用于自动测试);
	 * PRINT_LEVEL = 1: 正常模式  输出具体错误信息
	 * PRINT_LEVEL = 2: 调试模式  输出具体错误信息以外，还输出整个符号表，还输出使用未初始化变量信息，还输出定义变量却未使用的警告
	 */
	public static int PRINT_LEVEL = 0;
	
	/**
	 * 将错误信息补全行列格式存入vector:string中
	 * @param line		错误出现所在行
	 * @param column	错误出现所在列
	 * @param error_msg	错误信息
	 */
	public static void print(int line, int column, String error_msg) {
		String msg = "Line " + line + " Column " + column + ": " + error_msg;
		errors.addElement(msg); // 存储错误信息
	}

	/**
	 * 打印具体错误信息，输出vectors:string的内容
	 */
	public static void printAll() {
		int sz = errors.size();
		for (int i = 0; i < sz; i++) {
			System.out.println(errors.elementAt(i));
		}
	}
	
	/**
	 * 按层次结构深度优先遍历打印整张符号表
	 * @param root	符号表根节点，即所有类的父结点
	 */
	public static void printSymbolTable(MClasses root) {
		for(int i = 0; i < root.mj_classes.size(); i ++){
			MClass m_class = root.mj_classes.elementAt(i);
			System.out.print("Class: " + m_class.getName());
			if(!m_class.father.equals("") && !m_class.father.equals("Object")){
				System.out.print(" with father Class: " + m_class.father);
			}
			System.out.println();
			for(int j = 0; j < m_class.mj_var.size();j ++){
				System.out.println("\tVariable: " + m_class.mj_var.elementAt(j).getName() + " (" + m_class.mj_var.elementAt(j).type + ")");
			}
			for(int j = 0; j < m_class.mj_method.size();j ++){
				MMethod m_method = m_class.mj_method.elementAt(j);
				System.out.println("\tMethod: " + m_method.getName());
				System.out.println("\t\tReturn Type: " + m_method.type);
				if(m_method.mj_para.size() == 0)
					System.out.println("\t\tParam: void");
				for(int k = 0; k < m_method.mj_para.size(); k ++){
					System.out.println("\t\tParam: " + m_method.mj_para.elementAt(k).getName());
				}
				for(int k = 0; k < m_method.mj_var.size(); k ++){
					System.out.println("\t\tVariable: " + m_method.mj_var.elementAt(k).getName() + " (" + m_method.mj_var.elementAt(k).type + ")");
				}
			}
		}
		System.out.println();
	}
}

