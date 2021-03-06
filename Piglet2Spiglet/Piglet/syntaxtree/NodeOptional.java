//
// Generated by JTB 1.3.2
//

package Piglet.syntaxtree;

/**
 * Represents an grammar optional node, e.g. ( A )? or [ A ]
 */
public class NodeOptional implements Node {
   public NodeOptional() {
      node = null;
   }

   public NodeOptional(Node n) {
      addNode(n);
   }

   public void addNode(Node n)  {
      if ( node != null)                // Oh oh!
         throw new Error("Attempt to set optional node twice");

      node = n;
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
   public boolean present()   { return node != null; }

   public Node node;
}

