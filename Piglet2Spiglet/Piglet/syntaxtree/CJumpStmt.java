//
// Generated by JTB 1.3.2
//

package Piglet.syntaxtree;

/**
 * Grammar production:
 * f0 -> "CJUMP"
 * f1 -> Exp()
 * f2 -> Label()
 */
public class CJumpStmt implements Node {
   public NodeToken f0;
   public Exp f1;
   public Label f2;

   public CJumpStmt(NodeToken n0, Exp n1, Label n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public CJumpStmt(Exp n0, Label n1) {
      f0 = new NodeToken("CJUMP");
      f1 = n0;
      f2 = n1;
   }

   public void accept(Piglet.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(Piglet.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(Piglet.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(Piglet.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

