//
// By CZY
//

package visitor;

import syntaxtree.*;
import java.util.*;

public class NodePrintVisitor implements Visitor {
	public int d;

	public NodePrintVisitor() {
		d = 0;
	}

	public void visit(NodeList n) {
		for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
			e.nextElement().accept(this);
	}

	public void visit(NodeListOptional n) {
		if (n.present())
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
				e.nextElement().accept(this);
	}

	public void visit(NodeOptional n) {
		if (n.present())
			n.node.accept(this);
	}

	public void visit(NodeSequence n) {
		for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
			e.nextElement().accept(this);
	}

	public void visit(NodeToken n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("NodeToken: " + n.toString());
	}

	public void visit(Goal n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("Goal: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(MainClass n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("MainClass: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		n.f6.accept(this);
		n.f7.accept(this);
		n.f8.accept(this);
		n.f9.accept(this);
		n.f10.accept(this);
		n.f11.accept(this);
		n.f12.accept(this);
		n.f13.accept(this);
		n.f14.accept(this);
		n.f15.accept(this);
		n.f16.accept(this);
		n.f17.accept(this);
		d -= 1;
	}

	public void visit(TypeDeclaration n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("TypeDeclaration: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(ClassDeclaration n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ClassDeclaration: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		d -= 1;
	}

	public void visit(ClassExtendsDeclaration n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ClassExtendDeclaration: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		n.f6.accept(this);
		n.f7.accept(this);
		d -= 1;
	}

	public void visit(VarDeclaration n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("VarDeclaration: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(MethodDeclaration n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("MethodDeclaration: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		n.f6.accept(this);
		n.f7.accept(this);
		n.f8.accept(this);
		n.f9.accept(this);
		n.f10.accept(this);
		n.f11.accept(this);
		n.f12.accept(this);
		d -= 1;
	}

	public void visit(FormalParameterList n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("FormalPrameterList: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		d -= 1;

	}

	public void visit(FormalParameter n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("FormalParameter: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		d -= 1;

	}

	public void visit(FormalParameterRest n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("FormalParameterRest: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		d -= 1;
	}

	public void visit(Type n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("Type: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(ArrayType n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ArrayType: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(BooleanType n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("BooleanType: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;

	}

	public void visit(IntegerType n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("IntegerType: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(Statement n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("Statement: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(Block n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("Block: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(AssignmentStatement n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("AssignmentStatement: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		d -= 1;
	}

	public void visit(ArrayAssignmentStatement n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ArrayAssignmentStatement: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		n.f6.accept(this);
		d -= 1;
	}

	public void visit(IfStatement n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("IfStatement: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		n.f6.accept(this);
		d -= 1;
	}

	public void visit(WhileStatement n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("WhileStatement: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		d -= 1;
	}

	public void visit(PrintStatement n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("PrintStatement: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		d -= 1;
	}

	public void visit(Expression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("Expression: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(AndExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("AndExpression: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(CompareExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("CompareExpression: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(PlusExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("PlusExpression: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(MinusExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("MinusExpression: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(TimesExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("TimesExpression: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(ArrayLookup n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ArrayLookup: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		d -= 1;
	}

	public void visit(ArrayLength n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ArrayLength: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

	public void visit(MessageSend n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("MessageSend: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		d -= 1;
	}

	public void visit(ExpressionList n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ExpressionList: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		d -= 1;
	}

	public void visit(ExpressionRest n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ExpressionRest: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		d -= 1;
	}

	public void visit(PrimaryExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("PrimaryExpression: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(IntegerLiteral n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("IntegerLiteral: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(TrueLiteral n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("TrueLiteral: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(FalseLiteral n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("FalseLiteral: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(Identifier n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("Identifier: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(ThisExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ThisExpression: ");
		d += 1;
		n.f0.accept(this);
		d -= 1;
	}

	public void visit(ArrayAllocationExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("ArrayAllocationExpression: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		d -= 1;
	}

	public void visit(AllocationExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("AllocationExpression: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		d -= 1;
	}

	public void visit(NotExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("NotExpression: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		d -= 1;
	}

	public void visit(BracketExpression n) {
		for (int i = 0; i < d; i++)
			System.out.print("    ");
		System.out.println("BracketExpression: ");
		d += 1;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		d -= 1;
	}

}