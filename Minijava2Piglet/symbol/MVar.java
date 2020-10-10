package symbol;

public class MVar extends MIdentifier{
	public String type;
	public String runtimeType = "";
	public String varName = "";
	public int classOffset;//class的第n个field，存在TEMP0偏移n*4处
	public int methodTempNo;//在method中的TEMP号码，局部变量存在TEMP 20及以上，参数存在TEMP 1-19
	public MVar(String _type, String _name, int _offset, int _tempNo) {
		super(_type, _name);
		type = _type;
		classOffset = _offset;
		methodTempNo = _tempNo;
	}
	public void setRuntimeType(String rt) {
		runtimeType = rt;
	}

	public int getTempNo() {
		return methodTempNo;
	}
}
