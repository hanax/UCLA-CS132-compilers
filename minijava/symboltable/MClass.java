package minijava.symboltable;

import java.util.Vector;

/**
 * 符号表中的类
 */
public class MClass extends MType {
	/* 所有类的列表 */
	public MClasses all_classes;
	/* 类的父类名字，若无父类则遵从java规定，定义其缺省父类为"Object" */
	public String father;
	/* 存放类中定义的成员变量(不包括方法中定义的变量与其父类的成员变量) */
	public Vector<MVar> mj_var = new Vector();
	/* 存放类中定义的成员函数(不包括父类的成员函数) */
	public Vector<MMethod> mj_method = new Vector();

	/**
	 * 类的构造函数
	 * @param v_name	类名
	 * @param all		所有类的列表，用于提供查找其他类的入口
	 * @param m_line	类首次定义所在行
	 * @param m_column	类首次定义所在列
	 */
	public MClass(String v_name, MClasses all, int m_line, int m_column) {
		super(m_line, m_column);
		name = v_name;
		all_classes = all;
	}
	
	/**
	 * 在类中插入成员变量
	 * @param v_var	需要插入的MVar类型成员变量
	 * @return		若插入成功（无重复定义）则返回null，否则返回错误信息
	 */
	public String InsertVar(MVar v_var) { 
		String var_name = v_var.getName();
		if (Repeated_var(var_name)) // 如已经定义过该类，返回错误信息
			return "VARIABLE DOUBLE DECLARATION " + "[" + var_name + "]";
		mj_var.addElement(v_var);
		return null;
	}

	/**
	 * 查找类中是否已有同名变量定义
	 * @param var_name	所需查找的变量
	 * @return			若已有重复变量，则返回true，否则返回false
	 */
	public boolean Repeated_var(String var_name) {
		int sz = mj_var.size();
		for (int i = 0; i < sz; i++) {
			String v_name = ((MVar) mj_var.elementAt(i)).getName();
			if (v_name.equals(var_name))
				return true;
		}
		return false;
	}

	/**
	 * 在类中插入成员函数
	 * @param v_method	需要插入的MMethod类型成员函数
	 * @return			若插入成功（无重复定义）则返回null，否则返回错误信息
	 */
	public String InsertMethod(MMethod v_method) { 
		String method_name = v_method.getName();
		if (Repeated_method(method_name)) // 如已经定义过该类，返回错误信息
			return "METHOD DOUBLE DECLARATION " + "[" + method_name + "]";
		mj_method.addElement(v_method);
		return null;
	}

	/**
	 * 查找类中是否已有同名函数定义
	 * @param method_name	所需查找的函数
	 * @return				若已有重复函数，则返回true，否则返回false
	 */
	public boolean Repeated_method(String method_name) {
		int sz = mj_method.size();
		for (int i = 0; i < sz; i++) {
			String m_name = ((MMethod) mj_method.elementAt(i)).getName();
			if (m_name.equals(method_name))
				return true;
		}
		return false;
	}
	
	/**
	 * 根据变量名字，返回在vector:mj_var中的下标
	 * @param 	所需查找的变量名字
	 * @return	变量在vector:mj_var中的下标，若不存在则返回-1
	 */
	public int getVarIndex(String var_name){
		int sz = mj_var.size();
		for (int i = 0; i < sz; i++) {
			String c_name = ((MVar) mj_var.elementAt(i)).getName();
			if (c_name.equals(var_name))
				return i;
		}
		return -1;
	}
	
	/**
	 * 根据方法名字，返回在vector:mj_method中的下标
	 * @param 	所需查找的方法名字
	 * @return	函数在vector:mj_method中的下标，若不存在则返回-1
	 */
	public int getMethodIndex(String method_name){
		int sz = mj_method.size();
		for (int i = 0; i < sz; i++) {
			String c_name = ((MMethod) mj_method.elementAt(i)).getName();
			if (c_name.equals(method_name))
				return i;
		}
		return -1;
	}
	
	/**
	 * 根据方法名字，查找自己的方法与父类们的方法，返回作用域最近的方法
	 * @param 	所需查找的方法名字
	 * @return	返回查找到的方法，若不存在则返回null
	 */
	public MMethod getMethod(String method_name){
		//按作用域由近到远查找方法
		MMethod m = null;
		//查找自己的方法
		int sz = mj_method.size();
		for (int i = 0; i < sz; i++) {
			String c_name = ((MMethod) mj_method.elementAt(i)).getName();
			if (c_name.equals(method_name))
				return mj_method.elementAt(i);
		}
		//查找父类们的方法
		String f = father;
		while(!f.equals("") && !f.equals("Object")){
			int szo = all_classes.getClass(f).mj_method.size();
			for (int i = 0; i < szo; i++) {
				String c_name = ((MMethod) all_classes.getClass(f).mj_method.elementAt(i)).getName();
				if (c_name.equals(method_name))
					return all_classes.getClass(f).mj_method.elementAt(i);
			}
			f = all_classes.getClass(f).father;
		}
		return m;
	}

	/**
	 * 根据变量名字，查找自己的变量与父类们的变量，返回作用域最近的变量
	 * @param 	所需查找的变量名字
	 * @return	返回查找到的变量，若不存在则返回null
	 */
	public MVar getVar(String var_name){
		// 按作用域由近到远查找变量
		MVar v = null;
		// 找方法内变量
		int sz = mj_var.size();
		for (int i = 0; i < sz; i++) {
			String c_name = ((MVar) mj_var.elementAt(i)).getName();
			if (c_name.equals(var_name))
				return mj_var.elementAt(i);
		}
		// 找类成员变量及父类们的成员变量
		String f = father;
		while(!f.equals("") && !f.equals("Object")){
			int szo = all_classes.getClass(f).mj_var.size();
			for (int i = 0; i < szo; i++) {
				String c_name = ((MVar) all_classes.getClass(f).mj_var.elementAt(i)).getName();
				if (c_name.equals(var_name))
					return all_classes.getClass(f).mj_var.elementAt(i);
			}
			f = all_classes.getClass(f).father;
		}
		return v;
	}
}

