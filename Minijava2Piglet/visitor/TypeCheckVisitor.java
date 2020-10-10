package visitor;

import syntaxtree.*;
import utils.ErrorPrinter;
import utils.GlobalData;
import java.util.*;

import symbol.*;

public class TypeCheckVisitor implements GJVisitor<MType, MType> {

	@Override
	public MType visit(NodeList n, MType argu) {
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	      }
		return null;
	}

	@Override
	public MType visit(NodeListOptional n, MType argu) {
		if ( n.present() ) {
	         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	            e.nextElement().accept(this,argu);
	         }
		}
		return null;
	}

	@Override
	public MType visit(NodeOptional n, MType argu) {
		if (n.present()) {
			return n.node.accept(this, argu);
		}
		else return null;
	}

	@Override
	public MType visit(NodeSequence n, MType argu) {
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	      }
		return null;
	}

	@Override
	public MType visit(NodeToken n, MType argu) {
		return null;
	}

	   /**
	    * f0 -> MainClass()
	    * f1 -> ( TypeDeclaration() )*
	    * f2 -> <EOF>
	    */
	public MType visit(Goal n, MType argu) {
		  GlobalData.allClassList = (MClassList) argu;  
		  n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      return null;
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
	    * f14 -> ( VarDeclaration() )*
	    * f15 -> ( Statement() )*
	    * f16 -> "}"
	    * f17 -> "}"
	    */
	@Override
	public MType visit(MainClass n, MType argu) {
		MMethod mainMethod = ((MClassList)argu).getClassInfo(n.f1.f0.
				toString()).getMethod("main");
		n.f14.accept(this, mainMethod);
	    n.f15.accept(this, mainMethod);
		return null;
	}

	   /**
	    * f0 -> ClassDeclaration()
	    *       | ClassExtendsDeclaration()
	    */
	@Override
	public MType visit(TypeDeclaration n, MType argu) {
		n.f0.accept(this, argu);
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
	@Override
	public MType visit(ClassDeclaration n, MType argu) {
		MIdentifier className = (MIdentifier) n.f1.accept(this, argu);
		MClass thisClass = ((MClassList)argu).getClassInfo(className.getName());
		n.f3.accept(this, thisClass);
		n.f4.accept(this, thisClass);
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
	@Override
	public MType visit(ClassExtendsDeclaration n, MType argu) {
		MIdentifier className = (MIdentifier) n.f1.accept(this, argu);
		MClass mclass = ((MClassList)argu).getClassInfo(className.getName());
		n.f5.accept(this, mclass);
	    n.f6.accept(this, mclass);
		return null;
	}

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    * f2 -> ";"
	    */
	@Override
	public MType visit(VarDeclaration n, MType argu) {
		if (argu.getClass().getName() == "symbol.MMethod") {
			MIdentifier id = (MIdentifier) n.f1.accept(this, argu);
			if (((MMethod)argu).existParam(id.getName())) {
				ErrorPrinter.add("Error type 1: parameters "
						+ "duplicate with variables in method " 
						+ ((MMethod)argu).getName());
			}
		}
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
	@Override
	public MType visit(MethodDeclaration n, MType argu) {
	    MType returnType = n.f1.accept(this, GlobalData.allClassList);
	    String methodName = ((MIdentifier) n.f2.accept(this, argu)).getName();
	    MMethod thisMethod = ((MClass)argu).getMethod(methodName);
	    n.f7.accept(this, thisMethod);
	    n.f8.accept(this, thisMethod);
	    MType returnType1 = n.f10.accept(this, thisMethod);
	    if (returnType.getType() != returnType1.getType()) {
	    	ErrorPrinter.add("Error Type 3: return Types don't "
	    			+ "match between " + returnType.getType() 
	    			+ " and " + returnType1.getType());
	    }
		return null;
	}


	   /**
	    * f0 -> FormalParameter()
	    * f1 -> ( FormalParameterRest() )*
	    */
	@Override
	public MType visit(FormalParameterList n, MType argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    */
	@Override
	public MType visit(FormalParameter n, MType argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	   /**
	    * f0 -> ","
	    * f1 -> FormalParameter()
	    */
	@Override
	public MType visit(FormalParameterRest n, MType argu) {
		n.f1.accept(this, argu);
	     return null;
	}

	   /**
	    * f0 -> ArrayType()
	    *       | BooleanType()
	    *       | IntegerType()
	    *       | Identifier()
	    */
	@Override
	public MType visit(Type n, MType argu) {
		return n.f0.accept(this, argu);
	}

	   /**
	    * f0 -> "int"
	    * f1 -> "["
	    * f2 -> "]"
	    */
	@Override
	public MType visit(ArrayType n, MType argu) {
		return new MType("int []");
	}

	  /**
	    * f0 -> "boolean"
	    */
	@Override
	public MType visit(BooleanType n, MType argu) {
		return new MType("boolean");
	}

	   /**
	    * f0 -> "int"
	    */
	@Override
	public MType visit(IntegerType n, MType argu) {
		return new MType("int");
	}

	  /**
	    * f0 -> Block()
	    *       | AssignmentStatement()
	    *       | ArrayAssignmentStatement()
	    *       | IfStatement()
	    *       | WhileStatement()
	    *       | PrintStatement()
	    */
	@Override
	public MType visit(Statement n, MType argu) {
		return n.f0.accept(this, argu);
	}

	   /**
	    * f0 -> "{"
	    * f1 -> ( Statement() )*
	    * f2 -> "}"
	    */
	@Override
	public MType visit(Block n, MType argu) {
		n.f1.accept(this, argu);
		return null;
	}

	  /**
	    * f0 -> Identifier()
	    * f1 -> "="
	    * f2 -> Expression()
	    * f3 -> ";"
	    */
	@Override
	public MType visit(AssignmentStatement n, MType argu) {
		MIdentifier left = (MIdentifier) n.f0.accept(this, argu);
		//test
		//System.out.println(left.getName() + " " + argu.getType() + " " + argu.getClass().getName());
		MType leftType = declared(left.getName(), ((MMethod)argu).getBelongClass());
		if (leftType == null) leftType = declared(left.getName(), argu);
		if (leftType == null) {
			ErrorPrinter.add("Error Type 1: undefined identifier " + left.getName());
			return null;
		}
		MType right = n.f2.accept(this, argu);
		if (right == null) {
			return null;
		}
		if (right != null && left != null 
				&& right.getType() != left.getType() ) {
			if (right.getType() != "int" && right.getType() != "boolean" 
					&& right.getType() != "int []" && 
					(GlobalData.allClassList.getClassInfo(right.getType()).
					getParentID() == left.getType())) {
				return null;
			}
			ErrorPrinter.add("Error Type 4: Types don't match "
					+ "in AssignmentStatement between " + 
					left.getType() + " with name " + left.getName() + " and " + right.getType());
		}
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
	@Override
	public MType visit(ArrayAssignmentStatement n, MType argu) {
		MIdentifier arrayID = (MIdentifier) n.f0.accept(this, argu);
		if (arrayID.getType() != "int []") {
			ErrorPrinter.add("Error Type 4: expected type int[] "
					+ "in ArrayAssignmentStatement but get " + arrayID.getType());
		}
	    MType expType1 = n.f2.accept(this, argu);
	    if (expType1.getType() != "int") {
	    	ErrorPrinter.add("Error Type 4: expected type int "
					+ "in ArrayAssignmentStatement but get " + expType1.getType());
	    }
	    MType expType2 = n.f5.accept(this, argu);
	    if (expType2.getType() != "int") {
	    	ErrorPrinter.add("Error Type 4: expected type int "
					+ "in ArrayAssignmentStatement but get " + expType1.getType());
	    }
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
	@Override
	public MType visit(IfStatement n, MType argu) {
		MType exp = n.f2.accept(this, argu);
		if (exp.getType() != "boolean") {
			ErrorPrinter.add("Error Type 4: boolean type expected "
					+ "for IfStatement but " + exp.getType());
		}
		n.f4.accept(this, argu);
		n.f6.accept(this, argu);
		return null;
	}

	   /**
	    * f0 -> "while"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> Statement()
	    */
	@Override
	public MType visit(WhileStatement n, MType argu) {
		MType exp = n.f2.accept(this, argu);
		if (exp.getType() != "boolean") {
			ErrorPrinter.add("Error Type 4: boolean type expected "
					+ "for WhileStatement but " + exp.getType());
		}
		n.f4.accept(this, argu);
		return null;
	}

	   /**
	    * f0 -> "System.out.println"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> ";"
	    */
	@Override
	public MType visit(PrintStatement n, MType argu) {
	    MType type = n.f2.accept(this, argu);
	    if (type.getType() != "int") {
	    	ErrorPrinter.add("Error Type 4: print statement type must be int but " + type.getType());
	    }
	    return null;
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
	@Override
	public MType visit(Expression n, MType argu) {
		return n.f0.accept(this, argu);
	}

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "&&"
	    * f2 -> PrimaryExpression()
	    */
	@Override
	public MType visit(AndExpression n, MType argu) {
		MType leftType = n.f0.accept(this, argu);
		MType rightType = n.f2.accept(this, argu);
		if (leftType.getType() != "boolean" || rightType.getType() != "boolean") {
			ErrorPrinter.add("Error Type 4: "
					+ "type mismatch in AndExpression, "
					+ "expected boolean and boolean, but "
					+ leftType.getType() + " and " + rightType.getType());
		}
		return new MType("boolean");
	}

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "<"
	    * f2 -> PrimaryExpression()
	    */
	public MType visit(CompareExpression n, MType argu) {
		MType leftType = n.f0.accept(this, argu);
		MType rightType = n.f2.accept(this, argu);
		if (leftType.getType() != "int" || rightType.getType() != "int") {
			ErrorPrinter.add("Error Type 4: "
					+ "type mismatch in CompareExpression, "
					+ "expected int and int, but "
					+ leftType.getType() + " and " + rightType.getType());
		}
		return new MType("boolean");
	}

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "+"
	    * f2 -> PrimaryExpression()
	    */
	@Override
	public MType visit(PlusExpression n, MType argu) {
		MType leftType = n.f0.accept(this, argu);
		MType rightType = n.f2.accept(this, argu);
		if (leftType.getType() != "int" || rightType.getType() != "int") {
			ErrorPrinter.add("Error Type 4: "
					+ "type mismatch in PlusExpression, "
					+ "expected int and int, but "
					+ leftType.getType() + " and " + rightType.getType());
		}
		return new MType("int");
	}
	
	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "-"
	    * f2 -> PrimaryExpression()
	    */
	@Override
	public MType visit(MinusExpression n, MType argu) {
		MType leftType = n.f0.accept(this, argu);
		MType rightType = n.f2.accept(this, argu);
		if (leftType.getType() != "int" ||  rightType.getType() != "int") {
			ErrorPrinter.add("Error Type 4: "
					+ "type mismatch in MinusExpression, "
					+ "expected int and int, but "
					+ leftType.getType() + " and " + rightType.getType());
		}
		return new MType("int");
	}

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "*"
	    * f2 -> PrimaryExpression()
	    */
	@Override
	public MType visit(TimesExpression n, MType argu) {
		MType leftType = n.f0.accept(this, argu);
		MType rightType = n.f2.accept(this, argu);
		if (leftType.getType() != "int" || rightType.getType() != "int") {
			ErrorPrinter.add("Error Type 4: "
					+ "type mismatch in TimesExpression, "
					+ "expected int and int, but "
					+ leftType.getType() + " and " + rightType.getType());
		}
		return new MType("int");
	}

	/**
	    * f0 -> PrimaryExpression()
	    * f1 -> "["
	    * f2 -> PrimaryExpression()
	    * f3 -> "]"
	    */
	@Override
	public MType visit(ArrayLookup n, MType argu) {
		MType type1 = n.f0.accept(this, argu);
		if (type1.getType() != "int []") {
			ErrorPrinter.add("Error Type 4: expected type int[] for ArrayLookUp, but" + type1.getType());
		}
		MType type2 = n.f2.accept(this, argu);
		if (type2.getType() != "int") {
			ErrorPrinter.add("Error Type 4: expected type int for ArrayLookUp, but" + type2.getType());
		}
		return new MType("int");
	}
	
	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "."
	    * f2 -> "length"
	    */
	@Override
	public MType visit(ArrayLength n, MType argu) {
		MIdentifier id = (MIdentifier) n.f0.accept(this, argu);
		if (id.getType() != "int []") {
			ErrorPrinter.add("Error Type 3: Type doesn't match, Array Type wanted but " + id.getType());
		}
		return new MType("int");
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
		MType var = n.f0.accept(this, argu);
		String varType = var.getType();
		if (varType == null || varType == "int" 
				|| varType == "boolean" || varType == "identifier") {
			ErrorPrinter.add("Error Type 4: type " + varType +  " doesn't match");
			return null;
		}
		else if (!GlobalData.allClassList.exist(varType)) {
			ErrorPrinter.add("Error: undefined type " + varType);
			return null;
		}
		MIdentifier id = (MIdentifier) n.f2.accept(this, GlobalData.allClassList.getClassInfo(varType));
		if (GlobalData.allClassList.exist(varType) && 
				!GlobalData.allClassList.getClassInfo(varType).
				existMethod(id.getName())) {
			ErrorPrinter.add("Error: method " + id.getName() + " inexist!");
			return null;
		}
		else {
			((MMethod)argu).paramCall.clear();
			n.f4.accept(this, argu);
			MMethod callMethod = GlobalData.allClassList.
					getClassInfo(varType).getMethod(id.getName());
			callMethod.initialize();
			if ((((MMethod)argu).paramCall.isEmpty() && 
				!callMethod.getParamSet().isEmpty())
					|| (!((MMethod)argu).paramCall.isEmpty() && 
							callMethod.getParamSet().isEmpty())) {
				ErrorPrinter.add("Error type 4: the number of "
						+ "parameters don't match in method " + 
						callMethod.getName());
				return null;
			}
			else if (!((MMethod)argu).paramCall.isEmpty() && 
					!callMethod.getParamSet().isEmpty()){
				Iterator<String> typeIterator = ((MMethod)argu).paramCall.iterator();
				while (callMethod.paramIterator.hasNext() && typeIterator.hasNext()) {
					String expected = callMethod.paramIterator.next().getType();
					String actual = typeIterator.next();
					if (expected != actual) {
						if (actual != "int" && actual != "boolean" 
								&& actual != "int []" && 
								(GlobalData.allClassList.getClassInfo(actual).
								getParentID() == expected)) {
							continue;
						}
						ErrorPrinter.add("Error type 4: the types of "
								+ "parameters don't match in method " + 
								callMethod.getName() + " expected " 
								+ expected + " but " + actual);
						break;
					}
				}
				if (callMethod.paramIterator.hasNext() || typeIterator.hasNext()) {
					ErrorPrinter.add("Error type 4: the numbers of "
							+ "parameters don't match in method " + 
							callMethod.getName());
				}
			}
		}
		return new MType(GlobalData.allClassList.getClassInfo(varType).
				getMethod(id.getName()).getType());
	}

	   /**
	    * f0 -> Expression()
	    * f1 -> ( ExpressionRest() )*
	    */
	public MType visit(ExpressionList n, MType argu) {
		MType type = n.f0.accept(this, argu);
		((MMethod)argu).paramCall.add(type.getType());
		n.f1.accept(this, argu);
		return null;
	}

	   /**
	    * f0 -> ","
	    * f1 -> Expression()
	    */
	public MType visit(ExpressionRest n, MType argu) {
		MType type = n.f1.accept(this, argu);
		((MMethod)argu).paramCall.add(type.getType());
		return null;
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
		return n.f0.accept(this, argu); 
	}

	   /**
	    * f0 -> <INTEGER_LITERAL>
	    */
	public MType visit(IntegerLiteral n, MType argu) {
		return new MType("int");
	}

	/**
	    * f0 -> "true"
	    */
	public MType visit(TrueLiteral n, MType argu) {
		return new MType("boolean");
	}

	/**
	    * f0 -> "false"
	    */
	public MType visit(FalseLiteral n, MType argu) {
		return new MType("boolean");
	}

	/**
	    * f0 -> <IDENTIFIER>
	    */
	public MType visit(Identifier n, MType argu) {
		String id = n.f0.toString();
		MType type = null;
		type = declared(id, GlobalData.allClassList);
		if (type == null) {
			if (argu.getClass().getName() == "symbol.MClass") {
				type = declared(id, argu);
			}
			if (argu.getClass().getName() == "symbol.MMethod") {
				type = declared(id, ((MMethod)argu).getBelongClass());
				if (type == null) {
					type = declared(id, argu);
				}
			}
		}
		if (type == null) {
			ErrorPrinter.add("Error Type 1: undefined variable " + id + " in method " + argu.getType());
			return new MIdentifier("identifier", n.f0.toString());
		}
		//test
		//System.out.println("identifier " + id + " " + type.getType());
		return new MIdentifier(type.getType(), n.f0.toString()); // to be revised */
		//return new MIdentifier(id, id);
	}

	/**
	    * f0 -> "this"
	    */
	public MType visit(ThisExpression n, MType argu) {
		if (argu.getClass().getName() == "symbol.MClass") {
			return new MType(((MClass)argu).getName());
		}
		else if (argu.getClass().getName() == "symbol.MMethod") {
			return new MType(((MMethod)argu).getBelongClass().getName());
		}
		else return null;
	}

	   /**
	    * f0 -> "new"
	    * f1 -> "int"
	    * f2 -> "["
	    * f3 -> Expression()
	    * f4 -> "]"
	    */
	public MType visit(ArrayAllocationExpression n, MType argu) {
		MType type = n.f3.accept(this, argu);
		if (type.getType() != "int") {
			ErrorPrinter.add("Error Type 3: Type must be integer in ArrayAllocationExpression!");
		}
		return new MType("int []");
	}

	/**
	    * f0 -> "new"
	    * f1 -> Identifier()
	    * f2 -> "("
	    * f3 -> ")"
	    */
	public MType visit(AllocationExpression n, MType argu) {
		n.f1.accept(this, argu);
		MIdentifier id = (MIdentifier) n.f1.accept(this, argu);
		/*if (!((MClassList)argu).exist(id.getName())) {
			ErrorPrinter.add("Error Type 1: undefined identifier " + id.getName());
		}*/
		return new MType(id.getName());
	}
	
	   /**
	    * f0 -> "!"
	    * f1 -> Expression()
	    */
	public MType visit(NotExpression n, MType argu) {
		MType type = n.f1.accept(this, argu);
		if (type.getType() != "boolean") {
			ErrorPrinter.add("Error Type 3: The Type must be boolean in a NotExpression!");
		}
		return new MType("boolean");
	}

	 /**
	    * f0 -> "("
	    * f1 -> Expression()
	    * f2 -> ")"
	    */
	public MType visit(BracketExpression n, MType argu) {
		return n.f1.accept(this, argu);
	}
	
	private MType declared(String id, MType field) {
		if (field == null) {
			for (String myClassName: GlobalData.allClassList.getKeys()) {
				if (id == myClassName) return new MType(GlobalData.allClassList.getClassInfo(myClassName).getType());
				MClass myClass = GlobalData.allClassList.getClassInfo(myClassName);
				for (String myMethodName: myClass.getMethodNames()) {
					if (id == myMethodName) {
						return new MType(myClass.getMethod(myMethodName).getType());
					}
					MMethod myMethod = myClass.getMethod(myMethodName);
					for (String myVarName: myMethod.getVarSet()) {
						if (id == myVarName) {
							return new MType(myMethod.getVar(myVarName).getType());
						}
					}
					for (String myParamName: myMethod.getParamSet()) {
						if (id == myParamName) {
							return new MType(myMethod.getParam(myParamName).getType());
						}
					}
				}
				for (String myVarName: myClass.getVarNames()) {
					if (id == myVarName) {
						return new MType(myClass.getVar(myVarName).getType());
					}
				}
			}
			return null;
		}
		else if (field.getClass().getName() == "symbol.MClassList") {
			for (String myClassName: GlobalData.allClassList.getKeys()) {
				if (id == myClassName) {
					return new MType(GlobalData.allClassList.
							getClassInfo(myClassName).getType());
				}
			}
			return null;
		}
		else if (field.getClass().getName() == "symbol.MClass"){
			//test
			//System.out.println(((MClass)field).getName() + " " + id);
			for (String myMethodName: ((MClass)field).getMethodNames()) {
				if (id == myMethodName) {
					return new MType(((MClass)field).
							getMethod(myMethodName).getType());
				}
			}
			for (String myVarName: ((MClass)field).getVarNames()) {
				if (id == myVarName) {
					return new MType(((MClass)field).
							getVar(myVarName).getType());
				}
			}
		}
		else if (field.getClass().getName() == "symbol.MMethod") {
			//test
			//System.out.println(((MMethod)field).getName() + " " + id);
			for (String myVarName: ((MMethod)field).getVarSet()) {
				if (id == myVarName) {
					return new MType(((MMethod)field).
							getVar(myVarName).getType());
				}
			}
			for (String myParamName: ((MMethod)field).getParamSet()) {
				if (id == myParamName) {
					return new MType(((MMethod)field).
							getParam(myParamName).getType());
				}
			}
		}
		return null;
	}
}
