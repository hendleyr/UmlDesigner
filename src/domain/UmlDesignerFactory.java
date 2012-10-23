package domain;

import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.ListFigure;
import org.jhotdraw.draw.TextAreaFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.xml.DefaultDOMFactory;
import ui.UmlClass.SeparatorFigure;
import ui.UmlClass.UmlClassFigure;
import domain.UmlClass.AccessModifier;
import domain.UmlClass.AssociationType;

public class UmlDesignerFactory extends DefaultDOMFactory {
	private final static Object[][] classTagArray = {
        { DefaultDrawing.class, "UmlDiagram" },
        { UmlClassFigure.class, "class" },
        { GraphicalCompositeFigure.class, "compartment" },
        { ListFigure.class, "list" },
        { TextFigure.class, "text" },
        { GroupFigure.class, "g" },
        { TextAreaFigure.class, "ta" },
        { SeparatorFigure.class, "separator" }
	};
	
	private final static Object[][] enumTagArray = {
		{ AccessModifier.class, "accessMod" },
		{ AssociationType.class, "assocType" }
	};
	
    /** Creates a new instance. */
    public UmlDesignerFactory() {
        for (Object[] o : enumTagArray) {
        	addEnumClass((String) o[1], (Class) o[0]);
        }
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
    }
}