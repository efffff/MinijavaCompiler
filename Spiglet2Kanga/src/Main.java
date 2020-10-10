import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import syntaxtree.Label;
import syntaxtree.Node;
import syntaxtree.Stmt;
import utils.GlobalData;
import visitor.IntervalVisitor;
import visitor.NodePrinterVisitor;
import visitor.UtilsVisitor;
import visitor.Visitor;
import visitor.toKangaVisitor;
import utils.GlobalData.*;
import utils.LiveVariableSet;
import utils.ProcInterval;
import utils.RegisterAllocation;
import utils.VarInterval;
import utils.kPrinter;

public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			/*
			 * String test =
			 * "D:\\eclipse-workspace\\Spiglet2Kanga\\src\\examples\\Factorial.spg";
			 * InputStream in = new FileInputStream(test); PrintStream out = new
			 * PrintStream( new
			 * FileOutputStream("D:\\eclipse-workspace\\Spiglet2Kanga\\src\\test.txt"));
			 * System.setOut(out); Node root = new SpigletParser(in).Goal(); //
			 * root.accept(new NodePrinterVisitor()); root.accept(new UtilsVisitor());
			 * liveVarAnalysis(); zCalculating(); // RegisterAllocation ra = new
			 * RegisterAllocation(); // ra.registerAllocation(); // all variables are in
			 * GlobalData.varinfo
			 */
			String arg = "/Users/yufeiwang/Downloads/toKanga2/src/test/spg/Factorial.spg";
//			String arg = args[0];
			InputStream in = new FileInputStream(arg);
			String fileName = "";
			if (arg.lastIndexOf("/") >= 0) {
				fileName = arg.substring(arg.lastIndexOf("/") + 1, arg.length() - 4);
			} else {
				fileName = arg.substring(arg.lastIndexOf("\\") + 1, arg.length() - 4);
			}
			Node root = new SpigletParser(in).Goal();

			root.accept(new UtilsVisitor());
			liveVarAnalysis();
			System.out.println("!!!!!!!!!!!!!!!"+GlobalData.varinfo.size());
			Iterator it = GlobalData.varinfo.keySet().iterator();
			while(it.hasNext()) {
				System.out.println(it.next());
			}
			GlobalData.varinfo.clear();

			GlobalData.varinfo.put("24", new VarInterval("24", 1, 5));
			GlobalData.varinfo.put("25", new VarInterval("25", 2, 6));
			GlobalData.varinfo.put("30", new VarInterval("30", 3, 4));
			GlobalData.varinfo.put("23", new VarInterval("23", 6, 10));
			GlobalData.varinfo.put("21", new VarInterval("21", 7, 8));
			GlobalData.varinfo.put("22", new VarInterval("22", 8, 10));
			GlobalData.varinfo.put("31", new VarInterval("31", 9, 10));
			GlobalData.varinfo.put("32", new VarInterval("32", 10, 11));
			GlobalData.varinfo.put("33", new VarInterval("33", 11, 12));
			GlobalData.varinfo.put("34", new VarInterval("34", 13, 14));
			GlobalData.varinfo.put("35", new VarInterval("35", 14, 15));
			GlobalData.varinfo.put("20", new VarInterval("20", 16, 27));
			GlobalData.varinfo.put("29", new VarInterval("29", 18, 23));
			GlobalData.varinfo.put("27", new VarInterval("27", 19, 20));
			GlobalData.varinfo.put("28", new VarInterval("28", 20, 23));
			GlobalData.varinfo.put("36", new VarInterval("36", 21, 22));
			GlobalData.varinfo.put("37", new VarInterval("37", 22, 23));
			GlobalData.varinfo.put("38", new VarInterval("38", 23, 24));
			GlobalData.varinfo.put("39", new VarInterval("39", 24, 25));
			GlobalData.varinfo.put("Fac_ComputeFac_0", new VarInterval("Fac_ComputeFac_0", 13, 27));
			GlobalData.varinfo.put("Fac_ComputeFac_1", new VarInterval("Fac_ComputeFac_1", 13, 27));


			zCalculating();
			//Iterator it = GlobalData.raInfo.
			RegisterAllocation regAllocation = new RegisterAllocation();
			regAllocation.registerAllocation();
			System.out.println("!!!!!!!!!!!!!!!"+GlobalData.raInfo.size());
			Iterator iter = GlobalData.raInfo.keySet().iterator();
			/*while(it.hasNext()) {
				System.out.println(iter.next());
			}*/
			root.accept( new toKangaVisitor(), null);
			
			
			String kangaTranslation = kPrinter.pprint();
			BufferedWriter out = new BufferedWriter(new FileWriter("./src/" + fileName + ".kg"));
            out.write(kangaTranslation);
            out.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public static void zCalculating() {
		for (String var : GlobalData.procedureInfo.keySet()) {
			for (int pos : GlobalData.callInfo.keySet()) {
				ProcInterval interval = GlobalData.procedureInfo.get(var);
				if (pos >= interval.begin && pos <= interval.end) {
					int args = GlobalData.callInfo.get(pos);
					interval.maxArg = (interval.maxArg > args ? interval.maxArg : args);

				}
			}
		}
	}

	public static void liveVarAnalysis() {
		IntervalVisitor visitor = new IntervalVisitor();
		ListIterator<HashMap.Entry<Integer, Stmt>> iter = new ArrayList<HashMap.Entry<Integer, Stmt>>(
				GlobalData.codeinfo.entrySet()).listIterator(GlobalData.codeinfo.size());
		boolean needRepeat = false;
		do {
			needRepeat = false;
			while (iter.hasPrevious()) {
				HashMap.Entry<Integer, Stmt> entry = iter.previous();
				LiveVariableSet set = entry.getValue().accept(visitor, entry.getKey());
				LiveVariableSet set2 = GlobalData.codelist.get(entry.getKey());
				if (set == null) {
					if(set2 != null) {
						needRepeat = true;
						GlobalData.codelist.replace(entry.getKey(), set);
					}
				}
				else {
					if(!set.equal(set2)) {
						needRepeat = true;
						GlobalData.codelist.replace(entry.getKey(), set);
					}
					if(set2 == null) {
						GlobalData.codelist.put(entry.getKey(), set);
					}
				}
			}
		} while (needRepeat);
		for (int pos : GlobalData.codelist.keySet()) {
			LiveVariableSet set = GlobalData.codelist.get(pos);
			for (String var : set.variableSet) {
				if (!GlobalData.varinfo.containsKey(var)) {
					GlobalData.varinfo.put(var, new VarInterval(var, pos, pos));
				} else {
					GlobalData.varinfo.get(var).modifyEnd(pos);
				}
			}
		}
	}
}
