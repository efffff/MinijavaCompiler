package utils;

import java.util.*;

import symbol.MClass;
import symbol.MMethod;
import symbol.MType;

public class ErrorPrinter {
	static ArrayList <String> errorMsgs = new ArrayList<String>();

	public static void add(String msg) {
		errorMsgs.add(msg);
	}
	public static int getSize() {
		return errorMsgs.size();
	}
	public static void print() {
		Iterator<String> iter = errorMsgs.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	public static void printDebug() {
		for (String myClassName: GlobalData.allClassList.getKeys()) {
			MClass myClass = GlobalData.allClassList.getClassInfo(myClassName);
			System.out.println(myClass.getName() + " " + myClass.getType());
			for (String myMethodName: myClass.getMethodNames()) {
				MMethod myMethod = myClass.getMethod(myMethodName);
				System.out.println("\t" + myMethod.getName() + " " + myMethod.getType());
				for (String myVarName: myMethod.getVarSet()) {
					System.out.println("\t\t" + myVarName + " " + myMethod.getVar(myVarName).getType());
				}
				for (String myParamName: myMethod.getParamSet()) {
					System.out.println("\t\t\t" + myParamName + " " + myMethod.getParam(myParamName).getType());
				}
			}
			for (String myVarName: myClass.getVarNames()) {
				System.out.println("\t" + myVarName + " " + myClass.getVar(myVarName).getType());
			}
		}
	}

}
