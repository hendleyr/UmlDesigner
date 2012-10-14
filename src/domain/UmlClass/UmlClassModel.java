package domain.UmlClass;

import java.util.ArrayList;
import java.util.List;

public class UmlClassModel {
	private AccessModifier _accessModifier;
	private String _name;
	private List<UmlAssociationModel> _associations;
	private List<UmlAttributeModel> _attributes;
	private List<UmlMethodModel> _methods;
	
	public UmlClassModel() {
		this(AccessModifier.Public, "newClass");
	}
	
	public UmlClassModel(AccessModifier accessMod, String name) {
		_accessModifier = accessMod;
		_name = name;
		_associations = new ArrayList<UmlAssociationModel>();
		_attributes = new ArrayList<UmlAttributeModel>();
		_methods = new ArrayList<UmlMethodModel>();
	}
	
	public void addAssociation(UmlClassModel target, AssociationType associationType) {
		// if associations set includes a UmlAssociationModel with SAME target and a 
		// DIFFERENT associationType, throw exception
		//TODO:
		// do nothing if association already exists
		//TODO:
		// above could be done in like 20seconds if java supported lambda expressions. fml

		// add new UmlAssociationModel
		_associations.add(new UmlAssociationModel(target, associationType));
	}
	
	public void removeAssociation(UmlClassModel target) {
		// if multiple tuples in association set have this target, throw exception
		//TODO:
		// if association does not exist in the set, throw exception
		//TODO:
		
		// remove the UmlAssociationModel
		for (UmlAssociationModel association : _associations){
		    if (association.getTarget().getName().equals(target.getName())) {
		    	_associations.remove(association);
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
	
	public void addAttribute(UmlAttributeModel attr) {
		// check for duplicate, throw exception if found
	}
	
	public void removeAttribute(UmlAttributeModel attr) {
		// throw exception if cannot find attr to remove
	}
	
	public void addMethod(UmlMethodModel method) {
		// check for duplicate, throw exception if found
	}
	
	public void removeMethod(UmlMethodModel method) {
		// throw exception if cannot find method to remove
	}
	
	//TODO: toString, returning the text to go into a filestream to construct a code skeleton
	
	public AccessModifier getAccessModifier() {
		return _accessModifier;
	}
	public void setAccessModifier(AccessModifier accessModifier) {
		this._accessModifier = accessModifier;
	}
	public List<UmlAssociationModel> getAssociations() {
		return _associations;
	}
	public void setAssociations(List<UmlAssociationModel> associations) {
		this._associations = associations;
	}
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		this._name = name;
	}
	public List<UmlAttributeModel> getAttributes() {
		return _attributes;
	}
	public void setAttributes(List<UmlAttributeModel> attributes) {
		this._attributes = attributes;
	}
	public List<UmlMethodModel> getMethods() {
		return _methods;
	}
	public void setMethods(List<UmlMethodModel> methods) {
		this._methods = methods;
	}
}
