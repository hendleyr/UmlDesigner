package domain.UmlClass;

public class UmlAssociationModel {
	private AssociationType type;
	private UmlClassModel target;
	
	public UmlAssociationModel(UmlClassModel target, AssociationType type) {
		this.target = target;
		this.type = type;
	}

	public AssociationType getType() {
		return type;
	}

	public void setType(AssociationType type) {
		this.type = type;
	}

	public UmlClassModel getTarget() {
		return target;
	}

	public void setTarget(UmlClassModel target) {
		this.target = target;
	}
}
