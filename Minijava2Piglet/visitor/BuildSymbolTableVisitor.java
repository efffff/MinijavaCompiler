package visitor;

import symbol.*;
import syntaxtree.*;
import java.util.*;
import utils.*;

public class BuildSymbolTableVisitor implements GJVisitor<MType, MType> {

	public MType visit(Node n, MType argu) {
		n.accept(this, argu);
		return null;
	}

	public MType visit(NodeList n, MType argu) {
		for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
			e.nextElement().accept(this, argu);
		}
		return null;
	}

	public MType visit(NodeListOptional n, MType argu) {
		if (n.present())
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
				e.nextElement().accept(this, argu);
		return null;
	}

	public MType visit(NodeOptional n, MType argu) {
		if (n.present())
			n.node.accept(this, argu);
		return null;
	}

	public MType visit(NodeSequence n, MType argu) {
		for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
			e.nextElement().accept(this, argu);
		return null;
	}

	public MType visit(NodeToken n, MType argu) {
		return null;
	}

	/* Goal ::= MainClass ( TypeDeclaration )* <EOF> */
	public MType visit(Goal n, MType argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
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
	public MType visit(MainClass n, MType argu) {
		n.f0.accept(this, argu);
		MIdentifier classID = (MIdentifier) n.f1.accept(this, argu);
		MClass mainClass = new MClass(classID.getName(), null);
		MMethod mainMethod = new MMethod("void", "main", classID.getName(), 0);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		n.f7.accept(this, argu);
		n.f8.accept(this, argu);
		n.f9.accept(this, argu);
		n.f10.accept(this, argu);
		MIdentifier paramID = (MIdentifier) n.f11.accept(this, argu);
		mainMethod.addParam(new MVar("String", paramID.getName(), -1, 1));
		n.f12.accept(this, argu);
		n.f13.accept(this, argu);
		n.f14.accept(this, mainMethod);
		n.f15.accept(this, argu);
		n.f16.accept(this, argu);
		n.f17.accept(this, argu);
		mainClass.insertMethod(mainMethod);
		String msg = ((MClassList)argu).insertClass(mainClass);
		if (msg != null) {
			ErrorPrinter.add(msg);
		}
		return mainClass;
	}

	/**
	 * f0 -> ClassDeclaration()
	 *       | ClassExtendsDeclaration()
	 */
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
	public MType visit(ClassDeclaration n, MType argu) {
		n.f0.accept(this, argu);
		MIdentifier className = (MIdentifier) n.f1.accept(this, argu);
		MClass thisClass = new MClass(className.getName(), null);
		n.f2.accept(this, argu);
		n.f3.accept(this, thisClass);
		n.f4.accept(this, thisClass);
		n.f5.accept(this, argu);
		String msg = ((MClassList)argu).insertClass(thisClass);
		if (msg != null) {
			ErrorPrinter.add(msg);
		};
		return thisClass;
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
		n.f0.accept(this, argu);
		MIdentifier className = (MIdentifier) n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		MIdentifier parentClass = (MIdentifier) n.f3.accept(this, argu);
		MClass thisClass = new MClass(className.getName(), parentClass);
		//test
		//System.out.println(thisClass.getName() + " " + parentClass.getName());
		((MClassList)argu).insertClass(thisClass);
		n.f4.accept(this, argu);
		n.f5.accept(this, thisClass);
		n.f6.accept(this, thisClass);
		n.f7.accept(this, argu);
		return thisClass;
	}
	  /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    * f2 -> ";"
	    */
	public MType visit(VarDeclaration n, MType argu) {
		MType t = n.f0.accept(this, argu);
		MIdentifier id = (MIdentifier) n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		if (argu.getClass().getName() == "symbol.MClass") 
			((MClass)argu).insertVar(new MVar(t.getType(), id.getName(), ((MClass)argu).getVarNo()*4, -1));
		else if (argu.getClass().getName() == "symbol.MMethod")
			((MMethod)argu).insertVar(new MVar(t.getType(), id.getName(), -1, ((MMethod)argu).getLocalTempNo()));
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
		n.f0.accept(this, argu);
		MType returnType = n.f1.accept(this, argu);
		MIdentifier id = (MIdentifier) n.f2.accept(this, argu);
		MMethod thisMethod = new MMethod(returnType.getType(), id.getName(), ((MClass)argu).getName(), ((MClass)argu).getMethodNo());
		n.f3.accept(this, argu);
		n.f4.accept(this, thisMethod);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		n.f7.accept(this, thisMethod);
		n.f8.accept(this, argu);
		n.f9.accept(this, argu);
		n.f10.accept(this, argu);
		n.f11.accept(this, argu);
		n.f12.accept(this, argu);
		String msg = ((MClass)argu).insertMethod(thisMethod);
		if (msg != null) {
			ErrorPrinter.add(msg);
		}
		return null;
	}

	   /**
	    * f0 -> FormalParameter()
	    * f1 -> ( FormalParameterRest() )*
	    */
	public MType visit(FormalParameterList n, MType argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	
	public MType visit(FormalParameter n, MType argu) {
		MType t = n.f0.accept(this, argu);
		MIdentifier id = (MIdentifier) n.f1.accept(this, argu);
		//System.out.println(((MMethod)argu).getParamTempNo());
		((MMethod) argu).addParam(new MVar(t.getType(), id.getName(), -1, ((MMethod)argu).getParamTempNo()));
		return null;
	}

	
	public MType visit(FormalParameterRest n, MType argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	/**
	    * f0 -> ArrayType()
	    *       | BooleanType()
	    *       | IntegerType()
	    *       | Identifier()
	    */
	public MType visit(Type n, MType argu) {
		return n.f0.accept(this, argu);
	}

	
	public MType visit(ArrayType n, MType argu) {
		return new MType("int []"); // to be revised
	}

	
	public MType visit(BooleanType n, MType argu) {
		return new MType("boolean");
	}

	
	public MType visit(IntegerType n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("int");
	}

	
	public MType visit(Statement n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(Block n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(AssignmentStatement n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(ArrayAssignmentStatement n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(IfStatement n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(WhileStatement n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(PrintStatement n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(Expression n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(AndExpression n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("boolean");
	}

	
	public MType visit(CompareExpression n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("boolean");
	}

	
	public MType visit(PlusExpression n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("int");
	}

	
	public MType visit(MinusExpression n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("int");
	}

	
	public MType visit(TimesExpression n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("int");
	}

	
	public MType visit(ArrayLookup n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("int");
	}

	
	public MType visit(ArrayLength n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("int");
	}

	
	public MType visit(MessageSend n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(ExpressionList n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(ExpressionRest n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(PrimaryExpression n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(IntegerLiteral n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("int");
	}

	
	public MType visit(TrueLiteral n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("boolean");
	}

	
	public MType visit(FalseLiteral n, MType argu) {
		// TODO Auto-generated method stub
		return new MType("boolean");
	}

	public MType visit(Identifier n, MType argu) {
		return new MIdentifier(n.f0.toString(), n.f0.toString());
	}

	
	public MType visit(ThisExpression n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(ArrayAllocationExpression n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(AllocationExpression n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(NotExpression n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MType visit(BracketExpression n, MType argu) {
		// TODO Auto-generated method stub
		return null;
	}
}
