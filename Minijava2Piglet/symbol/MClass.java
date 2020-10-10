package symbol;

import java.util.*;
import utils.*;

import visitor.TypeCheckVisitor;

public class MClass extends MIdentifier{
	private HashMap <String, MMethod> methodList = new HashMap <String, MMethod>();
	private HashMap <String, MVar> varList = new HashMap<String, MVar>();
	private MIdentifier parent;
	private int varNo = 1;
	private int methodNo = 0;
	
	public MClass(String _name, MIdentifier _parent) {
		super(_name, _name);
		parent = _parent;
	}

	public String insertMethod(MMethod newMethod) {
		String methodName = newMethod.getName();
		if (methodList.containsKey(methodName)) {
			return "Error type 2: duplicate definition for method " + methodName;
		}
		methodList.put(methodName, newMethod);
		return null;
	}

	public void deleteMethod(String methodName) {
		methodList.remove(methodName);
	}
	
	public String insertVar(MVar newVar) {
		String varName = newVar.getName();
		if (varList.containsKey(varName)) {
			return "Error type 2: duplicate definition for variable " + varName;
		}
		varList.put(varName,  newVar);
		return null;
	}

	public void deleteVar(String varName) {
		varList.remove(varName);
	}
	
	public String getParentID() {
		if (parent == null) return null;
		return parent.getName();
	}
	
	public MMethod getMethod(String name) {
		return methodList.get(name);
	}
	
	public MVar getVar(String name) {
		return varList.get(name);
	}
	
	public boolean existMethod(String name) {
		return methodList.containsKey(name);
	}
	
	public boolean existVar(String name) {
		return varList.containsKey(name);
	}
	
	public Set<String> getVarNames() {
		return varList.keySet();
	}
	
	public Set<String> getMethodNames() {
		return methodList.keySet();
	}

	public int getVarNo() {
		int ret = varNo;
		varNo++;
		return ret;
	}

	public int getMethodNo() {
		int ret = methodNo;
		methodNo++;
		return ret;
	}	
}
