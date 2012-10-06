package domain.UmlClass;

import java.util.List;
import java.util.Set;

public class UmlClassModel {
	private AccessModifier accessModifier;
	private Set<UmlAssociationModel> associations;
	private String name;
	
	private List<UmlAttributeModel> attributes;
	private List<UmlMethodModel> methods;
	
	public void addAssociation(UmlClassModel target, AssociationType associationType) {
		// if associations set includes a UmlAssociationModel with SAME target and a 
		// DIFFERENT associationType, throw exception
		//TODO:
		// do nothing if association already exists
		//TODO:
		// above could be done in like 20seconds if java supported lambda expressions. fml

		// add new UmlAssociationModel
		associations.add(new UmlAssociationModel(target, associationType));
	}
	
	public void removeAssociation(UmlClassModel target) {
		// if multiple tuples in association set have this target, throw exception
		//TODO:
		// if association does not exist in the set, throw exception
		//TODO:
		
		// remove the UmlAssociationModel
		for (UmlAssociationModel association : associations){
		    if (association.getTarget().getName().equals(target.getName())) {
		    	associations.remove(association);
		    	return;
		    }
		}
	}
	
	public boolean hasInheritanceCycle(UmlClassModel target) {
		/* TODO: "An inheritance relationship can be established if
	     * there is no cyclic inheritance graph. This method is called before
	     * the two classes are connected in the diagram." */
		
		// in english, "a super class cannot inherit from one of its subclasses"
		return false;
	}
	
	public AccessModifier getAccessModifier() {
		return accessModifier;
	}
	public void setAccessModifier(AccessModifier accessModifier) {
		this.accessModifier = accessModifier;
	}
	public Set<UmlAssociationModel> getRelationships() {
		return associations;
	}
	public void setRelationships(Set<UmlAssociationModel> associations) {
		this.associations = associations;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<UmlAttributeModel> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<UmlAttributeModel> attributes) {
		this.attributes = attributes;
	}
	public List<UmlMethodModel> getMethods() {
		return methods;
	}
	public void setMethods(List<UmlMethodModel> methods) {
		this.methods = methods;
	}
	
	//TODO: toString, returning the text to go into a filestream to construct a code skeleton
}
