package toSpiglet;

import Piglet.*;
import Piglet.syntaxtree.*;
import Piglet.visitor.*;
import java.util.*;

import java.io.*;
//import symbol.*;


public class SpigletVisitor extends GJDepthFirst<String,Object> {
    String[] exps = {"StmtExp", "Call", "HAllocate", "BinOp", "Temp", "IntegerLiteral", "Label"};
    //String[] simpleExps = {"Temp", "IntegerLiteral", "Label"};
    
    /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
    public String visit(Goal n, Object argu) {
        Printer.add("MAIN\n");
	    n.f1.accept(this, argu);
	    Printer.add("END\n");
	    n.f3.accept(this, argu);
	    Printer.add("\n");
        return "";
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
    */
    public String visit(StmtList n, Object argu) {
        n.f0.accept(this, argu);
        return "";
    }

    /**
     * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
    public String visit(Procedure n, Object argu) {
        Printer.add(n.f0.accept(this,argu));
	    Printer.add(" [ " + n.f2.f0.toString() + " ]\n");
	    Printer.add("BEGIN\n");
        String exp = n.f4.accept(this,argu);
        if(!exp.startsWith("TEMP")){
            Tmp t = new Tmp();
            Printer.add("MOVE " + t.toString() + " " + exp + "\n");
            exp = t.toString();
        }
	    Printer.add("RETURN " + exp+"\n");
	    Printer.add("END\n");
        return "";
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
    public String visit(Stmt n, Object argu) {
        n.f0.accept(this, argu);
        return "";
    }

    /**
     * f0 -> "NOOP"
    */
    public String visit(NoOpStmt n, Object argu) {
        Printer.add("NOOP\n");
        return "";
    }

    /**
     * f0 -> "ERROR"
    */
    public String visit(ErrorStmt n, Object argu) {
        Printer.add("ERROR\n");
        return "";
    }

    /**
     * f0 -> "CJUMP"
    * f1 -> Exp()
    * f2 -> Label()
    */
    public String visit(CJumpStmt n, Object argu) {
        String e = n.f1.accept(this, argu);
        if(exps[n.f1.f0.which]!="Temp") {
        //if(simpleExps[((Exp)n.f1.f0.choice).f0.which]!="Temp") {
            Tmp t = new Tmp();
            Printer.add("MOVE " + t.toString() + " " + e + "\n");
            e = t.toString();
        }
        Printer.add("CJUMP " + e + " ");
        Printer.add(n.f2.accept(this, argu));
        Printer.add("\n");
        return "";
    }

    /**
     * f0 -> "JUMP"
    * f1 -> Label()
    */
    public String visit(JumpStmt n, Object argu) {
        Printer.add("JUMP ");
	    Printer.add(n.f1.accept(this,argu));
	    Printer.add("\n");
        return "";
    }

    /**
     * f0 -> "HSTORE"
    * f1 -> Exp()
    * f2 -> IntegerLiteral()
    * f3 -> Exp()
    */
    public String visit(HStoreStmt n, Object argu) {
        String e1 = n.f1.accept(this, argu);
        if(exps[n.f1.f0.which]!="Temp") {
        //if(simpleExps[((Exp)n.f1.f0.choice).f0.which]!="Temp") {
            Tmp t = new Tmp();
            Printer.add("MOVE " + t.toString() + " " + e1 + "\n");
            e1 = t.toString();
        }
        String e2 = n.f3.accept(this, argu);
        if(exps[n.f3.f0.which]!="Temp") {
        //if(simpleExps[((Exp)n.f1.f0.choice).f0.which]!="Temp") {
            Tmp t = new Tmp();
            Printer.add("MOVE " + t.toString() + " " + e2 + "\n");
            e2 = t.toString();
        }
        Printer.add("HSTORE " + e1 + " " + n.f2.f0.toString() + " " + e2 + "\n");
        return "";
    }

    /**
     * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Exp()
    * f3 -> IntegerLiteral()
    */
    public String visit(HLoadStmt n, Object argu) {
        String e1 = n.f2.accept(this, argu);
        if(exps[n.f2.f0.which]!="Temp") {
        //if(simpleExps[((Exp)n.f2.f0.choice).f0.which]!="Temp") {
            Tmp t = new Tmp();
            Printer.add("MOVE " + t.toString() + " " + e1 + "\n");
            e1 = t.toString();
        }
        Printer.add("HLOAD " + n.f1.accept(this, argu) + " " + e1 + " " + n.f3.f0.toString() + "\n");
        return "";
    }

    /**
     * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
    public String visit(MoveStmt n, Object argu) {
        String exp = n.f2.accept(this, argu);
        Printer.add("MOVE " + n.f1.accept(this, argu) + " " + exp + "\n");
        return "";
    }

    /**
     * f0 -> "PRINT"
    * f1 -> Exp()
    */
    public String visit(PrintStmt n, Object argu) {
        String exp = n.f1.accept(this, argu);
        if(n.f1.f0.which<4) {
            Tmp t = new Tmp();
            Printer.add("MOVE " + t.toString() + " " + exp + "\n");
            exp = t.toString();
        }
        Printer.add("PRINT " + exp + "\n");
        return "";
    }

    /**
     * f0 -> StmtExp()
    *       | Call()
    *       | HAllocate()
    *       | BinOp()
    *       | Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
    public String visit(Exp n, Object argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> Exp()
    * f4 -> "END"
    */
    public String visit(StmtExp n, Object argu) {
        n.f1.accept(this, argu);
        String exp = n.f3.accept(this, argu);
        return exp;
    }

    /**
     * f0 -> "CALL"
    * f1 -> Exp()
    * f2 -> "("
    * f3 -> ( Exp() )*
    * f4 -> ")"
    */
    public String visit(Call n, Object argu) {
        String exp1 = n.f1.accept(this, argu);
        String es = n.f3.accept(this, argu);
        Tmp t = new Tmp();
        Printer.add("MOVE " + t.toString() + " CALL " + exp1 + "( " + es + " )\n");
        return t.toString();
    }

    /**
     * f0 -> "HALLOCATE"
    * f1 -> Exp()
    */
    public String visit(HAllocate n, Object argu) {
        String ret = "";
        String exp = n.f1.accept(this, argu);
        if(exps[n.f1.f0.which] == "BinOp") {
            Tmp t = new Tmp();
            Printer.add("MOVE " + t.toString() + " " + exp + "\n");
            exp = t.toString();
        }
        ret += "HALLOCATE " + exp + "\n";
        return ret;
    }

    /**
     * f0 -> Operator()
    * f1 -> Exp()
    * f2 -> Exp()
    */
    public String visit(BinOp n, Object argu) {
        String ret = "";
        String e1 = n.f1.accept(this, argu);
        if(exps[n.f1.f0.which]!="Temp") {
        //if(simpleExps[((Exp)n.f1.f0.choice).f0.which]!="Temp") {
            Tmp t = new Tmp();
            Printer.add("MOVE " + t.toString() + " " + e1 + "\n");
            e1 = t.toString();
        }
        String e2 = n.f2.accept(this, argu);
        if(exps[n.f2.f0.which]!="Temp") {
        //if(simpleExps[((Exp)n.f1.f0.choice).f0.which]!="Temp") {
            Tmp t = new Tmp();
            Printer.add("MOVE " + t.toString() + " " + e2 + "\n");
            e2 = t.toString();
        }
        ret += n.f0.accept(this, argu) + " " + e1 + " " + e2 + "\n";
        return ret;
    }

    /**
     * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
    public String visit(Operator n, Object argu) {
        return n.f0.choice.toString();
    }

    /**
     * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
    public String visit(Temp n, Object argu) {
        return "TEMP " + n.f1.f0.toString();
    }

    /**
     * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, Object argu) {
        return n.f0.tokenImage;
    }

    /**
     * f0 -> <IDENTIFIER>
    */
    public String visit(Label n, Object argu) {
        String ret = n.f0.tokenImage + " ";
        return ret;
    }

    public String visit(NodeListOptional n, Object argu) {
        String ret = "";
        
	    for(int i=0; i<n.nodes.size(); i++)
	    {
			String curNode = n.nodes.get(i).accept(this,argu);
		    if(curNode!=null && curNode!="")
	 	    {
                if(!curNode.startsWith("TEMP ")) {
                    Tmp t = new Tmp();
                    Printer.add("MOVE " + t + " " + curNode + "\n");
                    curNode = t.toString();
                }
			    
			}
			if(i>0)
				ret +=" ";
			ret += curNode;
		}
	    return ret;
    }

    public String visit(NodeOptional n, Object argu) {
        if ( n.present() ){
            //System.out.println("?????" + n.node.accept(this, argu));
            Printer.add( n.node.accept(this, argu)); 
        }
            
        return "";
    }


}