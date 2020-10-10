package visitor;

import java.util.Enumeration;

import syntaxtree.*;

public class NodePrinterVisitor implements Visitor{
		public int d = 0;
	   //
	   // Auto class visitors--probably don't need to be overridden.
	   //
	   public void visit(NodeList n) {
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
	         e.nextElement().accept(this);
	   }

	   public void visit(NodeListOptional n) {
	      if ( n.present() )
	         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
	            e.nextElement().accept(this);
	   }

	   public void visit(NodeOptional n) {
	      if ( n.present() )
	         n.node.accept(this);
	   }

	   public void visit(NodeSequence n) {
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
	         e.nextElement().accept(this);
	   }

	   public void visit(NodeToken n) { }

	   //
	   // User-generated visitor methods below
	   //

	   /**
	    * f0 -> "MAIN"
	    * f1 -> StmtList()
	    * f2 -> "END"
	    * f3 -> ( Procedure() )*
	    * f4 -> <EOF>
	    */
	   public void visit(Goal n) {
		  for (int i = 0; i < d; i++) {
			  System.out.print("    ");
		  }
		  System.out.println("Goal: ");
		  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      n.f3.accept(this);
	      n.f4.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> ( ( Label() )? Stmt() )*
	    */
	   public void visit(StmtList n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("StmtList: ");
			  d += 1;
	      n.f0.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> Label()
	    * f1 -> "["
	    * f2 -> IntegerLiteral()
	    * f3 -> "]"
	    * f4 -> StmtExp()
	    */
	   public void visit(Procedure n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("Procedure: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      n.f3.accept(this);
	      n.f4.accept(this);
	      d -=1;
	   }

	   /**
	    * f0 -> NoOpStmt()
	    *       | ErrorStmt()
	    *       | CJumpStmt()
	    *       | JumpStmt()
	    *       | HStoreStmt()
	    *       | HLoadStmt()
	    *       | MoveStmt()
	    *       | PrintStmt()
	    */
	   public void visit(Stmt n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("Stmt: ");
			  d += 1;
	      n.f0.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "NOOP"
	    */
	   public void visit(NoOpStmt n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("NoOpStmt: ");
			  d += 1;
	      n.f0.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "ERROR"
	    */
	   public void visit(ErrorStmt n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("ErrorStmt: ");
			  d += 1;
	      n.f0.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "CJUMP"
	    * f1 -> Temp()
	    * f2 -> Label()
	    */
	   public void visit(CJumpStmt n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("CJumpStmt: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "JUMP"
	    * f1 -> Label()
	    */
	   public void visit(JumpStmt n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("JumpStmt: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "HSTORE"
	    * f1 -> Temp()
	    * f2 -> IntegerLiteral()
	    * f3 -> Temp()
	    */
	   public void visit(HStoreStmt n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("HStoreStmt: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      n.f3.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "HLOAD"
	    * f1 -> Temp()
	    * f2 -> Temp()
	    * f3 -> IntegerLiteral()
	    */
	   public void visit(HLoadStmt n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("HLoadStmt: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      n.f3.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "MOVE"
	    * f1 -> Temp()
	    * f2 -> Exp()
	    */
	   public void visit(MoveStmt n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("MoveStmt: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "PRINT"
	    * f1 -> SimpleExp()
	    */
	   public void visit(PrintStmt n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("SimpleExp: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> Call()
	    *       | HAllocate()
	    *       | BinOp()
	    *       | SimpleExp()
	    */
	   public void visit(Exp n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("Exp: ");
			  d += 1;
	      n.f0.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "BEGIN"
	    * f1 -> StmtList()
	    * f2 -> "RETURN"
	    * f3 -> SimpleExp()
	    * f4 -> "END"
	    */
	   public void visit(StmtExp n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("StmtExp: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      n.f3.accept(this);
	      n.f4.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "CALL"
	    * f1 -> SimpleExp()
	    * f2 -> "("
	    * f3 -> ( Temp() )*
	    * f4 -> ")"
	    */
	   public void visit(Call n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("Call: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      n.f3.accept(this);
	      n.f4.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "HALLOCATE"
	    * f1 -> SimpleExp()
	    */
	   public void visit(HAllocate n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("HAllocate: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> Operator()
	    * f1 -> Temp()
	    * f2 -> SimpleExp()
	    */
	   public void visit(BinOp n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("Binop: ");
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "LT"
	    *       | "PLUS"
	    *       | "MINUS"
	    *       | "TIMES"
	    */
	   public void visit(Operator n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("Operator: " + n.f0.toString());
			  d += 1;
	      n.f0.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> Temp()
	    *       | IntegerLiteral()
	    *       | Label()
	    */
	   public void visit(SimpleExp n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("SimpleExp: ");
			  d += 1;
	      n.f0.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> "TEMP"
	    * f1 -> IntegerLiteral()
	    */
	   public void visit(Temp n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("Temp: " + n.f0.toString());
			  d += 1;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> <INTEGER_LITERAL>
	    */
	   public void visit(IntegerLiteral n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("IntegerLiteral: " + n.f0.toString());
			  d += 1;
	      n.f0.accept(this);
	      d -= 1;
	   }

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	   public void visit(Label n) {
		   for (int i = 0; i < d; i++) {
				  System.out.print("    ");
			  }
			  System.out.println("Label: " + n.f0.toString());
			  d += 1;
	      n.f0.accept(this);
	      d -= 1;
	   }
}
