package ui.UmlClass;

import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.layouter.LocatorLayouter;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.locator.BezierLabelLocator;
import org.jhotdraw.draw.locator.BezierPointLocator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;

import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import domain.UmlClass.AssociationType;
import domain.UmlClass.UmlAssociationModel;

@SuppressWarnings("serial")
public class AssociationFigure extends LabeledLineConnectionFigure {
	private static final double[] DASH_PATTERN = new double[] {10};
	
	// layout thoughts: multiplicities above the line near the arrow, roles below the line near the arrow,
	//					relationship above the line centered horizontally
	
	private MultiplicityFigure sMult;
	private MultiplicityFigure eMult;
	
	private TextFigure relationship;
	
	private RoleFigure startRole;
	private RoleFigure endRole;
	
	private UmlAssociationModel associationModel;
	private UmlAssociationModel bidirectionalAssocModel;
	private UmlClassFigure startFigure;
	private UmlClassFigure endFigure;
	boolean isBidirectional;
	
    /** Creates a new instance. */
    public AssociationFigure() {
    	associationModel = new UmlAssociationModel(null, AssociationType.Dependency);
    	bidirectionalAssocModel = new UmlAssociationModel(null, AssociationType.Dependency);
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
        
        // TODO: move instantiation of role/multiplicities to handleConnect()/actions? private methods for changing association type?     
        setLayouter(new LocatorLayouter());
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
    	sf.getModel().addAssociation(associationModel);
    	if (isBidirectional) {
    		bidirectionalAssocModel = new UmlAssociationModel(sf.getModel().getName(), associationModel.getType(), 
    				endRole.getText(), eMult.getText());
    		ef.getModel().addAssociation(bidirectionalAssocModel);
    	}
        //sf.getModel().addAssociation(ef.getModel().getName(), associationModel.getType());
        //if (isBidirectional) ef.getModel().addAssociation(sf.getModel().getName(), associationModel.getType());
        
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
				
				//destroy any existing multiplicities/roles/relationships
		        remove(sMult);
		        remove(startRole);
		        remove(eMult);
		        remove(endRole);
		        remove(relationship);
		        
		        startRole = new RoleFigure("start role", associationModel);
		        endRole = new RoleFigure("end role", bidirectionalAssocModel);
		        relationship = new TextFigure("relationship");
		      
		        startRole.set(LocatorLayouter.LAYOUT_LOCATOR, new AnnotationLocator(5, 0));
		        endRole.set(LocatorLayouter.LAYOUT_LOCATOR, new AnnotationLocator(5, .9));
		        relationship.set(LocatorLayouter.LAYOUT_LOCATOR, new AnnotationLocator(5, .5));
		        
		        add(startRole);
		        add(endRole);
		        add(relationship);
		        
				if(isBidirectional) {
					bidirectionalAssocModel.setType(associationModel.getType());
					bidirectionalAssocModel.setRoleName(endRole.getText());
					bidirectionalAssocModel.setMultiplicity(null);
					set(START_DECORATION, AssociationFigure.this.get(END_DECORATION));
				}
		        
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
				
				associationModel.setType(AssociationType.Association);
				
				//destroy any existing multiplicities/roles/relationships
		        remove(sMult);
		        remove(startRole);
		        remove(eMult);
		        remove(endRole);
		        remove(relationship);
		        
	            sMult = new MultiplicityFigure("1", associationModel);
	            startRole = new RoleFigure("start role", associationModel);
	            eMult = new MultiplicityFigure("1", associationModel);
	            endRole = new RoleFigure("end role", bidirectionalAssocModel);
	            relationship = new TextFigure("relationship");
	          
	            sMult.set(LocatorLayouter.LAYOUT_LOCATOR, new AnnotationLocator(-5, 0));
	            startRole.set(LocatorLayouter.LAYOUT_LOCATOR, new AnnotationLocator(5, 0));
	            eMult.set(LocatorLayouter.LAYOUT_LOCATOR, new AnnotationLocator(-5, .9));
	            endRole.set(LocatorLayouter.LAYOUT_LOCATOR, new AnnotationLocator(5, .9));
	            relationship.set(LocatorLayouter.LAYOUT_LOCATOR, new AnnotationLocator(5, .5));
	            
	            add(sMult);
	            add(startRole);
	            add(eMult);
	            add(endRole);
	            add(relationship);
	            
	            if(isBidirectional) {
					bidirectionalAssocModel.setType(associationModel.getType());
					bidirectionalAssocModel.setRoleName(endRole.getText());
					bidirectionalAssocModel.setMultiplicity(eMult.getText());
					set(END_DECORATION, null);
				}
				else set(END_DECORATION, new ArrowTip());
	            
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
					
					//destroy any existing multiplicities/roles/relationships
			        remove(sMult);
			        remove(startRole);
			        remove(eMult);
			        remove(endRole);
			        remove(relationship);
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
					
					//destroy any existing multiplicities/roles/relationships
			        remove(sMult);
			        remove(startRole);
			        remove(eMult);
			        remove(endRole);
			        remove(relationship);
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
				
				if(isBidirectional) {
					bidirectionalAssocModel.setType(null);
					bidirectionalAssocModel.setRoleName(null);
					bidirectionalAssocModel.setMultiplicity(null);
					set(START_DECORATION, AssociationFigure.this.get(END_DECORATION));
				}
				else set(END_DECORATION, new ArrowTip());
				
				//destroy any existing multiplicities/roles/relationships
		        remove(sMult);
		        remove(startRole);
		        remove(eMult);
		        remove(endRole);
		        remove(relationship);
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
					bidirectionalAssocModel = new UmlAssociationModel(startFigure.getModel().getName(), associationModel.getType());
					if (associationModel.getType() == AssociationType.Association) {
						set(START_DECORATION, null);
						set(END_DECORATION, null);
					}
					if (eMult != null)
						bidirectionalAssocModel.setMultiplicity(eMult.getText());
					if (endRole != null)
						bidirectionalAssocModel.setRoleName(endRole.getText());
					set(START_DECORATION, AssociationFigure.this.get(END_DECORATION));
					endFigure.getModel().addAssociation(bidirectionalAssocModel);
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
    
//    @Override
//    public void read(DOMInput in) throws IOException {
//    	
//    }
//    
//    @Override
//    public void write(DOMOutput out) throws IOException {
//    	
//    }
}