package utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

//要维护的有3个东西：active队列 翻译时候要用到的Allocation表们 寄存器状态Registers
public class RegisterAllocation {
    int generalRegNum = 18;
    static public int stackPos = 0;
    Registers registers = new Registers();
    //所有interval按区间起始位置排序
    TreeSet<VarInterval>all = new TreeSet<VarInterval>(
        new Comparator<VarInterval>() {
            public int compare(VarInterval a, VarInterval b) {
                if(a == b)//???需要么
                    return 0;
                if(a.begin >= b.begin)
                    return 1;
                else 
                    return -1;
            }
        }
    );

    TreeSet<VarInterval>active = new TreeSet<VarInterval>(
	    new Comparator<VarInterval>() {
		    public int compare(VarInterval a, VarInterval b) {
			    if(a == b)
				    return 0;
			    if(a.end >= b.end)
				    return 1;
			    else
				    return -1;
		    }
        }
    );

    public void registerAllocation() {
        initRA();
        for (Iterator iter = all.iterator(); iter.hasNext();) {//遍历排序后的活性区间
            VarInterval vi = (VarInterval)iter.next();
            if(!vi.name.contains("_") && Integer.parseInt(vi.name)<20) {//TEMP0-19是函数参数
                System.out.println("continue");
                continue;
            }
            System.out.println("beginnn" +vi.begin);
            AllocationTable at;
            if(!GlobalData.raInfo.isEmpty()) {//遇到一个新的活性区间，就先把前一个allocationtable复制下来
                //先复制前一个pos的分配表 深拷贝
                at = new AllocationTable(GlobalData.raInfo.lastEntry().getValue());
            }
            else {
                at = new AllocationTable(vi.begin);
            }
            
            if (GlobalData.raInfo.containsKey(vi.begin)) {//如果前一个分配表的位置和现在这个活性区间的起始位置一样的话，就把前一个分配表从rainfo删了
                System.out.println("remove");
                //GlobalData.raInfo.remove(GlobalData.raInfo.lastKey());
                GlobalData.raInfo.remove(vi.begin);
            }
            //if (!GlobalData.raInfo.containsKey(vi.begin)) {
                //顺序扫描active队列 移除过期变量
                //if(!active.isEmpty()) {
                //for(VarInterval varInActive :active) {
                for(Iterator it = active.iterator(); it.hasNext();) {
                    VarInterval varInActive = (VarInterval)it.next();
                    if(varInActive.end<vi.begin) {
                        it.remove();
                        at.regAlloTable.remove(varInActive.name);
                        registers.clearOne(registers.whichReg(varInActive.name));
                    }
                }
                //}
                //试图将新变量加入队列
                if(active.size() < generalRegNum) {
                    active.add(vi);
                    int rn = registers.getOneFreeGenReg(vi.name);
                    if(rn != -1) {
                        at.regAlloTable.put(vi.name, rn);
                    }
                    else {
                        //报错
                    }
                    
                }
                else {//溢出 
                    //加入待加
                    active.add(vi);
                    VarInterval toSpill = active.last();
                    at.toSpillVarName.add(toSpill.name);
                    //at.toSpillVarName = toSpill.name;
                    at.regAlloTable.remove(toSpill.name);
                    active.remove(toSpill);

                    if (! toSpill.name.equals(vi.name)) {
                        at.spillFrom.add(registers.whichReg(toSpill.name));
                        //at.spillFrom = registers.whichReg(toSpill.name);
                        registers.clearOne(registers.whichReg(toSpill.name));
                        int rn = registers.getOneFreeGenReg(vi.name);
                        if(rn != -1) {
                            at.regAlloTable.put(vi.name, rn);
                        }
                    }

                    at.spill2Stack.put(toSpill.name, stackPos);
                    stackPos++;
                }
            at.pos = vi.begin;
            GlobalData.raInfo.put(at.pos, at);
            System.out.println("add one "+at.pos+"  "+vi.name);
        }
    }

    public void initRA () {
        //将procedure的函数参数加入varInfo
        Iterator it = GlobalData.procedureInfo.entrySet().iterator();
        while(it.hasNext()) {
            ProcInterval proc = (ProcInterval)((Map.Entry) it.next()).getValue();
            for(int i=0; i<proc.x; i++) {
                VarInterval vi = new VarInterval(proc.name+"_"+i, proc.begin, proc.end);
                GlobalData.varinfo.put(proc.name+"_"+i, vi);
            }
            
        }
        //初始化all
        for(VarInterval toadd: GlobalData.varinfo.values()) {
            all.add(toadd);
        }

        for(String t: GlobalData.varinfo.keySet()) {
            System.out.println("    "+t+"   "+GlobalData.varinfo.get(t).begin);
        }

        /*Iterator iter = GlobalData.varinfo.entrySet().iterator();
        while (iter.hasNext()) {
            all.add((VarInterval)((Map.Entry) iter.next()).getValue());
            //GlobalData.varinfo.get(iter.next());
        }*/

        System.out.println("ALL NUM    "+all.size());

    }


    
}