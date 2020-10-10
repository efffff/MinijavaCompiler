package utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AllocationTable {
	public int pos;
	public List<String> toSpillVarName = new ArrayList<String>();
	public List<Integer> spillFrom = new ArrayList<Integer>();
	//public String toSpillVarName = null;//如果有溢出情况的话，溢出的Temp序号
	//public int spillFrom = -1;//从哪个寄存器溢出
    public Hashtable<String, Integer> regAlloTable;
    public Hashtable<String, Integer> spill2Stack;//key是temp序号？

    public AllocationTable(int p, Hashtable<String,Integer> rat, Hashtable<String, Integer> s2s)
	{
		pos = p;
		regAlloTable = new Hashtable<String,Integer>(rat);
		spill2Stack = new Hashtable<String,Integer>(s2s);
	}
	public AllocationTable(int p)
	{
		pos = p;
		regAlloTable = new Hashtable<String, Integer>();
		spill2Stack = new Hashtable<String, Integer>();
	}
	public AllocationTable(AllocationTable at)
	{
		pos = at.pos;
		toSpillVarName = at.toSpillVarName;
		spillFrom = at.spillFrom;
		regAlloTable = new Hashtable<String, Integer>(at.regAlloTable);
		spill2Stack = new Hashtable<String, Integer>(at.spill2Stack);
	}
}