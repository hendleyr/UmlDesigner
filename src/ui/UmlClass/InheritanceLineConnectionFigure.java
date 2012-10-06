package ui.UmlClass;

import java.awt.Color;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.decoration.ArrowTip;

import domain.UmlClass.AssociationType;
import domain.UmlClass.UmlClassModel;

/**
 * An InheritanceLineConnection is a graphical representation for
 * an inheritance relationship (is-a) between two classes (represented
 * by ClassFigures).
 */
public class InheritanceLineConnectionFigure extends LineConnectionFigure {

    static final long serialVersionUID = 3140686678671889499L;

    /**
     * Create a new instance with a predefined arrow
     */
    public InheritanceLineConnectionFigure() {
        //setStartDecoration(null);
    	set(AttributeKeys.START_DECORATION, null);
        ArrowTip arrow = new ArrowTip(0.35, 20.0 , 20.0);
        set(AttributeKeys.FILL_COLOR, Color.white);
        set(AttributeKeys.STROKE_COLOR, Color.black);
//        arrow.setFillColor(Color.white);
//        arrow.setBorderColor(Color.black);
        //setEndDecoration(arrow);
        set(AttributeKeys.END_DECORATION, arrow);
    }
        
    /**
     * Hook method to plug in application behaviour into
     * a template method. This method is called when a
     * connection between two objects has been established.
     */
    protected void handleConnect(Connector start, Connector end) {
        super.handleConnect(start, end);

        UmlClassModel startClass = ((UmlClassFigure)start).getModellerClass();
        UmlClassModel endClass = ((UmlClassFigure)end).getModellerClass();

        //startClass.addSuperclass(endClass);
        startClass.addAssociation(endClass, AssociationType.Inheritance);
    }

    /**
     * Hook method to plug in application behaviour into
     * a template method. This method is called when a 
     * connection between two objects has been cancelled.
     */
    protected void handleDisconnect(Connector start, Connector end) {
        super.handleDisconnect(start, end);
        if ((start != null) && (end!= null)) {
        	UmlClassModel startClass = ((UmlClassFigure)start).getModellerClass();
        	UmlClassModel endClass = ((UmlClassFigure)end).getModellerClass();
            //startClass.removeSuperclass(endClass);
            startClass.removeAssociation(endClass);
        }
    }

    /**
     * Test whether an inheritance relationship between two ClassFigures can
     * be established. An inheritance relationship can be established if
     * there is no cyclic inheritance graph. This method is called before
     * the two classes are connected in the diagram.
     *
     * @param   start   subclass
     * @param   end     superclass
     * @return  true, if an inheritance relationship can be established, false otherwise
     */
    public boolean canConnect(Figure start, Figure end) {
        UmlClassModel startClass = ((UmlClassFigure)start).getModellerClass();
        UmlClassModel endClass = ((UmlClassFigure)end).getModellerClass();

        return !endClass.hasInheritanceCycle(startClass);
    }
}