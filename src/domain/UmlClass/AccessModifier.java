package domain.UmlClass;

public enum AccessModifier {
	
	// our access modifiers
	Public('+'),
	Private('-'),
	Protected('#');
	
	private char _symbol;
	
	AccessModifier(char symbol) {
		setSymbol(symbol);
	}

	public char getSymbol() {
		return _symbol;
	}

	public void setSymbol(char _symbol) {
		this._symbol = _symbol;
	}
}