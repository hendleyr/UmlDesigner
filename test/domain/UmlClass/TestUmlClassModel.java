package domain.UmlClass;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import domain.UmlClass.AssociationType;

public class TestUmlClassModel{

	private UmlClassModel targetModel;
	private UmlClassModel testModel;
	private UmlClassModel anotherModel;
	private UmlClassModel unassociatedModel;
	private UmlAttributeModel testAttribute;
	private UmlAttributeModel anotherAttribute;
	private UmlAttributeModel nonexistentAttribute;
	private UmlMethodModel testMethod;
	private UmlMethodModel anotherMethod;
	private UmlMethodModel nonexistentMethod;
	
	@Before
	public void setUp() throws Exception {
		targetModel = new UmlClassModel(AccessModifier.Public, "targetModel");
		testModel = new UmlClassModel();
		anotherModel = new UmlClassModel(AccessModifier.Public, "anotherModel");
		unassociatedModel = new UmlClassModel();
		testAttribute = new UmlAttributeModel();
		anotherAttribute = new UmlAttributeModel(AccessModifier.Private, "anotherAttribute", "Object");
		nonexistentAttribute = new UmlAttributeModel(AccessModifier.Private, "nonexistentAttribute", "Object");
		testMethod = new UmlMethodModel();
		anotherMethod = new UmlMethodModel(AccessModifier.Private, "anotherMethod", new ArrayList<UmlAttributeModel>());
		nonexistentMethod = new UmlMethodModel(AccessModifier.Private, "nonexistentMethod", new ArrayList<UmlAttributeModel>());
	}

	@Test
	public void testAddAssociation() {
		// added some syntactic sugar to add/remove Associations
		
		//test adding one association
		UmlAssociationModel assocA = testModel.addAssociation(targetModel, AssociationType.Inheritance);
		assertTrue(assocA != null);
		assertTrue(testModel.getAssociations().get(0).equals(assocA));
		
		//test adding duplicate association
		assertTrue(testModel.addAssociation(targetModel, AssociationType.Inheritance) == null);
		assertTrue(testModel.getAssociations().size() == 1);
		
		//test adding association with same target but different associationType
		assertTrue(testModel.addAssociation(targetModel, AssociationType.Composition) == null);
		assertTrue(testModel.getAssociations().size() == 1);
			
		//test adding a new association to the first one
		UmlAssociationModel assocB = testModel.addAssociation(anotherModel, AssociationType.Inheritance);
		assertTrue(assocB != null);
		assertTrue(testModel.getAssociations().get(1).equals(assocB));		
	}
	
	@Test
	public void testRemoveAssociation() {
		//add two associations in preparation for removal
		UmlAssociationModel assocA = testModel.addAssociation(targetModel, AssociationType.Inheritance);
		UmlAssociationModel assocB = testModel.addAssociation(anotherModel, AssociationType.Inheritance);
		assertTrue(assocA != null);
		assertTrue(assocB != null);
		//remove an association
		assertTrue(testModel.removeAssociation(targetModel).equals(assocA));
		assertTrue(testModel.getAssociations().size() == 1 && testModel.getAssociations().get(0).equals(assocB));
		//try to remove an association that doesn't exist
		assertTrue(testModel.removeAssociation(unassociatedModel) == null);
	}
	
	@Test
	public void testHasInheritanceCycle() {
		
	}
	
	@Test
	public void testAddAttribute() {
		//try to add one attribute
		UmlAttributeModel attrA = new UmlAttributeModel();
		assertTrue(testModel.addAttribute(attrA));
		assertTrue(testModel.getAttributes().get(0).equals(attrA));
		//try to add a duplicate attribute (throws exception)
		assertFalse(testModel.addAttribute(new UmlAttributeModel()));
	}
	
	@Test
	public void testRemoveAttribute() {
		//add attributes to set up
		assertTrue(testModel.addAttribute(testAttribute));
		assertTrue(testModel.addAttribute(anotherAttribute));
		//remove an attribute
		assertTrue(testModel.removeAttribute(testAttribute));
		assertTrue(testModel.getAttributes().size() == 1 && testModel.getAttributes().get(0).equals(anotherAttribute));
		//remove an attribute that doesn't exist (throws exception)
		assertFalse(testModel.removeAttribute(nonexistentAttribute));
	}
	
	@Test
	public void testAddMethod() {
		//try to add one method
		UmlMethodModel mockMethod = new UmlMethodModel();
		
		assertTrue(testModel.addMethod(mockMethod));
		assertTrue(testModel.getMethods().get(0).equals(mockMethod));
		//try to add a duplicate method (throws exception)
		assertFalse(testModel.addMethod(mockMethod));
	}
	
	@Test
	public void testRemoveMethod() {
		//add methods to set up 
		assertTrue(testModel.addMethod(testMethod));
		assertTrue(testModel.addMethod(anotherMethod));
		//remove an attribute
		assertTrue(testModel.removeMethod(testMethod));
		assertTrue(testModel.getMethods().size() == 1 && testModel.getMethods().get(0).equals(anotherMethod));
		//remove an attribute that doesn't exist (throws exception)
		assertFalse(testModel.removeMethod(nonexistentMethod));
	}
}
