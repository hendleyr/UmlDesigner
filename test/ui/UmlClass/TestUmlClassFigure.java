package ui.UmlClass;

import static org.junit.Assert.*;

import org.jhotdraw.draw.TextFigure;
import org.junit.Before;
import org.junit.Test;

import domain.UmlClass.AccessModifier;

// NB: these unit tests may be too brittle. If we change our strategy for validating UI input (very likely), these tests WILL break, and then
// some developer spends his billable hours rewriting them--and this is why we shouldn't go crazy with designing by unit tests... -richard

public class TestUmlClassFigure {
	UmlClassFigure umlClassFigure;
	
	// here's a brief run-down on the tests: we setText() on the figure, mimicking user input, then call fireFigureChanged().
	// Each tested figure has a listener that will execute code to validate the input. The listener then maps validated input to the classmodel
	// associated with the figure. These tests determine that the listener caught the event and produced the result we expect.
	
	// TODO: test the MethodAdapter
	
	@Before
	public void setUp() {
		umlClassFigure = new UmlClassFigure();
	}

	@Test
	public void testClassNameAdapter_Protected_ValidName() {
		//TODO: naive method of extracting access modifier, only examines charAt(0). is there a better way...?
		umlClassFigure.getNameFigure().setText("+ FastInverseSquareRootHandler");
		umlClassFigure.getNameFigure().fireFigureChanged();
		
		// assert that our regex filtered user input as expected and the adapter mapped results appropriately
		assertTrue(umlClassFigure.getModel().getAccessModifier().equals(AccessModifier.Public));
		assertTrue(umlClassFigure.getModel().getName().equals("FastInverseSquareRootHandler"));
		assertTrue(umlClassFigure.getNameFigure().getText().equals("+ FastInverseSquareRootHandler"));
	}
	
	@Test
	public void testClassNameAdapter_DefaultAccessModifier_REGEX() {
		umlClassFigure.getNameFigure().setText("MOCK CLASS NAME");
		umlClassFigure.getNameFigure().fireFigureChanged();
		
		// assert that our regex filtered user input as expected and the adapter mapped results appropriately
		assertTrue(umlClassFigure.getModel().getAccessModifier().equals(AccessModifier.Public));
		assertTrue(umlClassFigure.getModel().getName().equals("MOCK"));
		// we get a double space in the output when using an access modifier local, because it's symbol is ' ' instead of ''.
		// this feels dumb and I'm sure there's a simple fix
		assertTrue(umlClassFigure.getNameFigure().getText().equals("+ MOCK"));
	}
	
	@Test
	public void testClassNameAdapter_Protected_REGEX() {
		//TODO: naive method of extracting access modifier, only examines charAt(0). is there a better way...?
		umlClassFigure.getNameFigure().setText("# ++++++MOCK CLASS NAME");
		umlClassFigure.getNameFigure().fireFigureChanged();
		
		// assert that our regex filtered user input as expected and the adapter mapped results appropriately
		assertTrue(umlClassFigure.getModel().getAccessModifier().equals(AccessModifier.Protected));
		assertTrue(umlClassFigure.getModel().getName().equals("MOCK"));
		assertTrue(umlClassFigure.getNameFigure().getText().equals("# MOCK"));
	}
	
	@Test
	public void testAttributeAdapater_Protected_Valid_Int() {
		TextFigure attrFigure = (TextFigure)umlClassFigure.getAttributesCompartment().getChild(0);
		
		attrFigure.setText("# employeeId : int");
		attrFigure.fireFigureChanged();
		
		// assert that our regex filtered user input as expected and the adapter mapped results appropriately
		assertTrue(umlClassFigure.getModel().getAttributes().get(0).getAccessModifier().equals(AccessModifier.Protected));
		assertTrue(umlClassFigure.getModel().getAttributes().get(0).getName().equals("employeeId"));
		assertTrue(umlClassFigure.getModel().getAttributes().get(0).getType().equals("int"));
		assertTrue(attrFigure.getText().equals("# employeeId : int"));
	}
	
	@Test
	public void testAttributeAdapater_Default_REGEX_Default() {
		TextFigure attrFigure = (TextFigure)umlClassFigure.getAttributesCompartment().getChild(0);
		
		attrFigure.setText("())(((*)MOCK   +___&AS");
		attrFigure.fireFigureChanged();
		
		// assert that our regex filtered user input as expected and the adapter mapped results appropriately
		assertTrue(umlClassFigure.getModel().getAttributes().get(0).getAccessModifier().equals(AccessModifier.Private));
		assertTrue(umlClassFigure.getModel().getAttributes().get(0).getName().equals("MOCK"));
		assertTrue(umlClassFigure.getModel().getAttributes().get(0).getType().equals("Object"));
		// we get a double space in the output when using an access modifier local, because it's symbol is ' ' instead of ''.
		// this feels dumb and I'm sure there's a simple fix
		assertTrue(attrFigure.getText().equals("- MOCK : Object"));
	}
}