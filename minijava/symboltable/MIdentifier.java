package minijava.symboltable;

/**
 * 用于在visitor中传送所有已在符号表中存在的变量名
 */
public class MIdentifier extends MType {
	public MIdentifier(String v_name, int v_line, int v_column) {
		super(v_line, v_column);
		name = v_name;
	}
}

