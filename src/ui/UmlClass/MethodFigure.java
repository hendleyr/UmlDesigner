package ui.UmlClass;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;

import javax.swing.Action;

import org.jhotdraw.draw.TextFigure;

@SuppressWarnings("serial")
public class MethodFigure extends TextFigure{
    public MethodFigure(String text) {
        super(text);
    }
    
    @Override
    public void setText(String text) {
        String newText = "";
        // parse text with our regex logics into newText, map to model, then call super's setText
        super.setText(newText);
    }
    
    @Override
    public Collection<Action> getActions(Point2D.Double p) {
        // return actions for making method static
        // checkbox items maybe?
        return Collections.emptyList();
    }
}