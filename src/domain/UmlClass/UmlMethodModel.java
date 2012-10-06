package domain.UmlClass;

import java.util.List;

public class UmlMethodModel {
	private AccessModifier _accessModifier;
	private String _name;
	private List<UmlVariableModel> _parameters;
	
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
	public List<UmlVariableModel> getParameters() {
		return _parameters;
	}
	public void setParameters(List<UmlVariableModel> parameters) {
		this._parameters = parameters;
	}
}
