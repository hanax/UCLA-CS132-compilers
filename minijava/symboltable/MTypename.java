package minijava.symboltable;

/**
 * 用于在visitor中传送所有不再符号表中存在的结点，形如"true","(1+1)","New A()"等形式
 * 主要任务为自下而上传递类型
 */
public class MTypename extends MType {
	/* 字符串类型的类型值 */
	public String type;
	/* 结点的值，类型检查用不到只是顺便存一存 */
	public String val;
	public MTypename(String v_type, String v_val, int m_line, int m_column) {
		super(m_line, m_column);
		type = v_type;
		if(!v_val.equals(""))
			name = v_val;
		else name = v_type;
		val = v_val;
	}
	public MTypename(String v_type) {
		name = v_type;
		type = v_type;
	}
}

