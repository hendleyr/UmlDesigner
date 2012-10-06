package domain.UmlClass;

public class UmlAttributeModel {
	private AccessModifier _accessModifier;
	private UmlVariableModel _variable;
	
	public AccessModifier getAccessModifier() {
		return _accessModifier;
	}
	public void setAccessModifier(AccessModifier accessModifier) {
		this._accessModifier = accessModifier;
	}

	public UmlVariableModel getVariable() {
		return _variable;
	}
	public void setVariable(UmlVariableModel variable) {
		this._variable = variable;
	}
}
