package domain.UmlClass;

import java.util.List;

public class UmlMethodModel {
	private AccessModifier _accessModifier;
	private String _name;
	private List<UmlAttributeModel> _parameters;
	// NB: all parameters should have access modifier 'local'
	
	public UmlMethodModel(AccessModifier accessMod, String name, List<UmlAttributeModel> params) {
		this._accessModifier = accessMod;
		this._name = name;
		this._parameters = params;
	}
	
	public AccessModifier getAccessModifier() {
		return _accessModifier;
	}
	public void setAccessModifier(AccessModifier accessModifier) {
		this._accessModifier = accessModifier;
	}
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		this._name = name;
	}
	public List<UmlAttributeModel> getParameters() {
		return _parameters;
	}
	public void setParameters(List<UmlAttributeModel> parameters) {
		this._parameters = parameters;
	}
}