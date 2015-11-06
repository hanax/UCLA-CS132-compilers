package minijava.symboltable;

/**
 * 符号表中的变量
 */
public class MVar extends MType {
	/* 变量类型 */
	public String type;
	/* 变量所属方法或类 */
	public MType owner;
	/* 是否初始化过 */
	public boolean isInited;
	/* 是否初使用过 */
	public boolean isUsed;

	/**
	 * 变量的构造函数
	 * @param v_name	变量名
	 * @param v_type	变量类型
	 * @param all		变量所属方法或类
	 * @param m_line	变量首次定义所在行
	 * @param m_column	变量首次定义所在列
	 */
	public MVar(String v_name, String v_type, MType all, int m_line, int m_column) {
		super(m_line, m_column);
		name = v_name;
		type = v_type;
		owner = all;
		isInited = false;
		isUsed = false;
	}
}

