package minijava.symboltable;

/**
 * 表示类型的类，symboltable包中所有类的父类
 */
public abstract class MType {
	/* 名称 */
	protected String name;
	/* 所在行 */
	protected int line = 0;
	/* 所在列 */
	protected int column = 0;

	public MType() {
	};

	public MType(int m_line, int m_column) {
		line = m_line;
		column = m_column;
	}

	public String getName() {
		return name;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public void setName(String v_name) {
		name = v_name;
	}

	public void setLine(int m_line) {
		line = m_line;
	}

	public void setColumn(int m_column) {
		column = m_column;
	}
}

