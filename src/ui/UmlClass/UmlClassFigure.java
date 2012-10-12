package ui.UmlClass;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FONT_BOLD;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_DASHES;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.Action;

import org.jhotdraw.app.action.edit.AbstractSelectionAction;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.ListFigure;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.action.AbstractDrawingEditorAction;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.samples.pert.figures.SeparatorLineFigure;
import org.jhotdraw.util.ResourceBundleUtil;

import domain.UmlClass.UmlClassModel;


@SuppressWarnings("serial")
public class UmlClassFigure extends GraphicalCompositeFigure {
    private static class ClassNameAdapter extends FigureAdapter {
        private UmlClassFigure target;

        public ClassNameAdapter(UmlClassFigure target) {
            this.target = target;
        }

        @Override
        public void attributeChanged(FigureEvent e) {
            // We could fire a property change event here, in case
            // some other object would like to observe us.
            //target.firePropertyChange("name", e.getOldValue(), e.getNewValue());
        }
    }

    private static class AttributeAdapter extends FigureAdapter {
        private UmlClassFigure target;

        public AttributeAdapter(UmlClassFigure target) {
            this.target = target;
        }

        @Override
        public void attributeChanged(FigureEvent evt) {
            // We could fire a property change event here, in case
            // some other object would like to observe us.
            //target.firePropertyChange("duration", e.getOldValue(), e.getNewValue());
        }
    }
    
    private static class MethodAdapter extends FigureAdapter {
        private UmlClassFigure target;

        public MethodAdapter(UmlClassFigure target) {
            this.target = target;
        }

        @Override
        public void attributeChanged(FigureEvent evt) {
            // We could fire a property change event here, in case
            // some other object would like to observe us.
            //target.firePropertyChange("duration", e.getOldValue(), e.getNewValue());
        }
    }
    
    private HashSet<LineConnectionFigure> myAssociationFigures;
	
//    private Font attributeFont;
//    private Font methodFont;
    
	private UmlClassModel model;
	
    public UmlClassModel getModel() {
        return model;
    }
    protected void setModel(UmlClassModel newModel) {
    	model = newModel;
    }
    
    public UmlClassFigure() {
        super(new RectangleFigure());
        setLayouter(new VerticalLayouter());

        RectangleFigure nameCompartmentPF = new RectangleFigure();
        nameCompartmentPF.set(STROKE_COLOR, null);
        nameCompartmentPF.set(FILL_COLOR, null);
        RectangleFigure attributesCompartmentPF = new RectangleFigure();
        attributesCompartmentPF.set(STROKE_COLOR, null);
        attributesCompartmentPF.set(FILL_COLOR, null);
        RectangleFigure methodsCompartmentPF = new RectangleFigure();
        methodsCompartmentPF.set(STROKE_COLOR, null);
        methodsCompartmentPF.set(FILL_COLOR, null);
        
        ListFigure nameCompartment = new ListFigure(nameCompartmentPF); 			// child 0
        SeparatorLineFigure separator1 = new SeparatorLineFigure();					// child 1
        ListFigure attributesCompartment = new ListFigure(attributesCompartmentPF);	// child 2
        SeparatorLineFigure separator2 = new SeparatorLineFigure();					// child 3
        ListFigure methodsCompartment = new ListFigure(methodsCompartmentPF);		// child 4

        add(nameCompartment);
        add(separator1);
        add(attributesCompartment);
        add(separator2);
        add(methodsCompartment);

        Insets2D.Double insets = new Insets2D.Double(4, 8, 4, 8);
        nameCompartment.set(LAYOUT_INSETS, insets);
        attributesCompartment.set(LAYOUT_INSETS, insets);
        methodsCompartment.set(LAYOUT_INSETS, insets);

        nameCompartment.add(new TextFigure("newClass"));
        attributesCompartment.add(new TextFigure("- newAttribute: Object"));
        methodsCompartment.add(new TextFigure("+ newMethod()"));

        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("ui.UmlClass.Labels");
    }
    
    @Override
    public Collection<Action> getActions(Point2D.Double p) {
    	Collection<Action> actions = new ArrayList<Action>();
    	actions.add(new AbstractSelectionAction(null) {
    		public final static String id = "edit.addAttribute";
			@Override
			public void actionPerformed(ActionEvent e) {
				// add a new attribute text figure
				ResourceBundleUtil labels = ResourceBundleUtil.getBundle("ui.UmlClass.Labels");
		        labels.configureAction(this, id);
		        ((ListFigure)getChild(2)).add(new TextFigure("- newAttribute: Object"));				
			}
    	});
    	actions.add(new AbstractSelectionAction(null) {
    		public final static String id = "edit.addMethod";
			@Override
			public void actionPerformed(ActionEvent e) {
				// add a new method text figure
				 ((ListFigure)getChild(4)).add(new TextFigure("+ newMethod()"));
			}			
    	});
    	
    	return actions;        
    }
}
