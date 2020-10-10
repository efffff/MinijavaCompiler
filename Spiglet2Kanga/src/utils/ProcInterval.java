package utils;

public class ProcInterval {
	public String name;
	public int begin = 0;
	public int end = 0;
	public int x;//参数个数
	public int maxArg;
	public ProcInterval(String _name, int _begin, int _end, int _x) {
		name = _name;
		begin = _begin;
		end = _end;
		x = _x;
	}
	public ProcInterval(int _begin, int _end, int _x) {
		begin = _begin;
		end = _end;
		x = _x;
	}

	public ProcInterval(String _name, int _begin, int _end, int _x, int _maxArg) {
		name = _name;
		begin = _begin;
		end = _end;
		x = _x;
		maxArg = _maxArg;
	}

	public void setMaxArg(int _maxArg) {
		maxArg = _maxArg;
	}

	public int getBegin() {
		return begin;
	}
	
}
