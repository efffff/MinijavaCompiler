package utils;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class LiveVariableSet {
	public int position;
	public Set<String> variableSet;
	public LiveVariableSet(int _position) {
		position = _position;
		variableSet = new TreeSet<String>();
	}
	public void addVariable(int varNum) {
		variableSet.add(Integer.toString(varNum));
	}
	public void addVariable(String varNum) {
		variableSet.add(varNum);
	}
	public void removeVariable(String varNum) {
		for (String var: variableSet) {
			if (var == varNum) {
				variableSet.remove(var);
				break;
			}
		}
	}
	public void merge(LiveVariableSet other) {
		for (String var: other.variableSet) {
			addVariable(var);
		}
	}
	public boolean equal(LiveVariableSet other) {
		if(other == null) return false;
		if (position != other.position) return false;
		else {
			if (variableSet.size() != other.variableSet.size()) return false;
			else {
				Iterator<String> self = variableSet.iterator();
				Iterator<String> oth = other.variableSet.iterator();
				while (self.hasNext() && oth.hasNext()) {
					if (self.next() != oth.next()) return false;
				}
			}
		}
		return true;
	}
}
