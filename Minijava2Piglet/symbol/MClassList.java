package symbol;

import java.util.*;

public class MClassList extends MType {
	private HashMap <String, MClass> classList = new HashMap<String, MClass> ();
	
	public MClassList() {
		super("class list");
	}
	
	public int getClassNum() {
		return classList.size();
	}
	
	public String insertClass(MClass newClass) {
		String className = newClass.getName();
		if (classList.containsKey(className)) { // to be revised
			return "Error type 2: duplicate definition for class " + className;
		}
		classList.put(className, newClass);
		return null;
	}

	public void deleteClass(String className) {
		classList.remove(className);
	}
	
	public MClass getClassInfo(String name) {
		return classList.get(name);
	}
	
	public Set<String> getKeys() {
		return classList.keySet();
	}
	
	public boolean exist(String id) {
		return classList.containsKey(id);
	}
	
}
