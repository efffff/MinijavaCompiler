package visitor;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.swing.text.rtf.RTFEditorKit;

import syntaxtree.*;
import utils.*;



public class toKangaVisitor extends GJVoidDepthFirst<Object>{
    
    int position = 0;

    public void visit(NodeList n, Object argu) {
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            //System.out.println("NODELIST");
            e.nextElement().accept(this, argu);
        }
     }
  
     public void visit(NodeListOptional n, Object argu) {
        if ( n.present() ) {
           for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            //System.out.println("NODELISTOP");
              e.nextElement().accept(this,argu);
           }
        }
     }
  
     public void visit(NodeOptional n, Object argu) {
        if ( n.present() )
           n.node.accept(this,argu);
     }

    /*public void visit(NodeSequence n, Object argu) {
        //System.out.println("NODE SE");
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
           e.nextElement().accept(this,argu);
           _count++;
        }
     }*/

    public void visit(NodeSequence n, Object argu) {
        //System.out.println("NODE SE");
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            //e.nextElement().accept(this,argu);
            Node tmp = e.nextElement();
               if (tmp instanceof NodeOptional) {
                   if (((NodeOptional)tmp).present()) {
                       if (((NodeOptional)tmp).node instanceof Stmt) {
                           position++;
                           //GlobalData.jumplist.put((Label)((NodeOptional)tmp).node, (position + 1));
                           GlobalData.codeinfo.put(position, (Stmt)((NodeOptional)tmp).node);
                           //break;
                       }
                       //else if (((NodeOptional)tmp).node instanceof Stmt) {
                        //   position++;
                         //  GlobalData.codeinfo.put(position, (Stmt) ((NodeOptional)tmp).node);
                       //}
                   }
               }
               tmp.accept(this, argu);
        }
     }

    //x参数个数 y需要的栈单元个数 z最大参数数目
    //y=（被调用函数体范围内）用到的s寄存器个数+溢出单元+溢出参数（>4个参数）
    public void printFunHead(String function, int x, int y, int z, boolean[] usedSR) {
        kPrinter.add(function + " [" + x + "] [" + y + "] [" + z + "]\n");
        //把所有要用到的s寄存器压栈
        int j = 0;
        for(int i=0; i<8; i++) {
            if(usedSR[i] == true) {
                kPrinter.add("  ASTORE SPILLEDARG " + j + " " + Regs.regName[i+16] + "\n");
                j++;
            }
        }
    }

    public void printFunTail(boolean[] usedSR) {
        int j = 0;
        for(int i=0; i<8; i++) {
            if(usedSR[i] == true) {
                kPrinter.add("  ALOAD " + Regs.regName[i+16] + " SPILLEDARG " + j + "\n");
                j++;
            }
        }
        kPrinter.add("END\n");
    }

    //在Spiglet中参数在前20个TEMP中
    public void getArg(String funcName, int argNum, int pos) {
        while(!GlobalData.raInfo.containsKey(pos)) {
            pos--;
        }
        AllocationTable at = GlobalData.raInfo.get(pos);
        int i = 0;
        while(i<argNum && i<4) {
            System.out.println(funcName+"_"+i);

            int reg = at.regAlloTable.get(funcName+"_"+i);
            kPrinter.add("  MOVE " + Regs.regName[reg] + " a" + i+"\n");
            i++;
        }
        int j = 0;
        while(i<argNum) {
            int reg = at.regAlloTable.get(funcName+"_"+i);
            i++;
            kPrinter.add("  ALOAD "+Regs.regName[reg]+" SPILLEDARG "+j);
            j++;
        }
    }

    public String readFromTemp(int temp, int pos, int c) {
        //应该从pos往前找第一个最近的分配表，read没有加入新temp的可能
        while(!GlobalData.raInfo.containsKey(pos)) {
            pos--;
        }
        String t = "";
        if(temp>=20){
            t = Integer.toString(temp);
        }
        else {
            for(ProcInterval proce: GlobalData.procedureInfo.values()) {
                if(proce.begin<pos && proce.end>=pos) {
                    t = proce.name + "_" + Integer.toString(temp);
                }
            }
        }
        AllocationTable at = GlobalData.raInfo.get(pos);
        if(at.regAlloTable.containsKey(t)) {
            int re = at.regAlloTable.get(t);
            return Regs.regName[re];
        }
        else if(at.spill2Stack.containsKey(t)) {
            int st = at.spill2Stack.get(t);
            kPrinter.add("  ALOAD "+Regs.regName[c+4]+" SPILLEDARG "+st+"\n");
            return Regs.regName[c+4];
        }
        return null;
    }

    public String writeToTemp(int temp, int pos, int c) {
        //应该就在pos处会有分配表，这个temp应该是新出现的
        //if(pos == 0) pos =1;
        while(!GlobalData.raInfo.containsKey(pos)) {
            pos--;
        }
        String t = "";
        if(temp>=20){
            t = Integer.toString(temp);
        }
        else {
            for(ProcInterval proce: GlobalData.procedureInfo.values()) {
                if(proce.begin<pos && proce.end>=pos) {
                    t = proce.name + "_" + Integer.toString(temp);
                }
            }
        }
        AllocationTable at = GlobalData.raInfo.get(pos);
        if(at ==null)System.out.println("nullllll");
        System.out.println(pos);
        if(at.regAlloTable!=null 
        && at.regAlloTable.containsKey(t)){
            System.out.println("isReg");
            if(!at.toSpillVarName.isEmpty()) {//别人溢出
                for(int i=0; i<at.toSpillVarName.size(); i++)
                kPrinter.add("  ASTORE SPILLEDARG "+at.spill2Stack.get(at.toSpillVarName.get(i))+" "+Regs.regName[at.spillFrom.get(i)]);
            }
            int re = at.regAlloTable.get(t);
            System.out.println("re ======"+Regs.regName[re]);
            return Regs.regName[re];
        }
        else if(at.spill2Stack.containsKey(t)) {//自己溢出
            int st = at.spill2Stack.get(t);
            
            return Regs.regName[c+4];
        }
        return null;
        
    }

    /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
    public String simpleExp(SimpleExp n, int c) {
        switch(n.f0.which) {
            case 0: Temp t = (Temp)n.f0.choice;
            System.out.println("??"+t.f1.f0.tokenImage);
            return readFromTemp(Integer.parseInt(t.f1.f0.tokenImage), position, 0);
            case 1: return ((IntegerLiteral)n.f0.choice).f0.toString();
            case 2: return ((Label)n.f0.choice).f0.toString();
            default: return null;
        }
    }

    /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
    public void visit(Goal n, Object argu) {
        n.f0.accept(this, argu);
        Iterator it = GlobalData.procedureInfo.keySet().iterator();
        System.out.println(it.next().toString());
        ProcInterval pInterval = GlobalData.procedureInfo.get(n.f0.tokenImage);
        System.out.println(pInterval.name);
        int pBegin = pInterval.begin;
        System.out.println("begin"+pBegin);
        int pEnd = pInterval.end;
        System.out.println("end"+pEnd);
        boolean[] sReg = new boolean[8];
        for(int i=0; i<8; i++) {
            sReg[i] = false;
        }
        int sRegUsedCnt = 0;
        int spilledCnt = 0;
        Iterator iter = GlobalData.raInfo.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            //System.out.println(entry.);
            if((Integer)entry.getKey() >= pBegin) {
                break;
            }
        }
        if(iter.hasNext()) {
            do {
                Map.Entry<Integer, AllocationTable> entry = (Map.Entry)iter.next();
                Integer k = entry.getKey();
                if(k>pEnd) break;
                AllocationTable a = entry.getValue();
                for (Integer v : a.regAlloTable.values()) {
                    if(v>=16 && v<24) {
                        sReg[v-16] = true;
                    }
                }
                if(a.spill2Stack.size()>spilledCnt) {
                    spilledCnt = a.spill2Stack.size();
                }
            } while (iter.hasNext());
        }
        
        for(int i=0; i<8; i++) {
            if(sReg[i] == true) {
                sRegUsedCnt++;
            }
        }
        System.out.println("maxxxxxxxxx       "+pInterval.maxArg);
        printFunHead("MAIN", 0, sRegUsedCnt + spilledCnt, pInterval.maxArg, sReg);
        //GlobalData.registers.clearAll();
        //getArg(0);
        n.f1.accept(this, argu);
        printFunTail(sReg);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
    */
    public void visit(StmtList n, Object argu) {
        System.out.println("STMTLIST");
        n.f0.accept(this, argu);
    }

    /**
     * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
    public void visit(Procedure n, Object argu) {
        ProcInterval pInterval = GlobalData.procedureInfo.get(n.f0.f0.tokenImage);
        int pBegin = pInterval.begin;
        int pEnd = pInterval.end;
        boolean[] sReg = new boolean[8];
        for(int i=0; i<8; i++) {
            sReg[i] = false;
        }
        int sRegUsedCnt = 0;
        int spilledCnt = 0;
        Iterator iter = GlobalData.raInfo.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            if((Integer)entry.getKey() == pBegin) {
                break;
            }
        }
        if(iter.hasNext()) {
            do {
                Map.Entry<Integer, AllocationTable> entry = (Map.Entry)iter.next();
                Integer k = entry.getKey();
                if(k>pEnd) break;
                AllocationTable a = entry.getValue();
                for (Integer v : a.regAlloTable.values()) {
                    if(v>=16 && v<24) {
                        sReg[v-16] = true;
                    }
                }
                if(a.spill2Stack.size()>spilledCnt) {
                    spilledCnt = a.spill2Stack.size();//什么时候出栈好像没写
                }
            } while (iter.hasNext());
        }
        for(int i=0; i<8; i++) {
            if(sReg[i] == true) {
                sRegUsedCnt++;
            }
        }
        String fname = n.f0.f0.tokenImage;
        System.out.println("!!!"+fname);
        int argNum = Integer.parseInt(n.f2.f0.tokenImage);
        printFunHead(fname, argNum, sRegUsedCnt + spilledCnt, pInterval.maxArg, sReg);
        //GlobalData.registers.clearAll();
        getArg(fname, argNum, position);
        n.f4.accept(this, argu);
        printFunTail(sReg);
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
    public void visit(Stmt n, Object argu) {
        //System.out.println("STMT");
        position++;
        n.f0.accept(this, argu);
    }

    /**
     * f0 -> "NOOP"
    */
    public void visit(NoOpStmt n, Object argu) {
        kPrinter.add("  NOOP\n");
    }

    /**
     * f0 -> "ERROR"
    */
    public void visit(ErrorStmt n, Object argu) {
        kPrinter.add("  ERROR\n");
    }

    /**
     * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
    public void visit(CJumpStmt n, Object argu) {
        String t = readFromTemp(Integer.parseInt(n.f1.f1.f0.tokenImage), position, 0);

        kPrinter.add("  CJUMP " + t + " ");
        n.f2.accept(this, null);
        kPrinter.add("\n");
    }

    /**
     * f0 -> "JUMP"
    * f1 -> Label()
    */
    public void visit(JumpStmt n, Object argu) {
        kPrinter.add(" JUMP ");
        n.f1.accept(this, null);
        kPrinter.add("\n");
    }

    /**
     * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
    public void visit(HStoreStmt n, Object argu) {
        int tt1 = Integer.parseInt(n.f1.f1.f0.tokenImage);
        int tt2 = Integer.parseInt(n.f3.f1.f0.tokenImage);
        String t1 = readFromTemp(tt1, position, 0);
        String t2 = readFromTemp(tt2, position, 1);
        kPrinter.add("  HSTORE "+t1+" ");
        n.f2.accept(this, argu);
        kPrinter.add(" "+t2+"\n");
    }

    /**
     * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
    public void visit(HLoadStmt n, Object argu) {
        int tt1 = Integer.parseInt(n.f1.f1.f0.tokenImage);
        int tt2 = Integer.parseInt(n.f2.f1.f0.tokenImage);
        String t1 = writeToTemp(tt1, position, 0);
        String t2 = readFromTemp(tt2, position, 1);
        kPrinter.add("  HLOAD "+t1+" "+t2+" ");
        n.f3.accept(this, argu);
        kPrinter.add("\n");
        //??????
    }

    /**
     * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
    public void visit(MoveStmt n, Object argu) {
        System.out.println("MOVE");
        String t = writeToTemp(Integer.parseInt(n.f1.f1.f0.tokenImage), position, 0);
        System.out.println("temppppp"+t);
        switch(n.f2.f0.which) {
            case 0: { //Call
                n.f2.accept(this, argu);
                kPrinter.add("  MOVE " + t + " v0\n");
                return;
            }
            default: { //因为kanga的语法中，MOVE的定义除了不能嵌套call指令之外，其他和spiglet是一样的，因此直接writeToTemp+递归打印exp即可
                kPrinter.add("  MOVE " + t);
                System.out.println("  MOVE " + t);
                n.f2.accept(this, argu); 
                return;
            }
        }
    }

    /**
     * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
    public void visit(PrintStmt n, Object argu) {
        String s = simpleExp(n.f1, 0);
        kPrinter.add("  PRINT "+s+"\n");
    }

    /**
     * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
    public void visit(Exp n, Object argu) {
        n.f0.accept(this, argu);
    }

    /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
    public void visit(HAllocate n, Object argu) {
        String s = simpleExp(n.f1, 0);
        kPrinter.add(" HALLOCATE "+s+"\n");

    }

    /**
     * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
    public void visit(BinOp n, Object argu) {
        int tt = Integer.parseInt(n.f1.f1.f0.tokenImage);
        String t = readFromTemp(tt, position, 0);
        String e = simpleExp(n.f2, 1);
        kPrinter.add(" ");
        n.f0.accept(this, argu);
        kPrinter.add(" "+t+" "+e+"\n");
    }

    /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
    public void visit(SimpleExp n, Object argu) {
        String s = simpleExp(n, 0);
        kPrinter.add(" "+s+"\n");

        //n.f0.accept(this, argu);
    }

    /**
     * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
    public void visit(StmtExp n, Object argu) {
        n.f1.accept(this, argu);
        position++;
        String s = simpleExp(n.f3, 0);
        kPrinter.add("  MOVE v0 "+s+"\n");
    }

    /**
     * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
    public void visit(Call n, Object argu) {
        int i;
        Iterator<Node> iter = n.f3.nodes.iterator();
        int argNum = n.f3.size();
        for(i=0; i<4&&i<argNum; i++) {
            int t = Integer.parseInt(((Temp)iter.next()).f1.f0.tokenImage);
            String s = readFromTemp(t, position, 0);
            kPrinter.add("  MOVE a"+i+" "+s+"\n");
        }
        for(int j=1; i<argNum; i++, j++) {
            int t = Integer.parseInt(((Temp)iter.next()).f1.f0.tokenImage);
            String s = readFromTemp(t, position, 0);
            kPrinter.add("  PASSARG "+j+" "+s+"\n");
        }
        String s1 = simpleExp(n.f1, 0);
        kPrinter.add("  CALL "+s1+"\n");
    }


    /**
     * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
    public void visit(Operator n, Object argu) {
        switch(n.f0.which) {
		    case 0: kPrinter.add(" LT ");   return;
            case 1: kPrinter.add(" PLUS "); return;
            case 2: kPrinter.add(" MINUS "); return;
            case 3: kPrinter.add(" TIMES "); return;
		}
    }


    /**
     * f0 -> <INTEGER_LITERAL>
    */
    public void visit(IntegerLiteral n, Object argu) {
        kPrinter.add(" "+n.f0.tokenImage+" ");
    }

    /**
     * f0 -> <IDENTIFIER>
    */
    public void visit(Label n, Object argu) {
        kPrinter.add(" "+n.f0.tokenImage+" ");
        System.out.println("ID "+n.f0.tokenImage);
        n.f0.accept(this, argu);
    }
}