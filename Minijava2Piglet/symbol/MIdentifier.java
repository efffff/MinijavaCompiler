package symbol;

public class MIdentifier extends MType {
	private String name;
	public MIdentifier(String type, String _name) {
		super(type);
		name = _name;
	}

	public String getName() {
		return name;
	}
}
