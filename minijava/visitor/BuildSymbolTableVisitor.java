package minijava.visitor;

import java.util.Enumeration;

import minijava.symboltable.*;
import minijava.syntaxtree.*;
import minijava.typecheck.PrintMsg;

/**
 * 建立符号表的Visitor
 * 深度优先遍历
 * 同时也做重复定义检查
 */
public class BuildSymbolTableVisitor extends GJDepthFirst<MType, MType> {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
   public MType visit(NodeList n, MType argu) {
      MType _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public MType visit(NodeListOptional n, MType argu) {
      if ( n.present() ) {
         MType _ret=null;
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
         }
         return _ret;
      }
      else
         return null;
   }

   public MType visit(NodeOptional n, MType argu) {
      if ( n.present() )
         return n.node.accept(this,argu);
      else
         return null;
   }

   public MType visit(NodeSequence n, MType argu) {
      MType _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public MType visit(NodeToken n, MType argu) { return null; }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
   public MType visit(Goal n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
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
      MType _ret=null;
      MClass m_class;
      String class_name;
      String error_msg;
      n.f0.accept(this, argu);

      /*
       * 获得主类类名并在符号表中插入此类
       * 构造同时定义其父类为缺省的"Object"
       */
      class_name = ((MIdentifier) n.f1.accept(this, argu)).getName();
      m_class = new MClass(class_name, (MClasses) argu, n.f1.f0.beginLine,
				n.f1.f0.beginColumn);
      m_class.father = "Object";
      error_msg = ((MClasses) argu).InsertClass(m_class);
      if (error_msg != null){
    	  PrintMsg.print(m_class.getLine(), m_class.getColumn(), error_msg);
    	  return _ret;
      }
      /*
       * 手动为主类定义一个名为"main"，类型为"void"的方法并插入方法列表
       */
      MMethod m_method = new MMethod("main", "void", m_class, m_class.getLine()+1, m_class.getColumn()+27);
      error_msg = m_class.InsertMethod(m_method);
      if (error_msg != null){
    	  PrintMsg.print(m_method.getLine(), m_method.getColumn(), error_msg);
    	  return _ret;
      }
      n.f2.accept(this, argu);
      n.f3.accept(this, m_class);
      n.f4.accept(this, m_class);
      n.f5.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      n.f10.accept(this, argu);
      /*
       * 手动为主类添加一个String类型的参数变量并插入参数列表
       */
      MIdentifier arg = (MIdentifier)n.f11.accept(this, m_method);
      String argname = arg.getName();
      MVar argv = new MVar(argname, "String", m_method, arg.getLine(), arg.getColumn());
      m_method.InsertPara(argv);
      n.f12.accept(this, argu);
      n.f13.accept(this, argu);
      n.f14.accept(this, m_method);
      n.f15.accept(this, argu);
      n.f16.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public MType visit(TypeDeclaration n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      return _ret;
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
		MType _ret = null;
		MClass m_class;
		String class_name;
		String error_msg;

		n.f0.accept(this, argu);

		/*
		 * 构造新类在符号表中插入，并检查是否有重复
		 * 构造同时定义其父类为缺省的"Object"
		 */
		class_name = ((MIdentifier) n.f1.accept(this, argu)).getName();
		m_class = new MClass(class_name, (MClasses) argu, n.f1.f0.beginLine,
				n.f1.f0.beginColumn);
		m_class.father = "Object";
		error_msg = ((MClasses) argu).InsertClass(m_class);
		if (error_msg != null){
			PrintMsg.print(m_class.getLine(), m_class.getColumn(), error_msg);
			return _ret;
		}
		n.f2.accept(this, argu);
		n.f3.accept(this, m_class);
		n.f4.accept(this, m_class);
		n.f5.accept(this, argu);
		return _ret;
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
      MType _ret=null;
      MClass m_class;
      String class_name;
      String error_msg;
      
      /*
       * 构造新类并在符号表中插入，检查是否有重复
       * 构造同时为其定义有意义的父类
       */
      n.f0.accept(this, argu);
      class_name = ((MIdentifier) n.f1.accept(this, argu)).getName();
      m_class = new MClass(class_name, (MClasses) argu, n.f1.f0.beginLine,
				n.f1.f0.beginColumn);
      error_msg = ((MClasses) argu).InsertClass(m_class);
      n.f2.accept(this, argu);
      m_class.father = ( (MIdentifier) n.f3.accept(this, argu)).getName();
      if (error_msg != null){
    	  PrintMsg.print(m_class.getLine(), m_class.getColumn(), error_msg);
    	  return _ret;
      }
      n.f4.accept(this, argu);
      n.f5.accept(this, m_class);
      n.f6.accept(this, m_class);
      n.f7.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public MType visit(VarDeclaration n, MType argu) {
      MType _ret=null;
      MVar m_var;
      String var_name, var_type;
      String error_msg;
      
      /*
       * 构造新变量并在符号表中插入，检查是否有重复
       * 定义其类型、所属方法或类
       */
      var_type = ( (MTypename) n.f0.accept(this, argu) ).type;
      var_name = ((MIdentifier) n.f1.accept(this, argu)).getName();
      if(argu instanceof MMethod){	//查看变量属于方法或类
    	  m_var = new MVar(var_name, var_type, (MMethod) argu, n.f1.f0.beginLine,
    			  n.f1.f0.beginColumn);
    	  error_msg = ((MMethod) argu).InsertVar(m_var);
      }else if(argu instanceof MClass){
       	  m_var = new MVar(var_name, var_type, (MClass) argu, n.f1.f0.beginLine,
    			  n.f1.f0.beginColumn);
    	  error_msg = ((MClass) argu).InsertVar(m_var);
      }else{
    	  //impossible to happen because of previous check
    	  m_var = null;
    	  error_msg = "Invalid variable declaration position";
      }
      if (error_msg != null)
    	  PrintMsg.print(m_var.getLine(), m_var.getColumn(), error_msg);
      
      n.f2.accept(this, argu);
      return _ret;
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
      MType _ret=null;
      MMethod m_method;
      String method_name, ret_type;
      String error_msg;
      n.f0.accept(this, argu);
      
      /*
       * 构造新方法并在符号表中插入，检查是否有重复
       * 定义其返回类型，所属类
       */
      ret_type = ( (MTypename)n.f1.accept(this, argu) ).type;
      method_name = ((MIdentifier) n.f2.accept(this, argu)).getName();
      m_method = new MMethod(method_name, ret_type, (MClass) argu, n.f2.f0.beginLine,
    			 n.f2.f0.beginColumn);
      error_msg = ((MClass) argu).InsertMethod(m_method);
      
      if (error_msg != null){
    	  PrintMsg.print(m_method.getLine(), m_method.getColumn(), error_msg);
    	  return _ret;
      }
      n.f3.accept(this, m_method);
      n.f4.accept(this, m_method);
      n.f5.accept(this, m_method);
      n.f6.accept(this, m_method);
      n.f7.accept(this, m_method);
      n.f8.accept(this, m_method);
      n.f9.accept(this, m_method);
      n.f10.accept(this, m_method);
      n.f11.accept(this, m_method);
      n.f12.accept(this, m_method);
      return _ret;
   }

   /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
   public MType visit(FormalParameterList n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public MType visit(FormalParameter n, MType argu) {
      MType _ret=null;
      MVar m_var;
      String var_name, var_type;
      String error_msg;
      var_type = ( (MTypename)n.f0.accept(this, argu) ).type;
      
      var_name = ((MIdentifier) n.f1.accept(this, argu)).getName();
      m_var = new MVar(var_name, var_type, (MMethod) argu, n.f1.f0.beginLine,
    		  n.f1.f0.beginColumn);
      error_msg = ((MMethod) argu).InsertPara(m_var);
      if (error_msg != null)
    	  PrintMsg.print(m_var.getLine(), m_var.getColumn(), error_msg);
      
      return _ret;
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public MType visit(FormalParameterRest n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public MType visit(Type n, MType argu) {
	  MType _ret = n.f0.accept(this, argu);
	  /*
	   * 自下而上传递类型名 
	   */
	  if(_ret instanceof MIdentifier) 
		  _ret = new MTypename(_ret.getName(), "", _ret.getLine(), _ret.getColumn());
      return _ret;
   }
   

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public MType visit(ArrayType n, MType argu) {
      MTypename _ret=null;
      MType tmp = n.f0.accept(this, argu);
      _ret = new MTypename("Array", "", n.f0.beginLine, n.f0.beginColumn);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "boolean"
    */
   public MType visit(BooleanType n, MType argu) {
      MTypename _ret=null;
      MType tmp = n.f0.accept(this, argu);
      _ret = new MTypename("Boolean", "", n.f0.beginLine, n.f0.beginColumn);
      return _ret;
   }

   /**
    * f0 -> "int"
    */
   public MType visit(IntegerType n, MType argu) {
      MTypename _ret=null;
      MType tmp = n.f0.accept(this, argu);
      _ret = new MTypename("Int", "", n.f0.beginLine, n.f0.beginColumn);
      return _ret;
   }

   /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
   public MType visit(Statement n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public MType visit(Block n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public MType visit(AssignmentStatement n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      return _ret;
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
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      return _ret;
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
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public MType visit(WhileStatement n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public MType visit(PrintStatement n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
   public MType visit(Expression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
   public MType visit(AndExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public MType visit(CompareExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public MType visit(PlusExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public MType visit(MinusExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public MType visit(TimesExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public MType visit(ArrayLookup n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public MType visit(ArrayLength n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
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
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public MType visit(ExpressionList n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public MType visit(ExpressionRest n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
   public MType visit(PrimaryExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public MType visit(IntegerLiteral n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "true"
    */
   public MType visit(TrueLiteral n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "false"
    */
   public MType visit(FalseLiteral n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public MType visit(Identifier n, MType argu) {
     String identifier_name = n.f0.toString();
     MType _ret = null;
     _ret = new MIdentifier(identifier_name, n.f0.beginLine, n.f0.beginColumn);
     return _ret;
   }

   /**
    * f0 -> "this"
    */
   public MType visit(ThisExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public MType visit(ArrayAllocationExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public MType visit(AllocationExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public MType visit(NotExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public MType visit(BracketExpression n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

}