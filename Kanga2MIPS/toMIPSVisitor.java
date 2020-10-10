import syntaxtree.*;
import visitor.*;

public class toMIPSVisitor extends GJDepthFirst<String, Object>
{
    static String tab = "          ";
    /**
    * f0 -> "MAIN"
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> "["
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    * f7 -> "["
    * f8 -> IntegerLiteral()
    * f9 -> "]"
    * f10 -> StmtList()
    * f11 -> "END"
    * f12 -> ( Procedure() )*
    * f13 -> <EOF>
    */
    public String visit(Goal n, Object argu) {
        mPrinter.add(tab+".text\n");
        mPrinter.add(tab+".globl           main\n");
        mPrinter.add("main:\n");
        mPrinter.add(tab+"move $fp, $sp\n");
        int x = Integer.parseInt(n.f2.f0.tokenImage);
        int y = Integer.parseInt(n.f5.f0.tokenImage);
        int z = Integer.parseInt(n.f8.f0.tokenImage);
        XYZ xyz = new XYZ();
        xyz.x = x;
        xyz.y = y;
        xyz.z = z;
        if(z>4) z=z-4;
        else z=0;
        int stacklgth = (y+1+z)*4;
        mPrinter.add(tab+"subu $sp, $sp, "+stacklgth+"\n");
        mPrinter.add(tab+"sw $ra, -4($fp)\n");
        n.f10.accept(this, xyz);
        mPrinter.add(tab+"lw $ra, -4($fp)\n");//获取返回地址
        mPrinter.add(tab+"addu $sp, $sp, "+stacklgth+"\n");//弹出栈内容
        mPrinter.add(tab+"j $ra\n\n");//返回
        n.f12.accept(this, argu);

        mPrinter.add(tab+".text\n");
        mPrinter.add(tab+".globl _halloc\n");
        mPrinter.add("_halloc:\n");
        mPrinter.add(tab+"li $v0, 9\n");
        mPrinter.add(tab+"syscall\n");
        mPrinter.add(tab+"j $ra\n\n");

        mPrinter.add(tab+".text\n");
        mPrinter.add(tab+".globl _print\n");
        mPrinter.add("_print:\n");
        mPrinter.add(tab+"li $v0, 1\n");
        mPrinter.add(tab+"syscall\n");
        mPrinter.add(tab+"la $a0, newl\n");
        mPrinter.add(tab+"li $v0, 4\n");
        mPrinter.add(tab+"syscall\n");
        mPrinter.add(tab+"j $ra\n\n");

        mPrinter.add(tab+".data\n");
        mPrinter.add(tab+".align   0\n");
        mPrinter.add("newl:     .asciiz \"\\n\"\n");
        mPrinter.add(tab+".data\n");
        mPrinter.add(tab+".align   0\n");

        mPrinter.add("str_er:   .asciiz \" ERROR: abnormal termination\\n\"\n");
        return null;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
    */
    public String visit(StmtList n, Object argu) {
        n.f0.accept(this, argu);
        return null;
    }

    /**
     * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> "["
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    * f7 -> "["
    * f8 -> IntegerLiteral()
    * f9 -> "]"
    * f10 -> StmtList()
    * f11 -> "END"
    */
    public String visit(Procedure n, Object argu) {
        mPrinter.add(tab+".text\n");
        mPrinter.add(tab+".globl "+tab+n.f0.f0.tokenImage+"\n");
        mPrinter.add(n.f0.f0.tokenImage+":\n");
        int x = Integer.parseInt(n.f2.f0.tokenImage);
        int y = Integer.parseInt(n.f5.f0.tokenImage);
        int z = Integer.parseInt(n.f8.f0.tokenImage);
        XYZ xyz = new XYZ();
        xyz.x = x;
        xyz.y = y;
        xyz.z = z;
        if(z>4) z=z-4;
        else z=0;
        int stacklgth = (y+2+z)*4;
        mPrinter.add(tab+"sw $fp, -8($sp)\n");//保存调用者的帧指针在当前帧第二槽
        mPrinter.add(tab+"move $fp, $sp\n");//更新帧指针位置
        mPrinter.add(tab+"subu $sp, $sp, "+stacklgth+"\n");//开栈，设置本帧栈长，更新栈指针位置
        mPrinter.add(tab+"sw $ra, -4($fp)\n");//将返回地址保存在当前帧第一槽
        
        n.f10.accept(this, xyz);
        mPrinter.add(tab+"lw $ra, -4($fp)\n");//获取返回地址
        int l = stacklgth - 8;
        mPrinter.add(tab+"lw $fp, "+l+"($sp)\n");//获取上帧首址  ???8 or 12
        mPrinter.add(tab+"addu $sp, $sp, "+stacklgth+"\n");//弹出栈内容
        mPrinter.add(tab+"j $ra\n\n");//返回
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
    *       | ALoadStmt()
    *       | AStoreStmt()
    *       | PassArgStmt()
    *       | CallStmt()
    */
    public String visit(Stmt n, Object argu) {
        n.f0.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "NOOP"
    */
    public String visit(NoOpStmt n, Object argu) {
        mPrinter.add(tab+"nop\n");
        return null;
    }

    /**
     * f0 -> "ERROR"
    */
    public String visit(ErrorStmt n, Object argu) {
        mPrinter.add(tab+"li $v0, 4\n");
        mPrinter.add(tab+"la $a0, str_er\n");
        mPrinter.add(tab+"syscall\n");
        mPrinter.add(tab+"li $v0, 10\n");
        mPrinter.add(tab+"syscall\n");
        return null;
    }

    /**
     * f0 -> "CJUMP"
    * f1 -> Reg()
    * f2 -> Label()
    */
    public String visit(CJumpStmt n, Object argu) {
        mPrinter.add(tab+"beqz ");
        n.f1.accept(this, argu);
        mPrinter.add(" "+n.f2.f0.tokenImage+"\n");
        return null;
    }

    /**
     * f0 -> "JUMP"
    * f1 -> Label()
    */
    public String visit(JumpStmt n, Object argu) {
        mPrinter.add(tab+"b "+n.f1.f0.tokenImage+"\n");
        return null;
    }

    /**
     * f0 -> "HSTORE"
    * f1 -> Reg()
    * f2 -> IntegerLiteral()
    * f3 -> Reg()
    */
    public String visit(HStoreStmt n, Object argu) {
        mPrinter.add(tab+"sw ");
        n.f3.accept(this, argu);
        mPrinter.add(", "+n.f2.f0.tokenImage+"(");
        n.f1.accept(this, argu);
        mPrinter.add(")\n");
        return null;
    }

    /**
     * f0 -> "HLOAD"
    * f1 -> Reg()
    * f2 -> Reg()
    * f3 -> IntegerLiteral()
    */
    public String visit(HLoadStmt n, Object argu) {
        mPrinter.add(tab+"lw ");
        n.f1.accept(this, argu);
        mPrinter.add(" "+n.f3.f0.tokenImage+"(");
        n.f2.accept(this, argu);
        mPrinter.add(")\n");
        return null;
    }

    /**
     * f0 -> "MOVE"
    * f1 -> Reg()
    * f2 -> Exp()
    *              -> HAllocate()
    *               | BinOp()
    *               | SimpleExp()
    *                               -> Reg()
    *                                  | IntegerLiteral()
    *                                  | Label()
    */
    public String visit(MoveStmt n, Object argu) {
        String exp = n.f2.accept(this, argu);
        if(n.f2.f0.which == 0) {//HAllocate
            mPrinter.add(tab+"move ");
            n.f1.accept(this, argu);
            mPrinter.add(" $v0\n");
        }
        else if(n.f2.f0.which == 1) {
            BinOp bo = (BinOp)n.f2.f0.choice;
            mPrinter.add(tab+exp+" ");
            n.f1.accept(this, argu);
            mPrinter.add(", $"+bo.f1.f0.choice.toString()+", ");
            if(bo.f2.f0.which == 0) {
                //System.out.println(((Reg)bo.f2.f0.choice).f0.choice.toString());
                mPrinter.add("$"+((Reg)bo.f2.f0.choice).f0.choice.toString()+"\n");
            }
            else if(bo.f2.f0.which == 1) {
                mPrinter.add(((IntegerLiteral)bo.f2.f0.choice).f0.tokenImage+"\n");
            }
        }
        else if(n.f2.f0.which == 2) {
            SimpleExp se = (SimpleExp)n.f2.f0.choice;
            if(se.f0.which == 0) {
                mPrinter.add(tab+"move ");
                n.f1.accept(this, argu);
                //System.out.println(((Reg)se.f0.choice).f0.choice.toString());
                mPrinter.add(" $"+((Reg)se.f0.choice).f0.choice.toString()+"\n");

            }
            else if(se.f0.which == 1) {
                mPrinter.add(tab+"li ");
                n.f1.accept(this, argu);
                mPrinter.add(" "+((IntegerLiteral)se.f0.choice).f0.tokenImage+"\n");
            }
            else {//Label 函数
                mPrinter.add(tab+"la ");
                n.f1.accept(this, argu);
                mPrinter.add(" "+((Label)se.f0.choice).f0.tokenImage+"\n");
            }
        }
        return null;
    }

    /**
     * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
    public String visit(PrintStmt n, Object argu) {
        if(n.f1.f0.which == 0) {//Reg
            mPrinter.add(tab+"move $a0 $"+((Reg)n.f1.f0.choice).f0.choice.toString()+"\n");
            mPrinter.add(tab+"jal _print\n");
        }
        else if(n.f1.f0.which == 1) {
            mPrinter.add(tab+"li $a0 "+((IntegerLiteral)n.f1.f0.choice).f0.toString()+"\n");
            mPrinter.add(tab+"jal _print\n");
        }
        else {
            System.out.println("why Label2");
        }
        return null;
    }

    /**
     * f0 -> "ALOAD"
    * f1 -> Reg()
    * f2 -> SpilledArg()
    */
    public String visit(ALoadStmt n, Object argu) {
        mPrinter.add(tab+"lw ");
        n.f1.accept(this, argu);
        mPrinter.add(", ");
        int num = Integer.parseInt(n.f2.accept(this, argu));
        int x = ((XYZ)argu).x;
        int y = ((XYZ)argu).y;
        int z = ((XYZ)argu).z;
        if(z>4) z = z-4;
        else z = 0;
        
        if(num<x-4) {
            int nnum = num*4;
            mPrinter.add(nnum+"($fp)");
        }
        else {
            int nnum = (num+z)*4;
            mPrinter.add(nnum+"($sp)");
        }
        mPrinter.add("\n");
        return null;
    }

    /**
     * f0 -> "ASTORE"
    * f1 -> SpilledArg()
    * f2 -> Reg()
    */
    public String visit(AStoreStmt n, Object argu) {
        mPrinter.add(tab+"sw ");
        n.f2.accept(this, argu);
        mPrinter.add(", ");
        int num = Integer.parseInt(n.f1.accept(this, argu));
        int x = ((XYZ)argu).x;
        int y = ((XYZ)argu).y;
        int z = ((XYZ)argu).z;
        if(z>4) z = z-4;
        else z = 0;
        
        if(num<x-4) {
            int nnum = num*4;
            mPrinter.add(nnum+"($fp)");
        }
        else {
            int nnum = (num+z)*4;
            mPrinter.add(nnum+"($sp)");
        }
        
        mPrinter.add("\n");
        return null;
    }

    /**
     * f0 -> "PASSARG"
    * f1 -> IntegerLiteral()
    * f2 -> Reg()
    */
    public String visit(PassArgStmt n, Object argu) {
        mPrinter.add(tab+"sw ");
        n.f2.accept(this, argu);
        mPrinter.add(", ");
        int num = Integer.parseInt(n.f1.f0.tokenImage);
        num = num*4 - 4;
        mPrinter.add(num+"($sp)\n");
        return null;
    }

    /**
     * f0 -> "CALL"
    * f1 -> SimpleExp()
    */
    public String visit(CallStmt n, Object argu) {
        mPrinter.add(tab+"jalr $"+((Reg)n.f1.f0.choice).f0.choice.toString()+"\n");
        return null;
    }

    /**
     * f0 -> HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
    public String visit(Exp n, Object argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    *                       -> Reg()
    *                       | IntegerLiteral()
    *                       | Label()
    */
    public String visit(HAllocate n, Object argu) {
        String ret = "";
        if(n.f1.f0.which == 0) {
            mPrinter.add(tab+"move $a0 ");
            n.f1.f0.choice.accept(this, argu);
            mPrinter.add("\n");
            ret = "move";
        }
        else if(n.f1.f0.which == 1) {
            mPrinter.add(tab+"li $a0 "+((IntegerLiteral)n.f1.f0.choice).f0.tokenImage+"\n");
            ret = "li";
        }
        else {
            System.out.println("why Label????");
        }
        mPrinter.add(tab+"jal _halloc\n");
        
        return ret;
    }

    /**
     * f0 -> Operator()
    * f1 -> Reg()
    * f2 -> SimpleExp()
    */
    public String visit(BinOp n, Object argu) {
        String ret = n.f0.accept(this, argu);
        return ret;
    }

    /**
     * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
    public String visit(Operator n, Object argu) {
        switch(n.f0.which) {
            case 0: return "slt";
            case 1: return "add";
            case 2: return "sub";
            case 3: return "mul";
            default: return "error";
        }
    }

    /**
     * f0 -> "SPILLEDARG"
    * f1 -> IntegerLiteral()
    */
    public String visit(SpilledArg n, Object argu) {
        //int num = Integer.parseInt(n.f1.f0.tokenImage);
        //num = num*4;
        //mPrinter.add(num+"($sp)");
        return n.f1.f0.tokenImage;
    }

    /**
     * f0 -> Reg()
    *       | IntegerLiteral()
    *       | Label()
    */
    public String visit(SimpleExp n, Object argu) {
        return null;
    }
    

    /**
     * f0 -> "a0"
    *       | "a1"
    *       | "a2"
    *       | "a3"
    *       | "t0"
    *       | "t1"
    *       | "t2"
    *       | "t3"
    *       | "t4"
    *       | "t5"
    *       | "t6"
    *       | "t7"
    *       | "s0"
    *       | "s1"
    *       | "s2"
    *       | "s3"
    *       | "s4"
    *       | "s5"
    *       | "s6"
    *       | "s7"
    *       | "t8"
    *       | "t9"
    *       | "v0"
    *       | "v1"
    */
    public String visit(Reg n, Object argu) {
        mPrinter.add("$"+n.f0.choice.toString());
        return null;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, Object argu) {
        
        return null;
    }

    /**
     * f0 -> <IDENTIFIER>
    */
    public String visit(Label n, Object argu) {
        String identifier = n.f0.tokenImage;
        if(identifier.startsWith("L")) {
            mPrinter.add(identifier+":");
        }
        
        return null;
    }
}