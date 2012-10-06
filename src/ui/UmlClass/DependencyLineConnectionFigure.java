package ui.UmlClass;

//TODO: rework to new framework
//import CH.ifa.draw.figures.LineConnection;
//import CH.ifa.draw.figures.ArrowTip;
//import CH.ifa.draw.framework.Figure;
import java.awt.Graphics;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.decoration.ArrowTip;

import domain.UmlClass.AssociationType;
import domain.UmlClass.UmlClassModel;

/**
 * Draw a dependency line between two classes. A dependency relation is a
 * uses-a relation with a direction where the connection points to the class
 * used by another one. The start class is dependend on the end class. A
 * DependencyLineConnection has an arrow at the end point and is dotted.
 * Currently, classes keep track themselves about other dependend classes
 */
public class DependencyLineConnectionFigure extends LineConnectionFigure {

    static final long serialVersionUID = -2964321053621789632L;

    /**
     * Create a new instance
     */
    public DependencyLineConnectionFigure() {
        super();
        set(AttributeKeys.START_DECORATION, null);
        ArrowTip arrow = new ArrowTip(0.4, 12.0, 0.0);
        
        set(AttributeKeys.STROKE_COLOR, java.awt.Color.black);
        set(AttributeKeys.END_DECORATION, arrow);
    }

    /**
     * Hook method to plug in application behaviour into
     * a template method. This method is called when a
     * connection between two objects has been established.
     *
     * @param start figure representing the start class which is dependend on the end class
     * @param end   figure representing the end class
     */
    protected void handleConnect(Connector start, Connector end) {
        super.handleConnect(start, end);

        UmlClassModel startClass = ((UmlClassFigure)start).getModellerClass();
        UmlClassModel endClass = ((UmlClassFigure)end).getModellerClass();

        startClass.addAssociation(endClass, AssociationType.Dependency);
    }

    /**
     * Hook method to plug in application behaviour into
     * a template method. This method is called when a 
     * connection between two objects has been cancelled.
     *
     * @param start figure representing the start class which is dependend on the end class
     * @param end   figure representing the end class
     */
    protected void handleDisconnect(Connector start, Connector end) {
        super.handleDisconnect(start, end);
        if ((start != null) && (end!= null)) {
            UmlClassModel startClass = ((UmlClassFigure)start).getModellerClass();
            UmlClassModel endClass = ((UmlClassFigure)end).getModellerClass();
            startClass.removeAssociation(endClass);
        }
    }

    /*
     * Draw the line which is a dotted line for a dependency connection. Instead
     * of drawing one line from start point to end point, the line is divided into
     * several small lines each 5 pixels long and 5 pixels away from the previous
     * line. Some minor inaccuracy are possible due to rounding errors or incomplete
     * last lines.
     *
     * @param g  graphics context into which the line is drawn
     * @param x1 start x point
     * @param y1 start y point
     * @param x2 end x point
     * @param y2 end y point
     */
    //TODO: we want to supply the framework with a liner that mimics this method
    protected void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
        int xDistance = x2 - x1;
        int yDistance = y2 - y1;
        double direction = Math.PI/2 - Math.atan2(xDistance, yDistance);
        
        double xAngle = Math.cos(direction);
        double yAngle = Math.sin(direction);
        int lineLength = (int)Math.sqrt(xDistance*xDistance + yDistance*yDistance);

        for (int i = 0; i + 5 < lineLength; i = i + 10) {
            int p1x = x1 + (int)(i * xAngle);
            int p1y = y1 + (int)(i * yAngle);
            int p2x = x1 + (int)((i + 5) * xAngle);
            int p2y = y1 + (int)((i + 5) * yAngle);
            g.drawLine(p1x, p1y, p2x, p2y);
        }
    }
}