package minijava.visitor;

import java.util.HashMap;
import java.util.Vector;

import com.sun.corba.se.spi.activation._ActivatorImplBase;

import minijava.minijava2piglet.PrintPiglet;
import minijava.symboltable.*;
import minijava.syntaxtree.*;

public class Minijava2PigletVisitor extends GJDepthFirst<MType, MType> {
		
	private MMethod cur_method = null;
	private MClass cur_class = null;
	private int cur_tmp = 0;
	private int cur_label = 0;
	private int para_pos = 0;
	private boolean over_20 = false;
	private int tmptmp = -1;
	public static HashMap<String, Integer> tmpMap = new HashMap<String, Integer>();
	public static HashMap<String, Vector<String>> varMap = new HashMap<String, Vector<String>>();
	public static HashMap<String, Vector<String>> methodMap = new HashMap<String, Vector<String>>();
	public static MClasses all_classes;
	
	@SuppressWarnings("unchecked")
	private Vector<String> buildMethodMap(MClass m_class) { //展开继承链变量列表
		Vector<String> v;
		if (methodMap.containsKey(m_class.getName()))
			return methodMap.get(m_class.getName());
		
		if (all_classes.getClass(m_class.father) != null)
			v = (Vector<String>) buildMethodMap(all_classes.getClass(m_class.father)).clone();
		else 
			v = new Vector<String>();
		for (int i = 0; i < m_class.mj_method.size(); i ++) {
			MMethod methodId = m_class.mj_method.elementAt(i);
			if(v.contains(methodId.getName()))	//覆盖父类方法
				v.remove(methodId.getName());
			v.add(methodId.getName());
		}
		methodMap.put(m_class.getName(), v);
		return v;
	}
	
	private int getMethodPos(String class_name, String method_name) {
		Vector<String> v = methodMap.get(class_name);
		for (int i = v.size() - 1; i >= 0; i --) {
			if (v.get(i).equals(method_name)) {
				return i*4;	//address offset in memory
			}
		}
		return -2; //error (shouldn't happen)
	}

	@SuppressWarnings("unchecked")
	private Vector<String> buildVarMap(MClass m_class) { //展开继承链变量列表
		Vector<String> v;
		if (varMap.containsKey(m_class.getName()))
			return varMap.get(m_class.getName());
		
		if (all_classes.getClass(m_class.father) != null)
			v = (Vector<String>) buildVarMap(all_classes.getClass(m_class.father)).clone();
		else 
			v = new Vector<String>();
		for (int i = 0; i < m_class.mj_var.size(); i ++) {
			MVar varId = m_class.mj_var.elementAt(i);
			v.add(varId.getName());
		}
		varMap.put(m_class.getName(), v);
		return v;
	}
	
	private int getVarPos(String class_name, String var_name) {
		Vector<String> v = varMap.get(class_name);
		for (int i = v.size() - 1; i >= 0; i --) {
			if (v.get(i).equals(var_name)) {
				return (1 + i) * 4;	//address offset in memory
			}
		}
		return -1; //error (shouldn't happen)
	}
	
		/**
		* f0 -> "class"
		* f1 -> Identifier()
		* f2 -> "{"
		* f3 -> "public"
		* f4 -> "static"
		* f5 -> "void"
		* f6 -> "main"
		* f7 -> "("
		* f8 -> "String"
		* f9 -> "["
		* f10 -> "]"
		* f11 -> Identifier()
		* f12 -> ")"
		* f13 -> "{"
		* f14 -> PrintStatement()
		* f15 -> "}"
		* f16 -> "}"
		*/
	public MType visit(MainClass n, MType argu) {
		all_classes = ((MClasses) argu);
		for (int i = 0; i < ((MClasses) argu).mj_classes.size(); i ++) {
			MClass m_class = ((MClasses) argu).mj_classes.elementAt(i);
			buildVarMap(m_class);
			buildMethodMap(m_class);
		}
		
		cur_class = ((MClasses)argu).mj_classes.elementAt(0); //mainclass
		PrintPiglet.pMain();
		n.f14.accept(this, cur_class.mj_method.elementAt(0)); //main
		PrintPiglet.pEndProcedure();
		return null;
	}
	
		/**
		* f0 -> "class"
		* f1 -> Identifier()
		* f2 -> "{"
		* f3 -> ( VarDeclaration() )*
		* f4 -> ( MethodDeclaration() )*
		* f5 -> "}"
		*/
	public MType visit(ClassDeclaration n, MType argu) {
		String name = n.f1.accept(this, argu).getName();
		cur_class = ((MClasses) argu).getClass(name);
		n.f3.accept(this, cur_class);
		n.f4.accept(this, cur_class);
		return null;
	}
	
		/**
		* f0 -> "class"
		* f1 -> Identifier()
		* f2 -> "extends"
		* f3 -> Identifier()
		* f4 -> "{"
		* f5 -> ( VarDeclaration() )*
		* f6 -> ( MethodDeclaration() )*
		* f7 -> "}"
		*/
	public MType visit(ClassExtendsDeclaration n, MType argu) {
		String name = n.f1.accept(this, argu).getName();
		cur_class = ((MClasses) argu).getClass(name);
		n.f5.accept(this, cur_class);
		n.f6.accept(this, cur_class);
		return null;
	}

		/**
		* f0 -> "public"
		* f1 -> Type()
		* f2 -> Identifier()
		* f3 -> "("
		* f4 -> ( FormalParameterList() )?
		* f5 -> ")"
		* f6 -> "{"
		* f7 -> ( VarDeclaration() )*
		* f8 -> ( Statement() )*
		* f9 -> "return"
		* f10 -> Expression()
		* f11 -> ";"
		* f12 -> "}"
		*/
	public MType visit(MethodDeclaration n, MType argu) {
		String name = n.f2.accept(this,argu).getName();
		cur_method = ((MClass) argu).getMethod(name);
		tmpMap = new HashMap<String, Integer>();
		tmpMap.put("this", 0);
		int nTmp = 1;
		if(cur_method.mj_para.size() < 20)
			for(int i = 0; i < cur_method.mj_para.size(); i ++)
				tmpMap.put(cur_method.mj_para.elementAt(i).getName(), nTmp ++);
		else
			for(int i = 0; i < 18; i ++)
				tmpMap.put(cur_method.mj_para.elementAt(i).getName(), nTmp ++);
		if(nTmp == 19) nTmp ++;
		for(int i = 0; i < cur_method.mj_var.size(); i ++)
			tmpMap.put(cur_method.mj_var.elementAt(i).getName(), nTmp ++);
		cur_tmp = nTmp;
		
		PrintPiglet.pProcedure(cur_class.getName(), cur_method.getName(), cur_method.mj_para.size() + 1);
		PrintPiglet.pBegin();
		n.f8.accept(this, cur_method);
		PrintPiglet.pReturn();
		n.f10.accept(this, cur_method);
		PrintPiglet.pln("");
		PrintPiglet.pEndProcedure();
		
		return null;
	}
	
		/**
		* f0 -> Identifier()
		* f1 -> "="
		* f2 -> Expression()
		* f3 -> ";"
		*/
	public MType visit(AssignmentStatement n, MType argu) {
		String name = n.f0.accept(this, argu).getName();		

		if(tmpMap.get(name) != null){
			PrintPiglet.p("MOVE TEMP " + tmpMap.get(name) + " ");
		}else{
			PrintPiglet.p("HSTORE TEMP 0 " + getVarPos(cur_class.getName(), name) + " ");
		}
		n.f2.accept(this, argu);
		PrintPiglet.pln("");
		return null;
	}
	
		/**
		* f0 -> Identifier()
		* f1 -> "["
		* f2 -> Expression()
		* f3 -> "]"
		* f4 -> "="
		* f5 -> Expression()
		* f6 -> ";"
		*/
	public MType visit(ArrayAssignmentStatement n, MType argu) {
		String name = n.f0.accept(this, argu).getName();
		int t1 = cur_tmp ++;
		int t2 = cur_tmp ++;
		int t3 = cur_tmp ++;
		int l1 = cur_label ++;
		int l2 = cur_label ++;
		int l3 = cur_label ++;
		
		if(tmpMap.get(name) != null){
			PrintPiglet.pln("MOVE TEMP " + t1 + " TEMP " + tmpMap.get(name));
		}else{
			PrintPiglet.pln("HLOAD TEMP " + t1 + " TEMP 0 " + getVarPos(cur_class.getName(), name));
		}
		PrintPiglet.p("MOVE TEMP " + t2 + " ");
		n.f2.accept(this, argu);
		PrintPiglet.pln("");
		PrintPiglet.p("MOVE TEMP " + t1 + " PLUS TEMP " + t1 + " TIMES 4 PLUS 1 TEMP " + t2);
		PrintPiglet.p("HSTORE TEMP " + t1 + " 0 ");
		n.f5.accept(this, argu);
		PrintPiglet.pln("");
		return null;
	}
	
		/**
		* f0 -> "if"
		* f1 -> "("
		* f2 -> Expression()
		* f3 -> ")"
		* f4 -> Statement()
		* f5 -> "else"
		* f6 -> Statement()
		*/
	public MType visit(IfStatement n, MType argu) {
		int l1 = cur_label ++;
		int l2 = cur_label ++;
		
		PrintPiglet.p("CJUMP ");
		n.f2.accept(this, argu);
		PrintPiglet.pln("LABEL" + l1 + " ");
		n.f4.accept(this, argu);
		PrintPiglet.pln("JUMP LABEL" + l2);
		PrintPiglet.p("LABEL" + l1 + " ");
		n.f6.accept(this, argu);
		PrintPiglet.pln("LABEL" + l2 + " NOOP");

		return null;
	}
	
		/**
		* f0 -> "while"
		* f1 -> "("
		* f2 -> Expression()
		* f3 -> ")"
		* f4 -> Statement()
		*/
	public MType visit(WhileStatement n, MType argu) {
		int l1 = cur_label ++;
		int l2 = cur_label ++;
		
		PrintPiglet.p("LABEL" + l1 + " CJUMP ");
		n.f2.accept(this, argu);
		PrintPiglet.pln("LABEL" + l2 + " ");
		n.f4.accept(this, argu);
		PrintPiglet.pln("JUMP LABEL" + l1);
		PrintPiglet.pln("LABEL" + l2 + " NOOP");
		
		return null;
	}
	
		/**
		* f0 -> AndExpression()
		*		| CompareExpression()
		*		| PlusExpression()
		*		| MinusExpression()
		*		| TimesExpression()
		*		| ArrayLookup()
		*		| ArrayLength()
		*		| MessageSend()
		*		| PrimaryExpression()
		*/
	public MType visit(Expression n, MType argu) {
		return n.f0.accept(this, argu);
	}
	
		/**
		* f0 -> PrimaryExpression()
		* f1 -> "&&"
		* f2 -> PrimaryExpression()
		*/
	public MType visit(AndExpression n, MType argu) {
		int t = cur_tmp ++;
		int l = cur_label ++;
			
		PrintPiglet.pBegin();
		PrintPiglet.pln("MOVE TEMP " + t + " 0");
		PrintPiglet.p("CJUMP ");
		n.f0.accept(this, argu);
		PrintPiglet.pln("LABEL" + l + " ");
		PrintPiglet.p("CJUMP ");
		n.f2.accept(this, argu);
		PrintPiglet.pln("LABEL" + l);
		PrintPiglet.pln("MOVE TEMP " + t + " 1");
		PrintPiglet.pln("LABEL" + l + " NOOP");
		PrintPiglet.pReturn();
		PrintPiglet.pln("TEMP " + t);
		PrintPiglet.pEnd();
		
		return null;
	}
	
		/**
		* f0 -> PrimaryExpression()
		* f1 -> "<"
		* f2 -> PrimaryExpression()
		*/
	public MType visit(CompareExpression n, MType argu) {
		PrintPiglet.p("LT ");
		n.f0.accept(this, argu);
		n.f2.accept(this, argu);
		return null;
	}
	
		/**
		* f0 -> PrimaryExpression()
		* f1 -> "+"
		* f2 -> PrimaryExpression()
		*/
	public MType visit(PlusExpression n, MType argu) {
		PrintPiglet.p("PLUS ");
		
		n.f0.accept(this, argu);
		n.f2.accept(this, argu);
		return null;
	}
	
		/**
		* f0 -> PrimaryExpression()
		* f1 -> "-"
		* f2 -> PrimaryExpression()
		*/
	public MType visit(MinusExpression n, MType argu) {
		PrintPiglet.p("MINUS ");
		
		n.f0.accept(this, argu);
		n.f2.accept(this, argu);
		return null;
	}
	
		/**
		* f0 -> PrimaryExpression()
		* f1 -> "*"
		* f2 -> PrimaryExpression()
		*/
	public MType visit(TimesExpression n, MType argu) {
		PrintPiglet.p("TIMES ");
		
		n.f0.accept(this, argu);
		n.f2.accept(this, argu);
		return null;
	}
	
		/**
		* f0 -> PrimaryExpression()
		* f1 -> "["
		* f2 -> PrimaryExpression()
		* f3 -> "]"
		*/
	public MType visit(ArrayLookup n, MType argu) {
		int t1 = cur_tmp ++;
		int t2 = cur_tmp ++;
		
		PrintPiglet.pBegin();
		PrintPiglet.p("MOVE TEMP " + t1 + " ");
		n.f0.accept(this, argu);
		PrintPiglet.pln("");
		PrintPiglet.p("MOVE TEMP " + t2 + " ");
		n.f2.accept(this, argu);
		PrintPiglet.pln("");
		PrintPiglet.pln("MOVE TEMP " + t1 + " PLUS TEMP " + t1 + " TIMES 4 PLUS 1 TEMP " + t2);
		PrintPiglet.pln("HLOAD TEMP " + t1 + " TEMP " + t1 + " 0");
		PrintPiglet.pReturn();
		PrintPiglet.pln("TEMP " + t1);
		PrintPiglet.pEnd();
		return null;
	}

		/**
		* f0 -> PrimaryExpression()
		* f1 -> "."
		* f2 -> "length"
		*/
	public MType visit(ArrayLength n, MType argu) {
		int t1 = cur_tmp ++;
		int t2 = cur_tmp ++;
		int l1 = cur_label ++;
		
		PrintPiglet.pBegin();
		PrintPiglet.p("MOVE TEMP " + t1 + " ");
		n.f0.accept(this, argu);
		PrintPiglet.pln("");
		PrintPiglet.pln("HLOAD TEMP " + t2 + " TEMP " + t1 + " 0");
		PrintPiglet.pReturn();
		PrintPiglet.pln("TEMP " + t2);
		PrintPiglet.pEnd();
		return null;
	}
		
		/**
		* f0 -> PrimaryExpression()
		* f1 -> "."
		* f2 -> Identifier()
		* f3 -> "("
		* f4 -> ( ExpressionList() )?
		* f5 -> ")"
		*/
	public MType visit(MessageSend n, MType argu) {
		int t1 = cur_tmp ++;
		int t2 = cur_tmp ++;
		int l1 = cur_label ++;
		
		PrintPiglet.pln("CALL");
		PrintPiglet.pBegin();
		PrintPiglet.p("MOVE TEMP " + t1 + " ");
		MType m_class = n.f0.accept(this, argu); //return class
		PrintPiglet.pln("");

		String method_name = n.f2.accept(this, argu).getName();
		MMethod methodId = all_classes.getClass(m_class.getName()).getMethod(method_name);
		
		PrintPiglet.pln("HLOAD TEMP " + t2 + " TEMP " + t1 + " 0");
		PrintPiglet.pln("HLOAD TEMP " + t2 + " TEMP " + t2 + " " + getMethodPos(m_class.getName(), method_name));
		PrintPiglet.pReturn();
		PrintPiglet.pln("TEMP " + t2);
		PrintPiglet.pEnd();
		PrintPiglet.p("(TEMP " + t1 + " ");
		n.f4.accept(this, methodId);
		PrintPiglet.pln(")");

		return all_classes.getClass(((MMethod) methodId).type);	//in case of "(A.a()).a()"
	}
		
		/**
		* f0 -> Expression()
		* f1 -> ( ExpressionRest() )*
		*/
	public MType visit(ExpressionList n, MType argu) {
			// TODO more than 20 paras
			// TMP 0: class addr | 
			// TMP 1 ~ TMP 18: arg 1 ~ arg 18 |
			// TMP 19 : begin addr of arg 19 ...
			// arg array: length | arg 19 | arg 20 | arg 21 | ...
		if(((MMethod) argu).mj_para.size() >= 20){
			over_20 = true;
			para_pos = 1;
		}
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		over_20 = false;
		para_pos = -1;
		return null;
	}
	
		/**
		* f0 -> ","
		* f1 -> Expression()
		*/
	public MType visit(ExpressionRest n, MType argu) {
		MType _ret = null;
		para_pos ++;
		if(over_20){
			if(para_pos == 19){	//开始构造参数数组
				int t1 = cur_tmp ++;
				int t2 = cur_tmp ++;
				int nParaLeft = ((MMethod) argu).mj_para.size() - 18;
				PrintPiglet.pln("");
				PrintPiglet.pBegin();
				PrintPiglet.pln("MOVE TEMP " + t1 + " " + nParaLeft);
				PrintPiglet.pln("MOVE TEMP " + t2 + " HALLOCATE TIMES 4 PLUS 1 TEMP " + t1);
				PrintPiglet.pln("HSTORE TEMP " + t2 + " 0 TEMP " + t1);
				PrintPiglet.p("HSTORE TEMP " + t2 + " 4 ");
				n.f1.accept(this, argu);
				PrintPiglet.pln("");
				tmptmp = t2;
			}else if(para_pos > 19){
				PrintPiglet.p("HSTORE TEMP " + tmptmp + " " + (4*(para_pos-18)) + " ");
				n.f1.accept(this, argu);
				PrintPiglet.pln("");
			}else{
				n.f1.accept(this, argu);
			}
			if(para_pos == ((MMethod) argu).mj_para.size()){
				PrintPiglet.pReturn();
				PrintPiglet.pln("TEMP " + tmptmp);
				PrintPiglet.pEnd();
			}
		}else _ret = n.f1.accept(this, argu);
		return _ret;
	}
		
		/**
		* f0 -> IntegerLiteral()
		*		| TrueLiteral()
		*		| FalseLiteral()
		*		| Identifier()
		*		| ThisExpression()
		*		| ArrayAllocationExpression()
		*		| AllocationExpression()
		*		| NotExpression()
		*		| BracketExpression()
		*/
	public MType visit(PrimaryExpression n, MType argu) {
		MType _ret = n.f0.accept(this, argu);			
		if(_ret instanceof MIdentifier){	// 标识符
			String name = _ret.getName();
			if(tmpMap.get(name) != null){	// in TMP
				PrintPiglet.p("TEMP " + tmpMap.get(name) + " ");
				MVar varId = ((MMethod) cur_method).getVar(name);
				String retTypeName = varId.type;
				_ret = all_classes.getClass(retTypeName);

			}else if(((MClass) cur_class).getVar(name) != null){	// not in TMP (in Class)
				int t = cur_tmp ++;
				PrintPiglet.pBegin();
				PrintPiglet.pln("HLOAD TEMP " + t + " TEMP 0 " + getVarPos(cur_class.getName(), name));
				PrintPiglet.pReturn();
				PrintPiglet.pln("TEMP " + t);
				PrintPiglet.pEnd();
				_ret = all_classes.getClass(((MClass) cur_class).getVar(name).type);
				
			}else{
				//TEMP 19: 参数数组首地址
				int t1 = cur_tmp ++;	
				if(t1 == 19) t1 = cur_tmp ++;
				PrintPiglet.pBegin();
				PrintPiglet.pln("MOVE TEMP " + t1 + " TEMP 19");
				int pos = (cur_method.getParaIndex(name) - 18 + 1) * 4;
				PrintPiglet.pln("MOVE TEMP " + t1 + " PLUS TEMP " + t1 + " " + pos);
				PrintPiglet.pln("HLOAD TEMP " + t1 + " TEMP " + t1 + " 0");
				PrintPiglet.pReturn();
				PrintPiglet.pln("TEMP " + t1);
				PrintPiglet.pEnd();
			}
		}
		return _ret;
	}
		
		/**
		* f0 -> <INTEGER_LITEMRAL>
		*/
	public MType visit(IntegerLiteral n, MType argu) {
		PrintPiglet.p(n.f0.toString() + " ");
		return null;
	}
		
		/**
		* f0 -> "true"
		*/
	public MType visit(TrueLiteral n, MType argu) {
		PrintPiglet.p("1 ");
		return null;
	}

		/**
		* f0 -> "false"
		*/
	public MType visit(FalseLiteral n, MType argu) {
		PrintPiglet.p("0 ");
		return null;
	}
		
		/**
		* f0 -> <IDENTIFIER>
		*/
	public MType visit(Identifier n, MType argu) {
		return new MIdentifier(n.f0.toString(), -1, -1);
	}
	
		/**
		* f0 -> "this"
		*/
	public MType visit(ThisExpression n, MType argu) {
		PrintPiglet.p("TEMP 0 ");
		return cur_class;
	}
		
		/**
		* f0 -> "System.out.println"
		* f1 -> "("
		* f2 -> Expression()
		* f3 -> ")"
		* f4 -> ";"
		*/
	public MType visit(PrintStatement n, MType argu) {
		PrintPiglet.p("PRINT ");
		n.f2.accept(this, argu);
		PrintPiglet.pln("");
		return null;
	}
		
		/**
		* f0 -> "new"
		* f1 -> "int"
		* f2 -> "["
		* f3 -> Expression()
		* f4 -> "]"
		*/
	public MType visit(ArrayAllocationExpression n, MType argu) {
		int t1 = cur_tmp ++;
		int t2 = cur_tmp ++;
		int t3 = cur_tmp ++;
		int t4 = cur_tmp ++;
		int l1 = cur_label ++;
		int l2 = cur_label ++;
		PrintPiglet.pBegin();
		PrintPiglet.p("MOVE TEMP " + t1 + " ");
		n.f3.accept(this, argu);
		PrintPiglet.pln("");
		PrintPiglet.pln("MOVE TEMP " + t2 + " HALLOCATE TIMES 4 PLUS 1 TEMP " + t1);
		PrintPiglet.pln("HSTORE TEMP " + t2 + " 0 TEMP " + t1);
		PrintPiglet.pReturn();
		PrintPiglet.pln("TEMP " + t2);
		PrintPiglet.pEnd();
		return null;
	}
		
		/**
		* f0 -> "new"
		* f1 -> Identifier()
		* f2 -> "("
		* f3 -> ")"
		*/
	public MType visit(AllocationExpression n, MType argu) {  
		String class_name = n.f1.accept(this, argu).getName();
		int t1 = cur_tmp ++;
		int t2 = cur_tmp ++;
		
		PrintPiglet.pBegin();
		PrintPiglet.pln("MOVE TEMP " + t1 + " HALLOCATE " + (methodMap.get(class_name).size()*4));
		PrintPiglet.pln("MOVE TEMP " + t2 + " HALLOCATE " + ((varMap.get(class_name).size()+1)*4));
		for(int i = 0; i < methodMap.get(class_name).size(); i ++){
			PrintPiglet.pln("HSTORE TEMP " + t1 + " " + (i*4) + " " + class_name + "_" + methodMap.get(class_name).elementAt(i));
		}
		PrintPiglet.pln("HSTORE TEMP " + t2 + " 0 TEMP " + t1);
		PrintPiglet.pReturn();
		PrintPiglet.pln("TEMP " + t2);
		PrintPiglet.pEnd();
		
		return all_classes.getClass(class_name);
	}
		
		/**
		* f0 -> "!"
		* f1 -> Expression()
		*/
	public MType visit(NotExpression n, MType argu) {
		PrintPiglet.p("MINUS 1 ");
		n.f1.accept(this, argu);
		PrintPiglet.pln("");
		return null;
	}
		
		
		/**
		* f0 -> "("
		* f1 -> Expression()
		* f2 -> ")"
		*/
	public MType visit(BracketExpression n, MType argu) {
		return n.f1.accept(this, argu);
	}
}
