package ui.UmlClass;

import static org.junit.Assert.assertTrue;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.jhotdraw.draw.AttributeKeys;
import org.junit.Before;
import org.junit.Test;

import domain.UmlClass.AccessModifier;
import domain.UmlClass.UmlAttributeModel;

public class TestAttributeFigure {
	private UmlClassFigure umlClassFigure;
	private AttributeFigure attrFigure;
	private UmlAttributeModel attrModel;
	
	@Before
	public void setUp() {
		umlClassFigure = new UmlClassFigure();
		attrModel = new UmlAttributeModel();
		attrFigure = new AttributeFigure("- newAttribute : Object", umlClassFigure.getModel(), attrModel);		
	}
	
	@Test
	public void testAttribute_Protected_Valid_Int() {
		attrFigure.setText("# employeeId : int");		
		// assert that our regex filtered user input as expected and mapped results appropriately
		assertTrue(attrModel.getAccessModifier().equals(AccessModifier.Protected));
		assertTrue(attrModel.getName().equals("employeeId"));
		assertTrue(attrModel.getType().equals("int"));
		assertTrue(!attrModel.getStaticFlag());
		assertTrue(attrFigure.getText().equals("# employeeId : int"));
	}
	
	@Test
	public void testAttribute_DefaultMapping_REGEX_DefaultMapping() {
		attrFigure.setText("())(((*)MOCK   +___&AS");		
		// assert that our regex filtered user input as expected and mapped results appropriately
		assertTrue(attrModel.getAccessModifier().equals(AccessModifier.Private));
		assertTrue(attrModel.getName().equals("MOCK"));
		assertTrue(attrModel.getType().equals("Object"));
		assertTrue(!attrModel.getStaticFlag());
		assertTrue(attrFigure.getText().equals("- MOCK : Object"));
	}
	
	@Test
	public void testStaticFlag() {
		Object[] actions = attrFigure.getActions(null).toArray();
		AbstractAction a = (AbstractAction) actions[0];
		
		a.actionPerformed(new ActionEvent(attrFigure, 0, null));
		assertTrue(attrModel.getStaticFlag());
		assertTrue(attrFigure.get(AttributeKeys.FONT_UNDERLINE));
	}
}