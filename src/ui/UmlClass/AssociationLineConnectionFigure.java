package ui.UmlClass;

import java.awt.event.ActionEvent;
import javax.swing.*;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.decoration.ArrowTip;

import domain.UmlClass.AssociationType;
import domain.UmlClass.UmlClassModel;

import java.io.*;

/**
 * An AssociationLineConnection represents an association relationship (has-a)
 * between two classes (represented by their ClassFigures). An association
 * can either be bi-directional or uni-directional. An association can
 * be turned into an aggregation which can be regard a special kind of association.
 */
public class AssociationLineConnectionFigure extends LineConnectionFigure {

    /**
     * PopupMenu for an associations which allows to switch between
     * directed and not directed associations and associations and
     * aggregations
     */
    private transient JPopupMenu myPopupMenu;

    static final long serialVersionUID = 6492295462615980490L;
    
    /*
     * Create a new un-directed AssociationLineConnection
     */
    public AssociationLineConnectionFigure() {
        super();
        
        // we should be able to use the connectors set up in the super class
//        setStartDecoration(null);
//        setEndDecoration(null);
    
        
        // TODO: this is the old way of adding the popup menu
        //setAttribute(Figure.POPUP_MENU, createPopupMenu());
    }
    
    /**
     * Hook method to plug in application behaviour into
     * a template method. This method is called when a
     * connection between two objects has been established.
     */
    @Override
    protected void handleConnect(Connector start, Connector end) {
    	super.handleConnect(start, end);
    	UmlClassModel startClass = ((UmlClassFigure)start.getOwner()).getModellerClass();
    	UmlClassModel endClass = ((UmlClassFigure)end.getOwner()).getModellerClass();
    	
    	// TODO: strategy for determining the association type elegantly
    	startClass.addAssociation(endClass, AssociationType.Dependency);
    	endClass.addAssociation(startClass, AssociationType.Dependency);
    }

    /**
     * Hook method to plug in application behaviour into
     * a template method. This method is called when a 
     * connection between two objects has been cancelled.
     */
    protected void handleDisconnect(Connector start, Connector end) {
        super.handleDisconnect(start, end);
        // this could be an important check depending on how we do undo/redo
        if ((start != null) && (end!= null)) {
        	UmlClassModel startClass = ((UmlClassFigure)start).getModellerClass();
        	UmlClassModel endClass = ((UmlClassFigure)end).getModellerClass();
            startClass.removeAssociation(endClass);
            endClass.removeAssociation(startClass);
        }
    }
    
    /**
     * Sets the named attribute to the new value.
     * Intercept to enable popup menus.
     */
    public void setAttribute(String name, Object value) {
        //TODO: it would be nice to have the pop-up menu working
    	
//    	if (name.equals(Figure.POPUP_MENU)) {
//            myPopupMenu = (JPopupMenu)value;
//        }
//        else {
//            super.setAttribute(name, value);
//        }
    }

    /**
     * Return the named attribute or null if a
     * a figure doesn't have an attribute.
     * All figures support the attribute names
     * FillColor and FrameColor
     */
    public Object getAttribute(String name) {
    	return null;
    	//TODO:    	
//        if (name.equals(Figure.POPUP_MENU)) {
//            return myPopupMenu;
//        }
//        else {
//            return super.getAttribute(name);
//        }
    }

    /**
     * Factory method to create the associated popup menu.
     * It allows switching between associations and aggregation
     * and directed and not-directed associations depending
     * on the current kind of association. For uni-directional
     * associations the reference from the target class to
     * the start class is removed, while for bi-directional
     * associations, this relation is established again.
     *
     * @return newly created popup menu
     */
    protected JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new AbstractAction("aggregation") {
                public void actionPerformed(ActionEvent event) {
                    setAggregation(!isAggregation());
                    if (isAggregation()) {
                        ((JMenuItem)event.getSource()).setText("no aggregation");
                    }
                    else {
                        ((JMenuItem)event.getSource()).setText("aggregation");
                    }
                }
            });
        popupMenu.add(new AbstractAction("uni-directional") {
                public void actionPerformed(ActionEvent event) {
                    setUniDirectional(!isUniDirectional());
                    if (isUniDirectional()) {
                        ((JMenuItem)event.getSource()).setText("bi-directional");
                        UmlClassModel startClass = ((UmlClassFigure)getStartFigure()).getModellerClass();
                        UmlClassModel endClass = ((UmlClassFigure)getEndFigure()).getModellerClass();
                        endClass.addAssociation(startClass, AssociationType.Dependency);
                        			//TODO: determine assoc. type
                    }
                    else {
                        ((JMenuItem)event.getSource()).setText("uni-directional");
                        UmlClassModel startClass = ((UmlClassFigure)getStartFigure()).getModellerClass();
                        UmlClassModel endClass = ((UmlClassFigure)getEndFigure()).getModellerClass();
                        endClass.removeAssociation(startClass);
                    }
                }
            });
            
        popupMenu.setLightWeightPopupEnabled(true);
        return popupMenu;
    }

    /**
     * Turn an association into an aggregation or vice versa.
     * Whether an association is an aggregation is determined
     * by an internal flag that can be set with this method.
     *
     * @param isAggregation true to turn an association into an aggregation, false for the opposite effect
     */
    protected void setAggregation(boolean isAggregation) {
        willChange();
        if (isAggregation) {
        	set(AttributeKeys.START_DECORATION, new AggregationDecorationFigure());
        	// NB:       	
        	//org.jhotdraw.draw.AttributeKeys.START_DECORATION 
        	//org.jhotdraw.draw.AttributeKeys.END_DECORATIO
        }
        else {
        	set(AttributeKeys.START_DECORATION, null);
        }
        change();
        changed();
    }

    /**
     * Test whether an association is an aggregation or not
     *
     * @return true if the association is an aggregation, false otherwise
     */
    protected boolean isAggregation() {
    	// this is kind of stupid b/c we're just checking the state of the ui to determine this,
    	// and ignoring what might be in the model objects. consider revising
        return (get(AttributeKeys.START_DECORATION).getClass()) == AggregationDecorationFigure.class;
    }

    /**
     * Make an association directed or not directed.
     *
     * @param isDirected true for a directed association, false otherwise
     */
    protected void setUniDirectional(boolean isDirected) {
        willChange();
        if (isDirected) {
            ArrowTip arrow = new ArrowTip(0.4, 12.0, 0.0);
            //arrow.setBorderColor(java.awt.Color.black);
            set(AttributeKeys.END_DECORATION, arrow);
        }
        else {
        	set(AttributeKeys.END_DECORATION, null);
        }
        change();
        changed();
    }

    /**
     * Test whether an associations is directed or not
     *
     * @return true, if the association is directed, false otherwise
     */    
    protected boolean isUniDirectional() {
        return get(AttributeKeys.START_DECORATION) != null;
    }

    /**
     * Notify listeners about a change
     */
    protected void change() {
//        if (listener() != null) {
//            listener().figureRequestUpdate(new FigureChangeEvent(this));
//        }
    }   

    
    //NB: think our read strategy is going to be slightly different than what is below
    /**
     * Read a serialized AssociationLineConnection from an input stream and activate the
     * popup menu again.
     */
    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        // call superclass' private readObject() indirectly
        s.defaultReadObject();
        //setAttribute(Figure.POPUP_MENU, createPopupMenu());
    }
}