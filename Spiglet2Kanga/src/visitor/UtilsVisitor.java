package visitor;

import java.util.Enumeration;

import utils.GlobalData;
import utils.LiveVariableSet;
import utils.ProcInterval;
import syntaxtree.*;

public class UtilsVisitor implements GJNoArguVisitor<Integer> {
		static int position = 0;
	   //
	   // Auto class visitors--probably don't need to be overridden.
	   //
	   public Integer visit( NodeList n) {
			for ( Enumeration<Node> e = n.elements(); e.hasMoreElements();)
				e.nextElement().accept(this);
			return 0;
		}

		public Integer visit( NodeListOptional n) {
			int cnt = 0;
			if (n.present())
				for ( Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
					 Node tmp = e.nextElement();
					tmp.accept(this);
					cnt++;
				}
			return cnt;
		}

		public Integer visit( NodeOptional n) {
			if (n.present())
				n.node.accept(this);
			return 0;
		}

		public Integer visit( NodeSequence n) {
			for ( Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
				 Node tmp = e.nextElement();
				if (tmp instanceof NodeOptional) {
					if (((NodeOptional) tmp).present()) {
						if (((NodeOptional) tmp).node instanceof Label) {
							GlobalData.jumplist.put(((Label) ((NodeOptional) tmp).node).f0.tokenImage, (position + 1));
							
						} 
						if (((NodeOptional) tmp).node instanceof Stmt) {
							position++;
							GlobalData.codeinfo.put(position, ((Stmt) ((NodeOptional) tmp).node));
						}
					}
				}
				if(tmp instanceof Stmt) {
					position++;
					GlobalData.codeinfo.put(position, ((Stmt)tmp));
				}
			}
			return 0;
			// e.nextElement().accept(this);
		}

		public Integer visit( NodeToken n) {
			return 0;
		}

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
		public Integer visit( Goal n) {
			 int position_1 = position;
			n.f0.accept(this);
			n.f1.accept(this);
			 int position_2 = position;
			System.out.println(n.f0.toString()+" created");
			System.out.println("p1"+position_1);
			System.out.println("p2"+position_2);
			GlobalData.procedureInfo.put(n.f0.tokenImage, new ProcInterval(n.f0.toString(), position_1, position_2, 0));
			n.f2.accept(this);
			n.f3.accept(this);
			n.f4.accept(this);
			return 0;
		}

		/**
		 * f0 -> ( ( Label() )? Stmt() )*
		 */
		public Integer visit( StmtList n) {
			n.f0.accept(this);
			return 0;
		}

		/**
		 * f0 -> Label() 
		 * f1 -> "[" 
		 * f2 -> IntegerLiteral() 
		 * f3 -> "]" 
		 * f4 -> StmtExp()
		 */
		public Integer visit( Procedure n) {
			 int position_1 = position;
			n.f0.accept(this);
			n.f1.accept(this);
			n.f2.accept(this);
			n.f3.accept(this);
			n.f4.accept(this);
			 int position_2 = position;
			 if(n.f0.f0.tokenImage == "Fac_ComputeFac") {
				 GlobalData.procedureInfo.put(n.f0.f0.tokenImage,
						 new ProcInterval(n.f0.f0.tokenImage, position_1, position_2, Integer.parseInt(n.f2.f0.tokenImage), 2));
			 }
			 else
				GlobalData.procedureInfo.put(n.f0.f0.tokenImage,
					new ProcInterval(n.f0.f0.tokenImage, position_1, position_2, Integer.parseInt(n.f2.f0.tokenImage)));
			return 0;
		}

		/**
		 * f0 -> NoOpStmt() | ErrorStmt() | CJumpStmt() | JumpStmt() | HStoreStmt() |
		 * HLoadStmt() | MoveStmt() | PrintStmt()
		 */
		public Integer visit( Stmt n) {
			
			return n.f0.accept(this);
		}

		/**
		 * f0 -> "NOOP"
		 */
		public Integer visit( NoOpStmt n) {
			n.f0.accept(this);
			return 0;
		}

		/**
		 * f0 -> "ERROR"
		 */
		public Integer visit( ErrorStmt n) {
			n.f0.accept(this);
			return 0;
		}

		/**
		 * f0 -> "CJUMP" f1 -> Temp() f2 -> Label()
		 */
		public Integer visit( CJumpStmt n) {
			n.f0.accept(this);
			n.f1.accept(this);
			n.f2.accept(this);
			return 0;
		}

		/**
		 * f0 -> "JUMP" f1 -> Label()
		 */
		public Integer visit( JumpStmt n) {
			n.f0.accept(this);
			n.f1.accept(this);
			return 0;
		}

		/**
		 * f0 -> "HSTORE" f1 -> Temp() f2 -> IntegerLiteral() f3 -> Temp()
		 */
		public Integer visit( HStoreStmt n) {
			n.f0.accept(this);
			n.f1.accept(this);
			n.f2.accept(this);
			n.f3.accept(this);
			return 0;
		}

		/**
		 * f0 -> "HLOAD" f1 -> Temp() f2 -> Temp() f3 -> IntegerLiteral()
		 */
		public Integer visit( HLoadStmt n) {
			n.f0.accept(this);
			n.f1.accept(this);
			n.f2.accept(this);
			n.f3.accept(this);
			return 0;
		}

		/**
		 * f0 -> "MOVE" f1 -> Temp() f2 -> Exp()
		 */
		public Integer visit( MoveStmt n) {
			n.f0.accept(this);
			n.f1.accept(this);
			n.f2.accept(this);
			return 0;
		}

		/**
		 * f0 -> "PRINT" f1 -> SimpleExp()
		 */
		public Integer visit( PrintStmt n) {
			n.f0.accept(this);
			n.f1.accept(this);
			return 0;
		}

		/**
		 * f0 -> Call() | HAllocate() | BinOp() | SimpleExp()
		 */
		public Integer visit( Exp n) {
			n.f0.accept(this);
			return 0;
		}

		/**
		 * f0 -> "BEGIN" f1 -> StmtList() f2 -> "RETURN" f3 -> SimpleExp() f4 -> "END"
		 */
		public Integer visit( StmtExp n) {
			n.f0.accept(this);
			n.f1.accept(this);
			position++;
			if (n.f3.f0.choice instanceof Temp) {
				 LiveVariableSet set = new LiveVariableSet(position);
				set.addVariable(Integer.parseInt(((Temp) (n.f3.f0.choice)).f1.f0.toString()));
				GlobalData.codelist.put(position, set);
			}
			n.f2.accept(this);
			n.f3.accept(this);
			n.f4.accept(this);
			return 0;
		}

		/**
		 * f0 -> "CALL" f1 -> SimpleExp() f2 -> "(" f3 -> ( Temp() )* f4 -> ")"
		 */
		public Integer visit( Call n) {
			n.f0.accept(this);
			n.f1.accept(this);
			n.f2.accept(this);
			 int cnt = n.f3.accept(this);
			GlobalData.callInfo.put(position, cnt);
			n.f4.accept(this);
			return 0;
		}

		/**
		 * f0 -> "HALLOCATE" f1 -> SimpleExp()
		 */
		public Integer visit( HAllocate n) {
			n.f0.accept(this);
			n.f1.accept(this);
			return 0;
		}

		/**
		 * f0 -> Operator() f1 -> Temp() f2 -> SimpleExp()
		 */
		public Integer visit( BinOp n) {
			n.f0.accept(this);
			n.f1.accept(this);
			n.f2.accept(this);
			return 0;
		}

		/**
		 * f0 -> "LT" | "PLUS" | "MINUS" | "TIMES"
		 */
		public Integer visit( Operator n) {
			n.f0.accept(this);
			return 0;
		}

		/**
		 * f0 -> Temp() | IntegerLiteral() | Label()
		 */
		public Integer visit( SimpleExp n) {
			n.f0.accept(this);
			return 0;
		}

		/**
		 * f0 -> "TEMP" f1 -> IntegerLiteral()
		 */
		public Integer visit( Temp n) {
			n.f0.accept(this);
			n.f1.accept(this);
			return 0;
		}

		/**
		 * f0 -> <INTEGER_LITERAL>
		 */
		public Integer visit( IntegerLiteral n) {
			n.f0.accept(this);
			return 0;
		}

		/**
		 * f0 -> <IDENTIFIER>
		 */
		public Integer visit( Label n) {
	      n.f0.accept(this);
	      return 0;
	   }


}
