package toPiglet;

import visitor.*;
import utils.*;
import syntaxtree.*;
import java.util.*;

import java.io.*;
import symbol.*;


public class PigletVisitor extends GJDepthFirst<String,Object>{
    static String tab = "";
    static int lableNo = 0;
    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    public String visit(Goal n, Object argu) {
        String ret = "";
        PrinterPg.add(tab + "MAIN\n");
        tab += "\t";
        n.f0.accept(this, argu);
        tab = tab.substring(0, tab.length()-1);
        PrinterPg.add(tab + "END\n");
        n.f1.accept(this, argu);
        return ret;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    public String visit(MainClass n, Object argu) {
        String className = n.f1.f0.tokenImage;
        MClass mainClass = ((MClassList)argu).getClassInfo(className);
        MMethod method = mainClass.getMethod("main");
        n.f14.accept(this, method);
        n.f15.accept(this, method); 
        return "";
    }

    

	/**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
	public String visit(ClassDeclaration n, Object argu) {
        String className = n.f1.f0.tokenImage;
        MClass curClass = ((MClassList)argu).getClassInfo(className);
        n.f3.accept(this, curClass);
        n.f4.accept(this, curClass); 
		return "";
	}

	/**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
	public String visit(ClassExtendsDeclaration n, Object argu) {
		String className = n.f1.f0.tokenImage;
        MClass curClass = ((MClassList)argu).getClassInfo(className);
        n.f5.accept(this, curClass);
        n.f6.accept(this, curClass); 
		return "";
	}

	/**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"	
    */
	public String visit(VarDeclaration n, Object argu) {
        //System.out.println("VD");
        return "";
    }
		

    /**
     * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
    public String visit(MethodDeclaration n, Object argu) {
        MClass curClass = (MClass)argu;
        String className = curClass.getName();
        String methodName = n.f2.f0.toString();
        MMethod method = GlobalData.allClassList.getClassInfo(className).getMethod(methodName);
        int i = method.getParamSet().size();
        PrinterPg.add(tab + className + "_" + methodName + " [ " + ( i+1 ) + " ]" + "\n");
        tab += "\t";
        PrinterPg.add(tab + "BEGIN\n");
        tab += "\t";
        n.f8.accept(this, method);
        Temp t0 = new Temp();
        PrinterPg.add(tab + "MOVE " + t0.tostring() + " ");
        n.f10.accept(this, method);
        tab = tab.substring(0, tab.length()-1);
        PrinterPg.add("\n" + tab + "RETURN " + t0.tostring() + "\n");
        PrinterPg.add(tab + "END\n");
        tab = tab.substring(0, tab.length()-1);
        //System.out.println("MD");
        //System.out.println(ret);
        return "";
    }

    
    /**
     * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, Object argu) {
        String varName = n.f0.f0.tokenImage;
        MMethod curMethod = (MMethod)argu;
        MClass curClass = curMethod.getBelongClass();
        String curClassName = curClass.getName();
        String curMethodName = curMethod.getName();
        /*String[] s = argu.split(".");
        String curClassName = s[0];
        String curMethodName = s[1];
        MClass curClass = GlobalData.allClassList.getClassInfo(curClassName);
        MMethod curMethod = curClass.getMethod(curMethodName);*/
        String idType = "";
        if(curMethod.existVar(varName)||curMethod.existParam(varName)) {
            MVar var;
            if(curMethod.existVar(varName)){
                var = curMethod.getVar(varName);
            }
            else{
                var = curMethod.getParam(varName);
            }
            idType = var.getType();
            Temp t11 = new Temp(var.methodTempNo);
            PrinterPg.add(tab + "MOVE " + t11.tostring() + " ");
            String rec = n.f2.accept(this, argu);
            if(rec!=null && rec.length()>0) {
                
                //String[] s1 = rec.split(",");
                String runtime = rec;
                if(runtime != idType) {  //!
                    var.setRuntimeType(runtime);
                    curMethod.deleteVar(varName);
                    curMethod.insertVar(var);
                    curClass.deleteMethod(curMethodName);
                    curClass.insertMethod(curMethod);
                    GlobalData.allClassList.deleteClass(curClassName);
                    GlobalData.allClassList.insertClass(curClass);
                }
            }
        }
        else {
            System.out.println("class!!!!!!");
            System.out.println("varName = "+varName);
            System.out.println("curCLass = "+curClassName);
            System.out.println("curMethod = "+curMethodName);
            while(!curClass.existVar(varName)) {
                String parentClass = curClass.getParentID();
                if(parentClass != null)
                    curClass = GlobalData.allClassList.getClassInfo(parentClass);
                else 
                    ErrorPrinter.add("stray field");
            }
            MVar var = curClass.getVar(varName);
            PrinterPg.add(tab + "HSTORE TEMP 0 " + var.classOffset + " ");//还是new Temp(0)？
            idType = var.getType();

            String rec = n.f2.accept(this, argu);
            if(rec!=null && rec.length()>0) {
                String runtime = rec;
                if(runtime != idType) { //!
                    var.setRuntimeType(runtime);
                    curClass.deleteVar(varName);
                    curClass.insertVar(var);
                    GlobalData.allClassList.deleteClass(curClassName);
                    GlobalData.allClassList.insertClass(curClass);
                }
            }
        }
        PrinterPg.add("\n");
        return "";
    }

    /**
     * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
    public String visit(ArrayAssignmentStatement n, Object argu) {
        String varName = n.f0.f0.tokenImage;
        MMethod curMethod = (MMethod)argu;
        MClass curClass = curMethod.getBelongClass();
        String curClassName = curClass.getName();
        String curMethodName = curMethod.getName();
        /*
        String[] s = argu.split(".");
        String curClassName = s[0];
        String curMethodName = s[1];
        MClass curClass = GlobalData.allClassList.getClassInfo(curClassName);
        MMethod curMethod = curClass.getMethod(curMethodName);
        */
        Temp t0 = new Temp();
        PrinterPg.add(tab + "MOVE " + t0.tostring());
        if(curMethod.existVar(varName)) {
            MVar var = curMethod.getVar(varName);
            PrinterPg.add(" " + new Temp(var.methodTempNo).tostring() + "\n");
        }
        else if(curMethod.existParam(varName)) {
            MVar var = curMethod.getParam(varName);
            PrinterPg.add(" " + new Temp(var.methodTempNo).tostring() + "\n");
        }
        else {
            
            while(!curClass.existVar(varName)) {
                String parentClass = curClass.getParentID();
                if(parentClass != null)
                    curClass = GlobalData.allClassList.getClassInfo(parentClass);
                else 
                    ErrorPrinter.add("stray field");
            }
            PrinterPg.add("\n");
            tab += "\t";
            PrinterPg.add(tab + "BEGIN\n");
            tab += "\t";
            Temp t1 = new Temp();
            PrinterPg.add(tab + "HLOAD " + t1.tostring() + " TEMP 0 " + curClass.getVar(varName).classOffset + "\n");
            tab = tab.substring(0, tab.length()-1);
            PrinterPg.add(tab + "RETURN " + t1.tostring() + "\n");
            PrinterPg.add(tab + "END\n");
            tab = tab.substring(0, tab.length()-1);
        }
        Temp t2 = new Temp();
        PrinterPg.add(tab + "MOVE " + t2.tostring() + " ");
        n.f2.accept(this, argu);
        Temp t3 = new Temp();
        PrinterPg.add("\n" + tab + "HLOAD " + t3.tostring() + " " + t0.tostring() + " 0\n");//加载数组0位置得到长度
        int l0 = lableNo;
        PrinterPg.add(tab + "CJUMP LT " + t2.tostring() + " " + t3.tostring() + " L" + l0 + "\n");
        lableNo++;
        int l1 = lableNo;
        PrinterPg.add(tab + "JUMP L" + l1 + "\n");
        lableNo++;
        String ttab = tab.substring(0, tab.length()-1);
        PrinterPg.add("L" + l0 + ttab + "   NOOP\n");
        PrinterPg.add(tab + "ERROR\n");
        PrinterPg.add("L" + l1 + ttab + "   NOOP\n");
        PrinterPg.add(tab + "MOVE " + t0.tostring() + " PLUS " + t0.tostring() + " TIMES 4 PLUS 1 " + t2.tostring() + "\n");
        PrinterPg.add(tab + "HSTORE " + t0.tostring() + " 0 ");
        n.f5.accept(this, argu);
        PrinterPg.add("\n");
        return "";
    }

    /**
     * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
    public String visit(IfStatement n, Object argu) {
        Temp t0 = new Temp();
        PrinterPg.add("MOVE " + t0.tostring() + " ");
        n.f2.accept(this, argu);
        //System.out.println("if " +t0.tostring()+ ret + ")))))))");
        int l0 = lableNo;
        lableNo++;
        PrinterPg.add(tab + "CJUMP " + t0.tostring() + " L" + l0 + "\n");
        n.f4.accept(this, argu);
        int l1 = lableNo;
        lableNo++;
        PrinterPg.add(tab + "JUMP L" + l1 + "\n");
        String ttab = tab.substring(0, tab.length()-1);
        PrinterPg.add("L" + l0 + ttab + "   NOOP\n");
        n.f6.accept(this, argu);
        PrinterPg.add("L" + l1 + ttab + "   NOOP\n");
        return "";
    }

    /**
     * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public String visit(WhileStatement n, Object argu) {
        int l0 = lableNo;
        lableNo++;
        int l1 = lableNo;
        lableNo++;
        int l2 = lableNo;
        lableNo++;
        Temp t = new Temp();
        String ttab = tab.substring(0, tab.length()-1);
        PrinterPg.add("\nL" + l0 + ttab  + "MOVE " + t.tostring() + " ");
        n.f2.accept(this, argu);
        PrinterPg.add( tab + "  CJUMP " + t.tostring() + " L" + l2);
        PrinterPg.add("\nL" + l1 + "  NOOP\n");
        n.f4.accept(this, argu);
        PrinterPg.add("\n" + tab + "JUMP L" + l0);
        PrinterPg.add("\nL" + l2 + "  NOOP\n");
        return "";
    }

    /**
     * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public String visit(PrintStatement n, Object argu) {
        PrinterPg.add(tab + "PRINT ");
        n.f2.accept(this, argu);
        PrinterPg.add("\n");
        return "";
    }

    /**
     * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
    public String visit(Expression n, Object argu) {
        
        /*if(n.f0.choice instanceof PrimaryExpression) {
            int index = ret.indexOf(",");
            if(index>=0) {
                ret = ret.substring(0, index);
            }
        }*/
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
    public String visit(AndExpression n, Object argu) {
        tab += "\t";
        PrinterPg.add("\n" + tab + "BEGIN\n");
        tab += "\t";
        PrinterPg.add(tab + "CJUMP ");
        n.f0.accept(this, argu);
        int l0 = lableNo;
        Temp t = new Temp();
        lableNo++;
        int l1 = lableNo;
        lableNo++;
        PrinterPg.add(" L" + l0 + "\n");
        PrinterPg.add(tab + "CJUMP ");
        n.f2.accept(this, argu);
        PrinterPg.add(" L" + l0 + "\n");
        PrinterPg.add(tab + "MOVE " + t.tostring() + " 1\n");
        PrinterPg.add(tab + "JUMP L" + l1 + "\n");
        String ttab = tab.substring(0, tab.length()-1);
        PrinterPg.add("L" + l0 + ttab + "   NOOP\n");
        PrinterPg.add(tab + "MOVE " + t.tostring() + " 0\n");
        PrinterPg.add("L" + l1 + ttab + "   NOOP\n");
        tab = tab.substring(0, tab.length()-1);
        PrinterPg.add(tab + "RETURN " + t.tostring() + "\n" + tab + "END\n");
        tab = tab.substring(0, tab.length()-1);
        return "";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, Object argu) {
        
        PrinterPg.add(" LT ");
        n.f0.accept(this, argu);
        n.f2.accept(this, argu);
        return "";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, Object argu) {
        PrinterPg.add(" PLUS ");
        n.f0.accept(this, argu);
        n.f2.accept(this, argu);
        return "";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, Object argu) {
        PrinterPg.add(" MINUS ");
        n.f0.accept(this, argu);
        n.f2.accept(this, argu);
        return "";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, Object argu) {
        PrinterPg.add(" TIMES ");
        n.f0.accept(this, argu);
        n.f2.accept(this, argu);
        return "";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, Object argu) {
        tab += "\t";
        PrinterPg.add("\n" + tab + "BEGIN\n");
        tab += "\t";
        Temp t0 = new Temp();
        PrinterPg.add(tab + "MOVE " + t0.tostring() + " ");
        String className = n.f0.accept(this, argu);

        int l = lableNo;
        lableNo++;
        PrinterPg.add(tab + "\nCJUMP LT " + t0.tostring() + " 1 L" + l + "\n");
        PrinterPg.add(tab + "ERROR\n");
        PrinterPg.add("L" + l + " NOOP\n" );

        Temp t1 = new Temp();
        PrinterPg.add(tab + "MOVE " + t1.tostring() + " ");
        n.f2.accept(this, argu);
        Temp t2 = new Temp();
        PrinterPg.add(tab + "HLOAD " + t2.tostring() + " " + t0.tostring() + " 0\n");
        int l1 = lableNo;
        lableNo++;
        int l2 = lableNo;
        lableNo++;
        PrinterPg.add(tab + "CJUMP LT " + t1.tostring() + " " + t2.tostring() + " L" + l1 + "\n");
        PrinterPg.add(tab + "JUMP L" + l2 + "\n");
        String ttab = tab.substring(0, tab.length()-1);
        PrinterPg.add("L" + l1 + ttab + "   NOOP\n");
        PrinterPg.add(tab + "ERROR\n");
        PrinterPg.add("L" + l2 + ttab + "   NOOP\n");
        PrinterPg.add(tab + "MOVE " + t0.tostring() + " PLUS " + t0.tostring() + " TIMES 4 PLUS 1 " + t1.tostring() + "\n");
        Temp t3 = new Temp();
        PrinterPg.add(tab + "HLOAD " + t3.tostring() + " " + t0.tostring() + " 0\n");
        tab = tab.substring(0, tab.length()-1);
        PrinterPg.add(tab + "RETURN " + t3.tostring() + "\n");
        PrinterPg.add(tab + "END\n");
        tab = tab.substring(0, tab.length()-1);
        return className;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public String visit(ArrayLength n, Object argu) {
        String ret = "";
        tab += "\t";
        PrinterPg.add("\n" + tab + "BEGIN\n");
        tab += "\t";
        Temp t0 = new Temp();
        PrinterPg.add(tab + "MOVE " + t0.tostring() + " ");
        n.f0.accept(this, argu);
        Temp t1 = new Temp();
        PrinterPg.add(tab + "HLOAD " + t1.tostring() + " " + t0.tostring() + " 0\n");
        tab = tab.substring(0, tab.length()-1);
        PrinterPg.add(tab + "RETURN " + t1.tostring() + "\n");
        PrinterPg.add(tab + "END\n");
        tab = tab.substring(0, tab.length()-1);
        return "";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public String visit(MessageSend n, Object argu) {

        System.out.println("MMMMMSSSSSS");
        String ret = "";
        PrinterPg.add(tab + "CALL\n");
        tab += "\t";
        PrinterPg.add(tab + "BEGIN\n");
        tab += "\t";
        Temp t0 = new Temp();
        PrinterPg.add(tab + "MOVE " + t0.tostring() + " ");
        
        String pe = n.f0.accept(this, argu);
        
        
        Temp t1 = new Temp();
        PrinterPg.add(tab + "HLOAD " + t1.tostring() + " " + t0.tostring() + " 0\n");
        String className = "";

        System.out.println(ret);


        System.out.println("--------");
        
        if(n.f0.f0.choice instanceof ThisExpression) {
            MMethod curMethod = (MMethod)argu;
            MClass curClass = curMethod.getBelongClass();
            //String[] s = argu.split(".");
            className = curClass.getName();
        }
        else if(n.f0.f0.choice instanceof Identifier) {//有可能上溯造型/多态
            
            className = pe;
            String identifierName = ((Identifier) n.f0.f0.choice).f0.tokenImage;

            System.out.println("identifier name = " + identifierName);
            /*String[] s1 = argu.split(".");
            String curClassName = s1[0];
            String curMethodName = s1[1];*/
            String rt = "";
            if(((MMethod)argu).existVar(identifierName)) {
                System.out.println("var");
                MVar peid = ((MMethod)argu).getVar(identifierName);
                rt = peid.runtimeType;
            }
            else if(((MMethod)argu).existParam(identifierName)) {
                System.out.println("param");
                MVar peid = ((MMethod)argu).getParam(identifierName);
                rt = peid.runtimeType;
            }
            //MVar peid = GlobalData.allClassList.getClassInfo(curClassName).getMethod(curMethodName).getVar(identifierName);
            
            if(rt.length()>0) {
                className = rt;
            }
        }
        else if(n.f0.f0.choice instanceof BracketExpression) {
            //String[] s = pe.split(",");
            System.out.println("llllll pe = "+pe);
            //System.out.println("llllll s[1] = "+ s[1]);
            className = pe;
        }
        else if(n.f0.f0.choice instanceof AllocationExpression) {
            //String[] s = pe.split(",");
            className = pe;
        }
        System.out.println("class name = "+className);
        MClass peClass = GlobalData.allClassList.getClassInfo(className);
        String methodName = n.f2.f0.tokenImage;
        int offset = 0;
        while(!peClass.existMethod(methodName)) {
            String parentClass = peClass.getParentID();
            if(parentClass != null){
                //offset += peClass.getMethodNames().size()*4;
                peClass = GlobalData.allClassList.getClassInfo(parentClass);
            }               
            else 
                ErrorPrinter.add("stray field");
        }
        MMethod method = peClass.getMethod(methodName);
        offset += method.NoinClass*4;
        Temp t2 = new Temp();
        PrinterPg.add(tab + "HLOAD " + t2.tostring() + " " + t1.tostring() + " " + offset + "\n");
        tab = tab.substring(0, tab.length()-1);
        PrinterPg.add(tab + "RETURN " + t2.tostring() + "\n");
        PrinterPg.add(tab + "END ( " + t0.tostring() + " ");
        tab = tab.substring(0, tab.length()-1);
        /*?????
        ExpressionList explist = (ExpressionList)n.f4.node;
        explist.f0.accept(this, argu);
        for (Enumeration<Node> e = explist.f1.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this, argu);
        }*/
        n.f4.accept(this, argu);
        /*String rec1 = "";
        int index1 = rec.indexOf(rec);
        while(index1>=0)
        {
            rec1 += rec.substring(0, index1);
            rec = rec.substring(index+1, rec.length()-index1);
            index1 =
        }
        while
        */
        PrinterPg.add(" )\n");
        //PrinterPg.add("," + className + "?";
        //System.out.print(ret);
        //System.out.print("9999999999");
        return className;
    }

    /**
     * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
    public String visit(ExpressionList n, Object argu) {
        String ret = "";
        n.f0.accept(this, argu);
        PrinterPg.add(" ");
        n.f1.accept(this, argu);
        return "";
    }

    /**
     * f0 -> ","
    * f1 -> Expression()
    */
    public String visit(ExpressionRest n, Object argu) {
        String ret = n.f1.accept(this, argu);
        return ret;
    }

    /**
     * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
    public String visit(PrimaryExpression n, Object argu) {
	    return n.f0.accept(this,argu);
	}

    /**
     * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, Object argu) {
        String ret = "";
        PrinterPg.add(" " + n.f0.toString() + " ");
        return "";
    }

    /**
     * f0 -> "true"
    */
    public String visit(TrueLiteral n, Object argu) {
        PrinterPg.add(" 1 ");
        return "";
    }

    /**
     * f0 -> "false"
    */
    public String visit(FalseLiteral n, Object argu) {
        PrinterPg.add(" 0 ");
        return "";
    }

    /**
     * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, Object argu) {
        String ret = "";
        String className = "";
        String idName = n.f0.tokenImage;
        System.out.println(idName);
        MMethod curMethod = (MMethod)argu;
        MClass curClass = curMethod.getBelongClass();
        String curClassName = curClass.getName();
        String curMethodName = curMethod.getName();
        /*String[] s = argu.split(".");
        String curClassName = s[0];
        String curMethodName = s[1];
        MClass curClass = GlobalData.allClassList.getClassInfo(curClassName);
        MMethod curMethod = curClass.getMethod(curMethodName);*/
        if(GlobalData.allClassList.exist(idName)) {
            className = idName;
        }
        else if(curMethod.existVar(idName)) {
            MVar local = curMethod.getVar(idName);
            PrinterPg.add(" " + new Temp(local.methodTempNo).tostring());
            className = local.getType();
            System.out.println("var!!! TEMP"+local.methodTempNo);
        }
        else if(curMethod.existParam(idName)) {
            MVar param = curMethod.getParam(idName);
            //System.out.println(param.getTempNo());
            //System.out.println("123344");
            Temp tt = new Temp(param.methodTempNo);
            PrinterPg.add(" " + tt.tostring());
            className = param.getType();
            System.out.println("param!!! TEMP"+param.methodTempNo);
            //System.out.println("xxxxxx");
        }
        else {
            while(!curClass.existVar(idName)) {
                String parentClass = curClass.getParentID();
                if(parentClass != null){
                    curClass = GlobalData.allClassList.getClassInfo(parentClass);
                }
                else 
                    ErrorPrinter.add("stray field");
            }
            MVar field = curClass.getVar(idName);
            className = field.getType();
            int offset = field.classOffset;//?

            tab += "\t";
            PrinterPg.add("\n" + tab + "BEGIN\n");
            tab += "\t";
            Temp t = new Temp();
            PrinterPg.add(tab + "HLOAD " + t.tostring() + " TEMP 0 " + offset + "\n");
            tab = tab.substring(0, tab.length()-1);
            PrinterPg.add(tab + "RETURN " + t.tostring() + "\nEND\n");
            tab = tab.substring(0, tab.length()-1);
        }
        return className;
    }

    /**
     * f0 -> "this"
    */
    public String visit(ThisExpression n, Object argu) {
        PrinterPg.add(" TEMP 0\n");
	    return "";
    }

    /**
     * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(ArrayAllocationExpression n, Object argu) {
        String ret= "";
        tab += "\t";
        PrinterPg.add("\n" + tab + "BEGIN\n");
        tab += "\t";
        Temp t0 = new Temp();
        PrinterPg.add(tab + "MOVE " + t0.tostring() + " ");
        n.f3.accept(this, argu);
        Temp t1 = new Temp();
        PrinterPg.add("\n" + tab + "MOVE " + t1.tostring() + " HALLOCATE TIMES 4 PLUS " + t0.tostring() + " 1\n");
        PrinterPg.add(tab + "HSTORE " + t1.tostring() + " 0 " + t0.tostring() + "\n");
        Temp t2 = new Temp();
        PrinterPg.add(tab + "MOVE " + t2.tostring() + " 0\n");
        int l0 = lableNo;
        lableNo++;
        int l1 = lableNo;
        lableNo++;
        String ttab = tab.substring(0, tab.length()-1);
        PrinterPg.add("L" + l0 + ttab + "   NOOP\n");
        PrinterPg.add(tab + "CJUMP LT " + t2.tostring() + " " + t0.tostring() + " L" + l1 + "\n");
        Temp t3 = new Temp();
        PrinterPg.add(tab + "MOVE " +  t3.tostring() + " PLUS " + t1.tostring() + " TIMES 4 PLUS 1 " + t2.tostring() + "\n");
        PrinterPg.add(tab + "HSTORE " + t3.tostring() + " 0 0\n");
        PrinterPg.add(tab + "MOVE " + t2.tostring() + " PLUS " + t2.tostring() + " 1\n");
        PrinterPg.add(tab + "JUMP L" + l0 + "\n");
        PrinterPg.add("L" + l1 + ttab + "   NOOP\n");
        tab = tab.substring(0, tab.length()-1);
        PrinterPg.add(tab + "RETURN " + t1.tostring() + "\n");
        PrinterPg.add(tab + "END\n");
        tab = tab.substring(0, tab.length()-1);
        return ret;
    }


    /**
	 * f0 -> "new"
     * f1 -> Identifier()
	 * f2 -> "("
     * f3 -> ")"
	 */
    public String visit(AllocationExpression n, Object argu) {
        String ret = "";

        String className = n.f1.f0.tokenImage;
        MClass c = GlobalData.allClassList.getClassInfo(className);
        Temp t0 = new Temp();
        //PrinterPg.add(tab + "MOVE " + t0.tostring() + "\n";
        tab += "\t";
        PrinterPg.add("\n" + tab + "BEGIN\n");
        tab += "\t";

        //ALLOCATE Dtable
        Temp dTableAddr = new Temp();
        PrinterPg.add(tab + "MOVE " + dTableAddr.tostring() + " HALLOCATE " + 4*c.getMethodNames().size()+"\n");
        Set<String> methods = c.getMethodNames();
        Iterator<String> iter = methods.iterator();
        //int i = 0;
        while(iter.hasNext())
        {
            String mn = iter.next();
            PrinterPg.add(tab + "HSTORE " + dTableAddr.tostring() + " " + 4*c.getMethod(mn).NoinClass + " " + className + "_" + mn + "\n");
            //i += 1;
        }

        //ALLOCATE Vtable
        Temp vTableAddr = new Temp();
        PrinterPg.add(tab + "MOVE " + vTableAddr.tostring() + " HALLOCATE " + 4*(c.getVarNames().size() + 1) + "\n");
        System.out.println(">>>>varnum = " + c.getVarNames().size());
        for(int j = 1; j <= c.getVarNames().size(); j++) {
            PrinterPg.add(tab + "HSTORE " + vTableAddr.tostring() + " " + 4*j + " 0 " + "\n");
        }
        PrinterPg.add(tab + "HSTORE " + vTableAddr.tostring() + " 0 " + dTableAddr.tostring() + "\n");
        tab = tab.substring(0, tab.length()-1);
        PrinterPg.add(tab + "RETURN\n" + vTableAddr.tostring() + "\n");
        PrinterPg.add(tab + "END\n");
        tab = tab.substring(0, tab.length()-1);
        n.f1.accept(this, argu);

        return className;
    }

    /**
    * f0 -> "!"
    * f1 -> Expression()
    */
    public String visit(NotExpression n, Object argu) {
        String ret = "";
        tab += "\t";
        PrinterPg.add("\n" + tab + "BEGIN\n");
        tab += "\t";
        Temp t = new Temp();
        PrinterPg.add(tab + "CJUMP ");
        n.f1.accept(this, argu);
        int l0 = lableNo;
        lableNo++;
        int l1 = lableNo;
        lableNo++;
        PrinterPg.add(" L" + l0 + "\n");
        PrinterPg.add(tab + "MOVE " + t.tostring() + " 0\n");
        PrinterPg.add(tab + "JUMP L" + l1 + "\n");
        String ttab = tab.substring(0, tab.length()-1);
        PrinterPg.add("L" + l0 + ttab + "   NOOP\n");
        PrinterPg.add(tab + "MOVE " + t.tostring() + " 1\n");
        PrinterPg.add("L" + l1 + ttab + "   NOOP\n");
        tab = tab.substring(0, tab.length()-1);
        PrinterPg.add(tab + "RETURN " + t.tostring() + "\nEND\n");
        tab = tab.substring(0, tab.length()-1);
        return ret;
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, Object argu) {  
        return n.f1.accept(this, argu);
    }

    

}