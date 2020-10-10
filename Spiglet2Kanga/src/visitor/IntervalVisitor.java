package visitor;

import java.util.Enumeration;
import java.util.HashMap;

import syntaxtree.*;
import utils.LiveVariableSet;
import utils.GlobalData;
import utils.GlobalData.*;

public class IntervalVisitor implements GJVisitor<LiveVariableSet, Integer>{
	
	public LiveVariableSet visit(NodeList n, Integer argu) {
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
	         e.nextElement().accept(this, argu);
		return null;
	}

	public LiveVariableSet visit(NodeListOptional n, Integer argu) {
		if ( n.present() )
	         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
	            e.nextElement().accept(this, argu);
		return null;
	}

	public LiveVariableSet visit(NodeOptional n, Integer argu) {
		if ( n.present() )
			return n.node.accept(this, argu);
		return null;
	}

	public LiveVariableSet visit(NodeSequence n, Integer argu) {
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
	         e.nextElement().accept(this, argu);
		return null;
	}

	public LiveVariableSet visit(NodeToken n, Integer argu) {
		// TODO Auto-generated method stub
		return null;
	}

	   /**
	    * f0 -> "MAIN"
	    * f1 -> StmtList()
	    * f2 -> "END"
	    * f3 -> ( Procedure() )*
	    * f4 -> <EOF>
	    */
	public LiveVariableSet visit(Goal n, Integer argu) {
		return null;
	}
	
	/**
	    * f0 -> ( ( Label() )? Stmt() )*
	    */
	public LiveVariableSet visit(StmtList n, Integer argu) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	    * f0 -> Label()
	    * f1 -> "["
	    * f2 -> IntegerLiteral()
	    * f3 -> "]"
	    * f4 -> StmtExp()
	    */
	public LiveVariableSet visit(Procedure n, Integer argu) {
		// TODO Auto-generated method stub
		return null;
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
	public LiveVariableSet visit(Stmt n, Integer argu) {
		return n.f0.accept(this, argu);
	}

	   /**
	    * f0 -> "NOOP"
	    */
	public LiveVariableSet visit(NoOpStmt n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		if (GlobalData.codelist.containsKey(argu + 1)) {
			set.merge(GlobalData.codelist.get(argu + 1));
		}
		return set;
	}

	   /**
	    * f0 -> "ERROR"
	    */
	public LiveVariableSet visit(ErrorStmt n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		if (GlobalData.codelist.containsKey(argu + 1)) {
			set.merge(GlobalData.codelist.get(argu + 1));
		}
		return set;
	}

	   /**
	    * f0 -> "CJUMP"
	    * f1 -> Temp()
	    * f2 -> Label()
	    */
	public LiveVariableSet visit(CJumpStmt n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		set.addVariable((n.f1.f1.f0.tokenImage));
		int jumppos = GlobalData.jumplist.get(n.f2.f0.tokenImage);
		if (GlobalData.codelist.containsKey(jumppos)) {
			set.merge(GlobalData.codelist.get(jumppos));
		}
		if (GlobalData.codelist.containsKey(argu + 1)) {
			set.merge(GlobalData.codelist.get(argu + 1));
		}
		return set;
	}
	
	/**
	    * f0 -> "JUMP"
	    * f1 -> Label()
	    */
	public LiveVariableSet visit(JumpStmt n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		int jumppos = GlobalData.jumplist.get(n.f1.f0.tokenImage);
		if (GlobalData.codelist.containsKey(jumppos)) {
			set.merge(GlobalData.codelist.get(jumppos));
		}
		return set;
	}

	   /**
	    * f0 -> "HSTORE"
	    * f1 -> Temp()
	    * f2 -> IntegerLiteral()
	    * f3 -> Temp()
	    */
	public LiveVariableSet visit(HStoreStmt n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		if (GlobalData.codelist.containsKey(argu + 1)) {
			set.merge(GlobalData.codelist.get(argu + 1));
		}
		set.addVariable((n.f1.f1.f0.tokenImage));
		set.addVariable((n.f3.f1.f0.tokenImage));
		return set;
	}

	   /**
	    * f0 -> "HLOAD"
	    * f1 -> Temp()
	    * f2 -> Temp()
	    * f3 -> IntegerLiteral()
	    */
	public LiveVariableSet visit(HLoadStmt n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		if (GlobalData.codelist.containsKey(argu + 1)) {
			set.merge(GlobalData.codelist.get(argu + 1));
		}
		set.addVariable((n.f1.f1.f0.tokenImage));
		set.addVariable((n.f2.f1.f0.tokenImage));
		return set;
	}

	   /**
	    * f0 -> "MOVE"
	    * f1 -> Temp()
	    * f2 -> Exp()
	    */
	public LiveVariableSet visit(MoveStmt n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		if (GlobalData.codelist.containsKey(argu + 1)) {
			set.merge(GlobalData.codelist.get(argu + 1));
		}
		set.merge(n.f2.accept(this, argu));
		set.removeVariable(n.f1.f1.f0.tokenImage);
		return set;
	}

	   /**
	    * f0 -> "PRINT"
	    * f1 -> SimpleExp()
	    */
	public LiveVariableSet visit(PrintStmt n, Integer argu) {
		LiveVariableSet set = n.f1.accept(this, argu);
		if (GlobalData.codelist.containsKey(argu + 1)) {
			set.merge(GlobalData.codelist.get(argu + 1));
		}
		return set;
	}

	   /**
	    * f0 -> Call()
	    *       | HAllocate()
	    *       | BinOp()
	    *       | SimpleExp()
	    */
	public LiveVariableSet visit(Exp n, Integer argu) {
		LiveVariableSet set = n.f0.accept(this, argu);
		if (GlobalData.codelist.containsKey(argu + 1)) {
			set.merge(GlobalData.codelist.get(argu + 1));
		}
		return set;
	}

	   /**
	    * f0 -> "BEGIN"
	    * f1 -> StmtList()
	    * f2 -> "RETURN"
	    * f3 -> SimpleExp()
	    * f4 -> "END"
	    */
	public LiveVariableSet visit(StmtExp n, Integer argu) {
		// TODO Auto-generated method stub
		return null;
	}

	   /**
	    * f0 -> "CALL"
	    * f1 -> SimpleExp()
	    * f2 -> "("
	    * f3 -> ( Temp() )*
	    * f4 -> ")"
	    */
	public LiveVariableSet visit(Call n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		set.merge(n.f1.accept(this, argu));
		if (n.f3.present()) {
			for ( Enumeration<Node> e = n.f3.elements(); e.hasMoreElements(); ) {
				Temp tmp = (Temp)(e.nextElement());
				set.addVariable((tmp.f1.f0.tokenImage));
			}
		}
		return set;
	}

	   /**
	    * f0 -> "HALLOCATE"
	    * f1 -> SimpleExp()
	    */
	public LiveVariableSet visit(HAllocate n, Integer argu) {
		return n.f1.accept(this, argu);
	}

	   /**
	    * f0 -> Operator()
	    * f1 -> Temp()
	    * f2 -> SimpleExp()
	    */
	public LiveVariableSet visit(BinOp n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		set.addVariable((n.f1.f1.f0.tokenImage));
		set.merge(n.f2.accept(this, argu));
		return set;
	}

	   /**
	    * f0 -> "LT"
	    *       | "PLUS"
	    *       | "MINUS"
	    *       | "TIMES"
	    */
	public LiveVariableSet visit(Operator n, Integer argu) {
		// TODO Auto-generated method stub
		return null;
	}

	   /**
	    * f0 -> Temp()
	    *       | IntegerLiteral()
	    *       | Label()
	    */
	public LiveVariableSet visit(SimpleExp n, Integer argu) {
		LiveVariableSet set = new LiveVariableSet(argu);
		if (n.f0.choice instanceof Temp) {
			set.addVariable((((Temp)n.f0.choice).f1.f0.tokenImage));
		}
		return set;
	}

	   /**
	    * f0 -> "TEMP"
	    * f1 -> IntegerLiteral()
	    */
	public LiveVariableSet visit(Temp n, Integer argu) {
		// TODO Auto-generated method stub
		return null;
	}

	   /**
	    * f0 -> <INTEGER_LITERAL>
	    */
	public LiveVariableSet visit(IntegerLiteral n, Integer argu) {
		// TODO Auto-generated method stub
		return null;
	}

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	public LiveVariableSet visit(Label n, Integer argu) {
		return null;
	}

}