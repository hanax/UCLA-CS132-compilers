package minijava.symboltable;

import java.util.HashMap;
import java.util.Vector;

/**
 * 符号表中所有类的父结点（不是父类）
 */
public class MClasses extends MType {
	/* 存放类的vector */
	public Vector<MClass> mj_classes = new Vector<MClass>();

	/**
	 * 插入类
	 * @param v_class	需要插入的MClass类型类
	 * @return			若插入成功（无重复定义）则返回null，否则返回错误信息
	 */
	public String InsertClass(MClass v_class) { 
		String class_name = v_class.getName();
		if (Repeated(class_name)) // 如已经定义过该类，返回错误信息
			return "CLASS DOUBLE DECLARATION " + "[" + class_name + "]";
		mj_classes.addElement(v_class);
		return null;
	}

	/**
	 * 查找是否已有同名类
	 * @param class_name	所需查找的类
	 * @return				若已有重复类，则返回true，否则返回false
	 */
	public boolean Repeated(String class_name) {
		int sz = mj_classes.size();
		for (int i = 0; i < sz; i++) {
			String c_name = ((MClass) mj_classes.elementAt(i)).getName();
			if (c_name.equals(class_name))
				return true;
		}
		return false;
	}
	
	/**
	 * 根据类名，返回在vector:mj_classes中的下标
	 * @param class_name 	所需查找的类名字
	 * @return				变量在vector:mj_classes中的下标，若不存在则返回-1
	 */
	public int getIndex(String class_name){
		int sz = mj_classes.size();
		for (int i = 0; i < sz; i++) {
			String c_name = ((MClass) mj_classes.elementAt(i)).getName();
			if (c_name.equals(class_name))
				return i;
		}
		return -1;
	}
	
	/**
	 * 根据类名，查找该类
	 * @param class_name 	所需查找的类名
	 * @return				返回查找到的类，若不存在则返回null
	 */
	public MClass getClass(String class_name){
		MClass c = null;
		int sz = mj_classes.size();
		for (int i = 0; i < sz; i++) {
			String c_name = ((MClass) mj_classes.elementAt(i)).getName();
			if (c_name.equals(class_name))
				return mj_classes.elementAt(i);
		}
		return c;
	}
}

