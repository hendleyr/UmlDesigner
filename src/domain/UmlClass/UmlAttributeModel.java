package domain.UmlClass;

public class UmlAttributeModel {
	private AccessModifier _accessModifier;
	private String _name;
	private String _type;
	private boolean _isStatic;
	
	public UmlAttributeModel() {
		this(AccessModifier.Private, "newAttribute", "Object");
	}
	
	public UmlAttributeModel(AccessModifier accessModifier, String name, String type) {
		_accessModifier = accessModifier;
		_name = name;
		_type = type;
	}
	
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		this._name = name;
	}
	public String getType() {
		return _type;
	}
	public void setType(String type) {
		this._type = type;
	}	
	public AccessModifier getAccessModifier() {
		return _accessModifier;
	}
	public void setAccessModifier(AccessModifier accessModifier) {
		this._accessModifier = accessModifier;
	}

	public boolean getStaticFlag() {
		return _isStatic;
	}

	public void setStaticFlag(boolean _isStatic) {
		this._isStatic = _isStatic;
	}
}
