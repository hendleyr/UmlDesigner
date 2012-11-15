package domain.UmlClass;

public class UmlAssociationModel {
	private AssociationType _type;
	private UmlClassModel _target;
	private String _roleName;
	
	public UmlAssociationModel(UmlClassModel target, AssociationType type) {
		_target = target;
		_type = type;
		_roleName = "";
	}
	
	public UmlAssociationModel(UmlClassModel target, AssociationType type, String roleName) {
		_target = target;
		_type = type;
		_roleName = roleName;
	}

	public AssociationType getType() {
		return _type;
	}
	public void setType(AssociationType type) {
		this._type = type;
	}
	public UmlClassModel getTarget() {
		return _target;
	}
	public void setTarget(UmlClassModel target) {
		this._target = target;
	}

	public String getRoleName() {
		return _roleName;
	}

	public void setRoleName(String roleName) {
		this._roleName = roleName;
	}
}
