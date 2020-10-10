package symbol;

import java.util.*;
import toPiglet.*;

import utils.GlobalData;

public class MMethod extends MIdentifier {
	private HashMap <String, MVar> varList = new HashMap<String, MVar> ();
	private LinkedHashMap <String, MVar> paramList = new LinkedHashMap <String, MVar>();
	private String returnType;
	private String belongClass;
	public Iterator<MVar> paramIterator;
	public ArrayList <String> paramCall = new ArrayList<String> ();
	//static int localTempNo;//方法内声明变量匹配TEMP
	private int paramTempNo = 1;
	public int NoinClass;//是在所属类的第几个method

	public MMethod(String _returnType, String _name, String _belong, int _no) {
		super(_returnType, _name);
		belongClass = _belong;
		returnType = _returnType;
		NoinClass = _no;
	}
	
	public MClass getBelongClass() {
		return GlobalData.allClassList.getClassInfo(belongClass);
	}
	
	public void initialize() {
		paramIterator = paramList.values().iterator();
	}

	public void deleteVar(String varName) {
		varList.remove(varName);
	}
	
	public String insertVar(MVar newVar) {
		String varName = newVar.getName();
		if (varList.containsKey(varName)) {
			return "Error type 2: duplicate definition for variable " + varName;
		}
		varList.put(varName,  newVar);
		return null;
	}
	
	public String addParam(MVar newVar) {
		String varName = newVar.getName();
		if (paramList.containsKey(varName)) {
			return "Error type 2: duplicate definition for parameter " + varName;
		}
		paramList.put(varName,  newVar);
		return null;
	}
	
	@Override
	public String getType() {
		return returnType;
	}

	public boolean existVar(String id) {
		return varList.containsKey(id);
	}

	public MVar getVar(String id) {
		return varList.get(id);
	}

	public boolean existParam(String id) {
		return paramList.containsKey(id);
	}

	public MVar getParam(String id) {
		return paramList.get(id);
	}
	
	public Set<String> getParamSet() {
		return paramList.keySet();
	}
	
	public Set<String> getVarSet() {
		return varList.keySet();
	}
	
	public Enumeration<MVar> getParams() {
		return Collections.enumeration(paramList.values());
	}

	public int getLocalTempNo() {
		int ret = Temp.getTempNo20();
		return ret;
	}
	
	public int getParamTempNo() {
		int ret = paramTempNo;
		paramTempNo++;
		return ret;
	}
}
