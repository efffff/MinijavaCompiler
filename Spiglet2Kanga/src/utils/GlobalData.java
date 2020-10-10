package utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

import syntaxtree.Label;
import syntaxtree.Stmt;

public class GlobalData {
	public static HashMap<String, Integer> jumplist = new HashMap<String, Integer>();
	public static HashMap<Integer, LiveVariableSet> codelist = new HashMap<Integer, LiveVariableSet>();
	public static HashMap<Integer, Stmt> codeinfo = new HashMap<Integer, Stmt>();

	public static HashMap<String, VarInterval> varinfo = new HashMap<String, VarInterval>();//key是TEMP号
	public static HashMap<String, ProcInterval> procedureInfo = new HashMap<String, ProcInterval>();
	public static TreeMap<Integer, AllocationTable> raInfo = new TreeMap<Integer, AllocationTable>(
		new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {
				return a.compareTo(b);//升序排序
			}
		}
	);//key是用分配表的pos
	public static Registers registers = new Registers();

	// modified: call statement :<pos, arg_num>, 
	public static TreeMap<Integer, Integer> callInfo = new TreeMap<Integer, Integer> (
		new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {
				return a.compareTo(b);
			}
		}
	);

}
