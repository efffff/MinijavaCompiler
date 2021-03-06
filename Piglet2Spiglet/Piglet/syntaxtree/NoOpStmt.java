//
// Generated by JTB 1.3.2
//

package Piglet.syntaxtree;

/**
 * Grammar production:
 * f0 -> "NOOP"
 */
public class NoOpStmt implements Node {
   public NodeToken f0;

   public NoOpStmt(NodeToken n0) {
      f0 = n0;
   }

   public NoOpStmt() {
      f0 = new NodeToken("NOOP");
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

