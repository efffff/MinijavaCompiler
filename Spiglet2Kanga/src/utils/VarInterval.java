package utils;
public class VarInterval {
	String name;
	int begin;
	int end;
	public VarInterval(String _name) {
		name = _name;
		begin = 0;
		end = 0;
	}
	public VarInterval(String _name, int _begin, int _end) {
		name = _name;
		begin = _begin;
		end = _end;
	}
	public void modifyEnd(int _end) {
		end = (end > _end? end: _end);
	}
	public void modifyBegin(int _begin) {
		begin = (begin > _begin? _begin: begin);
	}
	public void modifyInterval(int _begin, int _end) {
		begin = (begin > _begin? _begin: begin);
		end = (end > _end? end: _end);
	}
}
