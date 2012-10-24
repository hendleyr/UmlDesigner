package ui.UmlClass;

import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.decoration.ArrowTip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;

import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.*;

import domain.UmlClass.AccessModifier;
import domain.UmlClass.AssociationType;
import domain.UmlClass.UmlAssociationModel;
import domain.UmlClass.UmlAttributeModel;
import domain.UmlClass.UmlMethodModel;

@SuppressWarnings("serial")
public class AssociationFigure extends LineConnectionFigure {
	private UmlAssociationModel associationModel;
	
    /** Creates a new instance. */
    public AssociationFigure() {
    	associationModel = new UmlAssociationModel(null, null);
        set(STROKE_COLOR, new Color(0x000099));
        set(AttributeKeys.FILL_COLOR, Color.WHITE);
        set(STROKE_WIDTH, 1d);
        set(END_DECORATION, new ArrowTip());

        setAttributeEnabled(FILL_COLOR, true);
        setAttributeEnabled(END_DECORATION, true);
        setAttributeEnabled(START_DECORATION, true);
        setAttributeEnabled(STROKE_DASHES, false);
        setAttributeEnabled(FONT_ITALIC, false);
        setAttributeEnabled(FONT_UNDERLINE, false);
    }

    /**
     * Checks if two figures can be connected. Implement this method
     * to constrain the allowed connections between figures.
     */
    @Override
    public boolean canConnect(Connector start, Connector end) {
    	if ((start.getOwner() instanceof UmlClassFigure) && (end.getOwner() instanceof UmlClassFigure)) {
    		UmlClassFigure startFigure = (UmlClassFigure) start.getOwner();
    		UmlClassFigure endFigure = (UmlClassFigure) end.getOwner();
    		
    		// TODO: check for inheritance cycle
    		if (associationModel.getType() == AssociationType.Inheritance) {
    			if (startFigure.getModel().hasInheritanceCycle(endFigure.getModel())) return false;
    		}
    		
    		return true;
    	}
        return false;
    }

    @Override
    public boolean canConnect(Connector start) {
        return (start.getOwner() instanceof UmlClassFigure);
    }

    /**
     * Handles the disconnection of a connection.
     * Override this method to handle this event.
     */
    @Override
    protected void handleDisconnect(Connector start, Connector end) {
    	UmlClassFigure sf = (UmlClassFigure) start.getOwner();
    	UmlClassFigure ef = (UmlClassFigure) end.getOwner();
    	    	
    	sf.getModel().removeAssociation(ef.getModel());        
    }

    /**
     * Handles the connection of a connection.
     * Override this method to handle this event.
     */
    @Override
    protected void handleConnect(Connector start, Connector end) {
    	UmlClassFigure sf = (UmlClassFigure) start.getOwner();
    	UmlClassFigure ef = (UmlClassFigure) end.getOwner();

    	associationModel.setTarget(ef.getModel());    	
        sf.getModel().addAssociation(associationModel.getTarget(), associationModel.getType());
    }

    @Override
    public AssociationFigure clone() {
    	AssociationFigure that = (AssociationFigure) super.clone();

        return that;
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public Collection<Action> getActions(Point2D.Double p) {
    	Collection<Action> actions = new ArrayList<Action>();
    	actions.add(new AbstractAction(AssociationType.Aggregation.toString()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				// change end decoration to white diamond
				set(END_DECORATION, new AggregationDecorationFigure());
		        changed();
			}
    	});
    	actions.add(new AbstractAction(AssociationType.Inheritance.toString()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				// TODO: add source attribute on associationModel to enable graceful validation
				// change end decoration to white arrow
				set(END_DECORATION, new ArrowTip(0.35, 20.0 , 20.0, false, true, true));
	        	changed();
			}
    	});
    	actions.add(new AbstractAction(AssociationType.Dependency.toString()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				// change end decoration to default small arrow
				set(END_DECORATION, new ArrowTip());
	        	changed();
			}
    	});
    	return actions;
    }
    
    @Override
    public void removeNotify(Drawing d) {
        if (getStartFigure() != null) {
//            ((UmlClassFigure) getStartFigure()).removeDependency(this);
        }
        if (getEndFigure() != null) {
//            ((UmlClassFigure) getEndFigure()).removeDependency(this);
        }
        super.removeNotify(d);
    }
}
