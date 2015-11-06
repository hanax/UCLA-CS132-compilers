package minijava.symboltable;

import java.util.Vector;

/**
 * 存放函数的参数列表
 * 用于检查函数调用时参数是否匹配
 */
public class MArgs extends MType{
	public Vector<MType> mj_args = new Vector();
	public MType argu;
	public MArgs(MType m_argu){
		argu = m_argu;
	}
	public MArgs(){
		argu = null;
	}
}
