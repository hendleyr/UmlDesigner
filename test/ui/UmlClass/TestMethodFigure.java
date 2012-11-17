package ui.UmlClass;

import static org.junit.Assert.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jhotdraw.draw.AttributeKeys;
import org.junit.Before;
import org.junit.Test;
import domain.UmlClass.AccessModifier;
import domain.UmlClass.UmlMethodModel;

public class TestMethodFigure {
	private UmlClassFigure umlClassFigure;
	private MethodFigure methodFigure;
	private UmlMethodModel methodModel;
	
	@Before
	public void setUp() {
		umlClassFigure = new UmlClassFigure();
		methodModel = new UmlMethodModel();
		methodFigure = new MethodFigure("- newMethod() : Object", umlClassFigure.getModel(), methodModel, umlClassFigure.getNameFigure());
	}
	
	@Test
	public void test_DefaultMapping_Valid_Empty_Valid() {
		methodFigure.setText("newMethod() : Object");
		assertTrue(methodFigure.getText().equals("- newMethod() : Object"));
		assertTrue(methodModel.getAccessModifier() == AccessModifier.Private);
		assertTrue(methodModel.getName().equals("newMethod"));
		assertTrue(methodModel.getParameters().size() == 0);
		assertTrue(methodModel.getReturnType().equals("Object"));
	}
	
	@Test
	public void test_DefaultMapping_Regex_Empty_Valid() {
		methodFigure.setText("new*&^%Method() : Object");
		assertTrue(methodFigure.getText().equals("- new() : Object"));
		assertTrue(methodModel.getAccessModifier() == AccessModifier.Private);
		assertTrue(methodModel.getName().equals("new"));
		assertTrue(methodModel.getParameters().size() == 0);
		assertTrue(methodModel.getReturnType().equals("Object"));
	}
	
	@Test
	public void test_DefaultMapping_Regex_Valid_Valid() {
		methodFigure.setText("new*&^%Method(x : int) : Object");
		assertTrue(methodFigure.getText().equals("- new(x : int) : Object"));
		assertTrue(methodModel.getAccessModifier() == AccessModifier.Private);
		assertTrue(methodModel.getName().equals("new"));
		assertTrue(methodModel.getParameters().size() == 1 && methodModel.getParameters().get(0).getName().equals("x") 
				&& methodModel.getParameters().get(0).getType().equals("int"));
		assertTrue(methodModel.getReturnType().equals("Object"));
	}
	
	@Test
	public void test_DefaultMapping_Regex_Regex_Valid() {
		methodFigure.setText("new*&^%Method(x(*&^:int) : Object");
		assertTrue(methodFigure.getText().equals("- new(x : int) : Object"));
		assertTrue(methodModel.getAccessModifier() == AccessModifier.Private);
		assertTrue(methodModel.getName().equals("new"));
		assertTrue(methodModel.getParameters().size() == 1 && methodModel.getParameters().get(0).getName().equals("x") 
				&& methodModel.getParameters().get(0).getType().equals("int"));
		assertTrue(methodModel.getReturnType().equals("Object"));
	}
	
	@Test
	public void test_DefaultMapping_Regex_Regex_Regex() {
		methodFigure.setText("new*&^%Method(x(*&^:int) : Obje*^%$ct");
		assertTrue(methodFigure.getText().equals("- new(x : int) : Obje"));
		assertTrue(methodModel.getAccessModifier() == AccessModifier.Private);
		assertTrue(methodModel.getName().equals("new"));
		assertTrue(methodModel.getParameters().size() == 1 && methodModel.getParameters().get(0).getName().equals("x") 
				&& methodModel.getParameters().get(0).getType().equals("int"));
		assertTrue(methodModel.getReturnType().equals("Obje"));
	}
	
	@Test
	public void test_DefaultMapping_Regex_Multiple_Regex() {
		methodFigure.setText("new*&^%Method(x : int, y : double) : Obje*^%$ct");
		assertTrue(methodFigure.getText().equals("- new(x : int, y : double) : Obje"));
		assertTrue(methodModel.getAccessModifier() == AccessModifier.Private);
		assertTrue(methodModel.getName().equals("new"));
		assertTrue(methodModel.getParameters().size() == 2 && methodModel.getParameters().get(0).getName().equals("x") 
				&& methodModel.getParameters().get(0).getType().equals("int"));
		assertTrue(methodModel.getParameters().size() == 2 && methodModel.getParameters().get(1).getName().equals("y") 
				&& methodModel.getParameters().get(1).getType().equals("double"));
		assertTrue(methodModel.getReturnType().equals("Obje"));
	}
	
	@Test
	public void test_DefaultMapping_Regex_MultipleRegex_Regex() {
		methodFigure.setText("new*&^%Method(x%$@# : int, y%$@# : double) : Obje*^%$ct");
		assertTrue(methodFigure.getText().equals("- new(x : int, y : double) : Obje"));
		assertTrue(methodModel.getAccessModifier() == AccessModifier.Private);
		assertTrue(methodModel.getName().equals("new"));
		assertTrue(methodModel.getParameters().size() == 2 && methodModel.getParameters().get(0).getName().equals("x") 
				&& methodModel.getParameters().get(0).getType().equals("int"));
		assertTrue(methodModel.getParameters().size() == 2 && methodModel.getParameters().get(1).getName().equals("y") 
				&& methodModel.getParameters().get(1).getType().equals("double"));
		assertTrue(methodModel.getReturnType().equals("Obje"));
	}
	
	// abstract: 0
	@Test
	public void testAbstractFlag() {
		Object[] actions = methodFigure.getActions(null).toArray();
		AbstractAction a = (AbstractAction) actions[0];
		
		a.actionPerformed(new ActionEvent(methodFigure, 0, null));
		assertTrue(methodModel.getAbstractFlag());
		assertTrue(!methodModel.getStaticFlag());
		assertTrue(methodFigure.get(AttributeKeys.FONT_ITALIC));
		assertTrue(umlClassFigure.getModel().getAbstractFlag());
	}
	// static: 1
	@Test
	public void testStaticFlag() {
		Object[] actions = methodFigure.getActions(null).toArray();
		AbstractAction a = (AbstractAction) actions[1];
		
		a.actionPerformed(new ActionEvent(methodFigure, 0, null));
		assertTrue(methodModel.getStaticFlag());
		assertTrue(methodFigure.get(AttributeKeys.FONT_UNDERLINE));
	}
	
	// test that swapping from static to abstract functions intelligently
	@Test
	public void testAbstractLogic() {
		Object[] actions = methodFigure.getActions(null).toArray();
		AbstractAction abstractAction = (AbstractAction) actions[0];
		AbstractAction staticAction = (AbstractAction) actions[1];
		
		staticAction.actionPerformed(new ActionEvent(methodFigure, 0, null));
		abstractAction.actionPerformed(new ActionEvent(methodFigure, 0, null));
		assertTrue(methodModel.getAbstractFlag());
		assertTrue(methodFigure.get(AttributeKeys.FONT_ITALIC));
		assertTrue(!methodModel.getStaticFlag());
		assertTrue(!methodFigure.get(AttributeKeys.FONT_UNDERLINE));
	}
	
	// test that swapping from abstract to static functions intelligently
	@Test
	public void testStaticLogic() {
		Object[] actions = methodFigure.getActions(null).toArray();
		AbstractAction abstractAction = (AbstractAction) actions[0];
		AbstractAction staticAction = (AbstractAction) actions[1];
		
		abstractAction.actionPerformed(new ActionEvent(methodFigure, 0, null));
		staticAction.actionPerformed(new ActionEvent(methodFigure, 0, null));
		assertTrue(!methodModel.getAbstractFlag());
		assertTrue(!methodFigure.get(AttributeKeys.FONT_ITALIC));
		assertTrue(methodModel.getStaticFlag());
		assertTrue(methodFigure.get(AttributeKeys.FONT_UNDERLINE));
	}
}