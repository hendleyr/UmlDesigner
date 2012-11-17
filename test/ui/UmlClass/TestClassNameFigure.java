package ui.UmlClass;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jhotdraw.draw.AttributeKeys;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import domain.UmlClass.AccessModifier;
import domain.UmlClass.UmlClassModel;

public class TestClassNameFigure {
	private UmlClassFigure umlClassFigure;
	private ClassNameFigure classNameFigure;
	private UmlClassModel classModel;
	
	@Before
	public void setUp() {
		umlClassFigure = new UmlClassFigure();
		classModel = umlClassFigure.getModel();
		classNameFigure = umlClassFigure.getNameFigure();
	}
	
	@Test
	public void testClassName_Protected_ValidName() {
		classNameFigure.setText("+ FastInverseSquareRootHandler");
		
		// assert that our regex filtered user input as expected and the adapter mapped results appropriately
		assertTrue(classModel.getAccessModifier().equals(AccessModifier.Public));
		assertTrue(classModel.getName().equals("FastInverseSquareRootHandler"));
		assertTrue(classNameFigure.getText().equals("+ FastInverseSquareRootHandler"));
	}
	
	@Test
	public void testClassName_DefaultAccessModifier_REGEX() {
		classNameFigure.setText("MOCK CLASS NAME");
		
		// assert that our regex filtered user input as expected and the adapter mapped results appropriately
		assertTrue(classModel.getAccessModifier().equals(AccessModifier.Public));
		assertTrue(classModel.getName().equals("MOCK"));
		assertTrue(classNameFigure.getText().equals("+ MOCK"));
	}
	
	@Test
	public void testClassName_Protected_REGEX() {
		classNameFigure.setText("# ++++++MOCK CLASS NAME");
		
		// assert that our regex filtered user input as expected and the adapter mapped results appropriately
		assertTrue(classModel.getAccessModifier().equals(AccessModifier.Protected));
		assertTrue(classModel.getName().equals("MOCK"));
		assertTrue(classNameFigure.getText().equals("# MOCK"));
	}
	
	// action 0: set interface
	public void testSetInterfaceAction() {
		Object[] actions = umlClassFigure.getActions(null).toArray();
		AbstractAction setInterface = (AbstractAction) actions[0];
		
		setInterface.actionPerformed(new ActionEvent(umlClassFigure, 0, null));
		assertTrue(classModel.getInterfaceFlag());
		assertTrue(classNameFigure.getText().startsWith("<<interface>>"));
	}
	
	// action 1: set abstract
	@Test
	public void testSetAbstractAction() {
		Object[] actions = umlClassFigure.getActions(null).toArray();
		AbstractAction setAbstract = (AbstractAction) actions[1];
		
		setAbstract.actionPerformed(new ActionEvent(umlClassFigure, 0, null));
		assertTrue(classModel.getAbstractFlag());
		assertTrue(classNameFigure.get(AttributeKeys.FONT_ITALIC));
	}
}