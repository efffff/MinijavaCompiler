import visitor.*;
import utils.*;
import syntaxtree.*;
import java.util.*;
import java.io.*;
import symbol.*;
import toPiglet.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//String test = "/Users/yufeiwang/Downloads/Compiler-master/src/testsample/m_program/Factorial.java";
			InputStream in = new FileInputStream(args[0]);
			String fileName = "";
			if(args[0].lastIndexOf("/") >= 0){
				fileName = args[0].substring(args[0].lastIndexOf("/")+1, args[0].length()-5);
			}
			else {
				fileName = args[0].substring(args[0].lastIndexOf("\\")+1, args[0].length()-5);
			}
			Node root = new MiniJavaParser(in).Goal();
			root.accept(new BuildSymbolTableVisitor(), GlobalData.allClassList);
			//traverse the allClassList
			Set <String> keyset = GlobalData.allClassList.getKeys();
			for (String str: keyset) {
				//if (visited.contains(str)) continue;
				MClass curclass = GlobalData.allClassList.getClassInfo(str);
				dfs(GlobalData.allClassList, curclass);
			}
			//ErrorPrinter.printDebug();
			//root.accept(new NodePrintVisitor());
			root.accept(new TypeCheckVisitor(), GlobalData.allClassList);
			if (ErrorPrinter.getSize() == 0) {
				//System.out.println("Successful");
			}
			else {
				ErrorPrinter.print();
			}
			root.accept(new PigletVisitor(), GlobalData.allClassList);
			String pigletTranslation = PrinterPg.pprint();
			System.out.println(pigletTranslation);
			//File file = new File("/Users/yufeiwang/Downloads/Compiler-master/src/testsample/pg/" + fileName + ".pg");
			//file.createNewFile();

			BufferedWriter out = new BufferedWriter(new FileWriter("./" + fileName + ".pg"));
            out.write(pigletTranslation);
            out.close();

			/*
			FileOutputStream out = new FileOutputStream(file);
			byte bytes[] = new byte[99999999];
			bytes = pigletTranslation.getBytes();
			out.write(bytes);
			out.close();
			*/

		} catch (Exception e) {
			ErrorPrinter.print();
			e.printStackTrace();
		}
	}
	
	static HashSet <String> visited = new HashSet <String>();
	public static void dfs(MType allClassList, MClass curClass) {
		String parentClass = curClass.getParentID();
		if (parentClass == null) return;
		MClass pClass = GlobalData.allClassList.getClassInfo(parentClass);
		for (String methodName: pClass.getMethodNames()) {
			String msg = curClass.insertMethod(
					new MMethod(pClass.getMethod(methodName).getType(), 
							methodName, pClass.getName(), pClass.getMethod(methodName).NoinClass));
			if (msg != null) {
				//examine the parameter list to determine if it's override
				MMethod method = curClass.getMethod(methodName);
				MMethod pmethod = pClass.getMethod(methodName);
				method.initialize();
				pmethod.initialize();
				while (method.paramIterator.hasNext() 
						&& pmethod.paramIterator.hasNext()) {
					if (method.paramIterator.next().getType() != 
							pmethod.paramIterator.next().getType()) {
						ErrorPrinter.add("Error Type 4: Overload "
								+ "for method " + methodName);
						break; // to be revised to determine whether it's break for further use
					}
				}
				if (method.paramIterator.hasNext() || 
						pmethod.paramIterator.hasNext()) {
					ErrorPrinter.add("Error Type 4: Overload "
							+ "for method " + methodName);
					break; // to be revised to determine whether it's break for further use
				}
			}
		}
		for (String varName: pClass.getVarNames()) {
			curClass.insertVar(new MVar(pClass.getVar(varName).getType(), varName, pClass.getVar(varName).classOffset, pClass.getVar(varName).methodTempNo));
		}
		visited.add(curClass.getName());
		if (visited.contains(parentClass)) {
			ErrorPrinter.add("Error Type 6: extending "
					+ "relationships make a loop for "
					+ parentClass + " and " + curClass.getName() + "!");
		}
		else {
			dfs(allClassList, ((MClassList)allClassList).getClassInfo(parentClass));
		}
		visited.remove(curClass.getName());
	}

}
