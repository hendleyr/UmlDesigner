package ui.UmlClass;

import static org.junit.Assert.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.junit.Before;
import org.junit.Test;


public class TestUmlClassFigure {
	UmlClassFigure umlClassFigure;
	
	@Before
	public void setUp() {
		umlClassFigure = new UmlClassFigure();
	}

	// test that code file is generated accurately
	@Test
	public void testCodeGen() throws IOException {
		Object[] actions = umlClassFigure.getActions(null).toArray();
		AbstractAction a = (AbstractAction) actions[4];
		a.actionPerformed(new ActionEvent(umlClassFigure, 0, null));
		
		FileReader f = new FileReader("newClass.java");
		BufferedReader b = new BufferedReader(f); 
		String code = "";
		int i;
		
		while ((i = b.read()) != -1) {
			code += (char)i;
		}
		b.close();
		f.close();
		
		assertTrue(code.equals(umlClassFigure.getModel().toString()));		
	}
}
