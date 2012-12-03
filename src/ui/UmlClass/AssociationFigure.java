package ui.UmlClass;

import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.layouter.LocatorLayouter;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.locator.BezierLabelLocator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;

import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.*;

import domain.UmlClass.AssociationType;
import domain.UmlClass.UmlAssociationModel;

@SuppressWarnings("serial")
public class AssociationFigure extends LabeledLineConnectionFigure {
	private static final double[] DASH_PATTERN = new double[] {10};
	
	// layout thoughts: multiplicities above the line near the arrow, roles below the line near the arrow,
	//					relationship above the line centered horizontally
	
	private TextFigure startMultiplicity;
	private TextFigure endMultiplicity;
	
	private TextFigure relationship;
	
	private TextFigure startRole;
	private TextFigure endRole;
	
	private UmlAssociationModel associationModel;
	private UmlClassFigure startFigure;
	private UmlClassFigure endFigure;
	boolean isBidirectional;
	
    /** Creates a new instance. */
    public AssociationFigure() {
    	associationModel = new UmlAssociationModel(null, AssociationType.Dependency);
    	isBidirectional = false;
    	setLiner(new ElbowLiner());
    	
        set(STROKE_COLOR, new Color(0x000099));
        set(AttributeKeys.FILL_COLOR, Color.WHITE);
        set(STROKE_WIDTH, 1d);
        set(STROKE_DASHES, DASH_PATTERN);
        set(END_DECORATION, new ArrowTip());
        
        setAttributeEnabled(FILL_COLOR, true);
        setAttributeEnabled(END_DECORATION, true);
        setAttributeEnabled(START_DECORATION, true);
        setAttributeEnabled(STROKE_DASHES, true);
        
        // roles will become RoleFigure objects, probably move instantiation to handleConnect()
        
        startMultiplicity = new TextFigure("start mult");
        startRole = new TextFigure("start role");
        endMultiplicity = new TextFigure("end mult");
        endRole = new TextFigure("end role");
        relationship = new TextFigure("relationship");
        // TODO: these layouts are ALL messed up; but it's a start/proof of concept i guess
        // maybe we want to try a DIFFERENT layouter
        setLayouter(new LocatorLayouter());
        startMultiplicity.set(LocatorLayouter.LAYOUT_LOCATOR, new BezierLabelLocator(0.2, Math.PI / 4, 2));
        startRole.set(LocatorLayouter.LAYOUT_LOCATOR, new BezierLabelLocator(0.2, -Math.PI / 4, 2));
        endMultiplicity.set(LocatorLayouter.LAYOUT_LOCATOR, new BezierLabelLocator(1, Math.PI / 4, 2));
        endRole.set(LocatorLayouter.LAYOUT_LOCATOR, new BezierLabelLocator(1, -Math.PI / 4, 2));
        relationship.set(LocatorLayouter.LAYOUT_LOCATOR, new BezierLabelLocator(0.5, -Math.PI / 4, 0));
        
        add(startMultiplicity);
        add(startRole);
        add(endMultiplicity);
        add(endRole);
        add(relationship);        
    }

    /**
     * Checks if two figures can be connected. Implement this method
     * to constrain the allowed connections between figures.
     */
    @Override
    public boolean canConnect(Connector start, Connector end) {
    	if (start.getOwner().equals(end.getOwner())) return false;
    	if ((start.getOwner() instanceof UmlClassFigure) && (end.getOwner() instanceof UmlClassFigure)) {
    		UmlClassFigure sf = (UmlClassFigure) start.getOwner();
    		UmlClassFigure ef = (UmlClassFigure) end.getOwner();
    		
    		if (associationModel.getType() == AssociationType.Inheritance) {
    			if (sf.getModel().hasInheritanceCycle(ef.getModel())) return false;
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
    	if (isBidirectional) ef.getModel().removeAssociation(sf.getModel());
    }

    /**
     * Handles the connection of a connection.
     * Override this method to handle this event.
     */
    @Override
    protected void handleConnect(Connector start, Connector end) {
    	UmlClassFigure sf = (UmlClassFigure) start.getOwner();
    	UmlClassFigure ef = (UmlClassFigure) end.getOwner();

    	associationModel.setTarget(ef.getModel().getName());
        sf.getModel().addAssociation(ef.getModel().getName(), associationModel.getType());
        if (isBidirectional) ef.getModel().addAssociation(sf.getModel().getName(), associationModel.getType());
        
        // store an internal reference to class figures for use in actions later
        startFigure = sf;
        endFigure = ef;
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
				set(STROKE_DASHES, null);
				set(END_DECORATION, new ArrowTip(1, 20.0 , 20.0, false, true, true));
				associationModel.setType(AssociationType.Aggregation);
				
				if(isBidirectional) set(START_DECORATION, AssociationFigure.this.get(END_DECORATION));
		        changed();
			}
    	});
    	actions.add(new AbstractAction(AssociationType.Association.toString()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				// change end decoration to no arrow
				set(STROKE_DASHES, null);
				
				set(START_DECORATION, null);
				if(isBidirectional) set(END_DECORATION, null);
				else set(END_DECORATION, new ArrowTip());
				associationModel.setType(AssociationType.Association);
	        	changed();
			}
    	});
    	actions.add(new AbstractAction(AssociationType.Implementation.toString()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!startFigure.getModel().hasInheritanceCycle(endFigure.getModel())) {
					willChange();
					// change end decoration to white arrow
					set(END_DECORATION, new ArrowTip(0.35, 20.0 , 10.0, true, false, true));
					set(STROKE_DASHES, DASH_PATTERN);					
					associationModel.setType(AssociationType.Implementation);
					
					// destroy bidirectional
					endFigure.getModel().removeAssociation(startFigure.getModel());
					isBidirectional = false;
					set(START_DECORATION, null);
		        	changed();
				}
			}
    	});
    	actions.add(new AbstractAction(AssociationType.Inheritance.toString()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!startFigure.getModel().hasInheritanceCycle(endFigure.getModel())) {
					willChange();
					// change end decoration to white arrow
					set(STROKE_DASHES, null);
					set(END_DECORATION, new ArrowTip(0.35, 20.0 , 10.0, false, true, true));
					associationModel.setType(AssociationType.Inheritance);
					
					// destroy bidirectional
					endFigure.getModel().removeAssociation(startFigure.getModel());
					isBidirectional = false;
					set(START_DECORATION, null);
		        	changed();
				}
			}
    	});
    	actions.add(new AbstractAction(AssociationType.Dependency.toString()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				// change end decoration to default small arrow
				set(STROKE_DASHES, DASH_PATTERN);
				set(END_DECORATION, new ArrowTip());
				associationModel.setType(AssociationType.Dependency);
				
				if(isBidirectional) set(START_DECORATION, AssociationFigure.this.get(END_DECORATION));
	        	changed();
			}
    	});
    	actions.add(new AbstractAction("Bidirectional") {
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				// don't allow setting to toggle if association is inheritance/implementation
				if(associationModel.getType() != AssociationType.Inheritance && associationModel.getType() != AssociationType.Implementation) {
					isBidirectional = !isBidirectional;
				}				
				// change start decoration to matching small arrow
				if(isBidirectional) {
					if (associationModel.getType() == AssociationType.Association) {
						set(START_DECORATION, null);
						set(END_DECORATION, null);
					}
					set(START_DECORATION, AssociationFigure.this.get(END_DECORATION));
					endFigure.getModel().addAssociation(startFigure.getModel().getName(), associationModel.getType());
				}			
				else {
					set(START_DECORATION, null);
					endFigure.getModel().removeAssociation(startFigure.getModel());
					if (associationModel.getType() == AssociationType.Association) {
						set(END_DECORATION, new ArrowTip());
					}
				}
	        	changed();
			}
    	});
    	return actions;
    }
}