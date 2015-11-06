package minijava.symboltable;

import java.util.Vector;

/**
 * 符号表中的方法
 */
public class MMethod extends MType {
	/* 方法所在类 */
	public MClass owner;
	/* 方法返回类型 */
	public String type;
	/* 存放方法参数 */
	public Vector<MVar> mj_para = new Vector();
	/* 存放方法中定义的变量(不包括方法所在类的成员变量与其父类的成员变量) */
	public Vector<MVar> mj_var = new Vector();

	/**
	 * 方法的构造函数
	 * @param v_name	方法名
	 * @param v_type	方法的返回类型
	 * @param all		方法所在类，即符号表中的父结点
	 * @param m_line	方法首次定义所在行
	 * @param m_column	方法首次定义所在列
	 */
	public MMethod(String v_name, String v_type, MClass all, int m_line, int m_column) {
		super(m_line, m_column);
		name = v_name;
		type = v_type;
		owner = all;
	}

	/**
	 * 在方法中插入参数
	 * @param v_para	需要插入的MVar类型参数
	 * @return			若插入成功（无重复定义）则返回null，否则返回错误信息
	 */
	public String InsertPara(MVar v_para) { 
		String para_name = v_para.getName();
		if (Repeated_var(para_name)) // 如已经定义过该类，返回错误信息
			return "PARA DOUBLE DECLARATION " + "[" + para_name + "]";
		mj_para.addElement(v_para);
		return null;
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
	 * 查找方法中是否已有同名变量定义（查找类中变量与参数列表）
	 * @param var_name	所需查找的变量名
	 * @return			若已有重复变量，则返回true，否则返回false
	 */
	public boolean Repeated_var(String var_name) {
		int sz = mj_var.size();
		for (int i = 0; i < sz; i++) {
			String v_name = ((MVar) mj_var.elementAt(i)).getName();
			if (v_name.equals(var_name))
				return true;
		}
		int sz2 = mj_para.size();
		for (int i = 0; i < sz2; i++) {
			String p_name = ((MVar) mj_para.elementAt(i)).getName();
			if (p_name.equals(var_name))
				return true;
		}
		return false;
	}
	
	/**
	 * 根据变量名字，返回在vector:mj_var中的下标
	 * @param var_name 	所需查找的变量名字
	 * @return			变量在vector:mj_var中的下标，若不存在则返回-1
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
	 * 根据参数名字，返回在vector:mj_var中的下标
	 * @param para_name 	所需查找的参数名字
	 * @return				参数在vector:mj_var中的下标，若不存在则返回-1
	 */
	public int getParaIndex(String para_name){
		int sz = mj_para.size();
		for (int i = 0; i < sz; i++) {
			String c_name = ((MVar) mj_para.elementAt(i)).getName();
			if (c_name.equals(para_name))
				return i;
		}
		return -1;
	}
	
	/**
	 * 根据变量名字，查找自己的变量，自己的参数，所在类的成员变量，与所在类的父类们的变量，返回作用域最近的变量
	 * @param var_name 	所需查找的变量名字
	 * @return			返回查找到的变量，若不存在则返回null
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
		// 找方法参数变量
		int sz2 = mj_para.size();
		for (int i = 0; i < sz2; i++) {
			String c_name = ((MVar) mj_para.elementAt(i)).getName();
			if (c_name.equals(var_name))
				return mj_para.elementAt(i);
		}
		// 找类成员变量及父类们的成员变量
		MClass o = owner;
		while(o!=null && !o.getName().equals("") && !o.getName().equals("Object")){
			//System.out.println(o.getName());
			int szo = o.mj_var.size();
			for (int i = 0; i < szo; i++) {
				String c_name = ((MVar) o.mj_var.elementAt(i)).getName();
				if (c_name.equals(var_name))
					return o.mj_var.elementAt(i);
			}
			o = o.all_classes.getClass(o.father);
		}
		return v;
	}
}