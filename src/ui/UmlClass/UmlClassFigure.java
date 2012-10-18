package ui.UmlClass;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FONT_BOLD;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_DASHES;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.ListFigure;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.TextFigure;
import javax.swing.AbstractAction;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.samples.pert.figures.SeparatorLineFigure;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import domain.UmlClass.AccessModifier;
import domain.UmlClass.UmlAttributeModel;
import domain.UmlClass.UmlClassModel;
import domain.UmlClass.UmlMethodModel;


@SuppressWarnings("serial")
public class UmlClassFigure extends GraphicalCompositeFigure {
	private int uniqAttrId = 1;
	private int uniqMethodId = 1;
    private static class ClassNameAdapter extends FigureAdapter {
        private UmlClassFigure target;

        public ClassNameAdapter(UmlClassFigure target) {
            this.target = target;
        }
        
        @Override
        public void figureChanged(FigureEvent e) {
        	// VERIFY AND MAP USER INPUT TO MODEL
        	TextFigure nameFigure = target.getNameFigure();        	        	
        	String text = nameFigure.getText();
        	
        	AccessModifier modelAccessModifier;
        	if (text.startsWith("+")) {
        		modelAccessModifier = AccessModifier.Public;
        	}
        	else if (text.startsWith("-")) {
        		modelAccessModifier = AccessModifier.Private;
        	}
        	else if (text.startsWith("#")) {
        		modelAccessModifier = AccessModifier.Protected;
        	}
        	else modelAccessModifier = AccessModifier.Public;
        	
        	Pattern p = Pattern.compile("([\\w]+\\.)*[\\w]+");
        	Matcher m = p.matcher(text);
        	m.find();
        	target.getModel().setAccessModifier(modelAccessModifier);
        	if (m.group().length() > 0) {
        		// found at least one valid identifier
        		nameFigure.setText(modelAccessModifier.getSymbol() + " " + m.group());
        		target.getModel().setName(m.group());
        	}
        	else {
        		// regex failed to find a valid java identifer
        		nameFigure.setText(modelAccessModifier.getSymbol() + " " + "newClass");
        		target.getModel().setName("newClass");
        	}
        }
    }

    private static class AttributeAdapter extends FigureAdapter {
        private UmlClassFigure target;

        public AttributeAdapter(UmlClassFigure target) {
            this.target = target;
        }//TODO: split out these huge blocks

        @Override
        public void figureChanged(FigureEvent e) {        	
        	// VERIFY AND MAP USER INPUT TO MODELS
        	// new strat: reinitialize the model's attr list; examine all text figures in  
        	// the compartment, map them into attrModels, then re-add them to the classModel
        	// if user entered bad data, we assign default values w/ uniqAttrId
        	List<Figure> attrFigures = target.getAttributesCompartment().getChildren();
        	Pattern p = Pattern.compile("([\\w]+)");	// regex for words
        	
        	// reinit class model's attrList
        	target.getModel().setAttributes(new ArrayList<UmlAttributeModel>());
        	for(Figure attrFigure : attrFigures) {
        		String attrText = ((TextFigure)attrFigure).getText();
        		
            	// assess <accessmod>
            	AccessModifier attrAccessModifier;
            	if (attrText.startsWith("+")) {
            		attrAccessModifier = AccessModifier.Public;
            	}
            	else if (attrText.startsWith("-")) {
            		attrAccessModifier = AccessModifier.Private;
            	}
            	else if (attrText.startsWith("#")) {
            		attrAccessModifier = AccessModifier.Protected;
            	}
            	else attrAccessModifier = AccessModifier.Private;
        		
        		// assess <name> : <type>
        		String attrType;
        		String attrName;
            	if (attrText.indexOf(':') == -1) {
            		attrType = "Object";
            		
            		Matcher m = p.matcher(attrText.substring(0));
            		m.find();
            		attrName = m.group();
            	}
            	else {
            		//TODO: use fully qualified java identifier pattern
                	Matcher m = p.matcher(attrText.substring(attrText.indexOf(':')));
                	m.find();
                	attrType = m.group();
            		
                	m = p.matcher(attrText.substring(0, attrText.indexOf(':')));
                	m.find();
                	attrName = m.group();
            	}
            	
            	UmlAttributeModel remappedAttr = new UmlAttributeModel(attrAccessModifier, attrName, attrType);
            	try {
            		target.getModel().addAttribute(remappedAttr);
            	} catch (Exception mapException) {
            		// something failed, supply defaults for user revision
            		remappedAttr = new UmlAttributeModel
            				(AccessModifier.Private, "newAttribute" + target.uniqAttrId, "Object");
            	}
            	((TextFigure)attrFigure).setText
            		(attrAccessModifier.getSymbol() + " " + remappedAttr.getName() + " : " + remappedAttr.getType());
        	}
    	}
    }
    
    private static class MethodAdapter extends FigureAdapter {
        private UmlClassFigure target;

        public MethodAdapter(UmlClassFigure target) {
            this.target = target;
        }

        @Override
        public void figureChanged(FigureEvent evt) {
        }
    }
    
    private HashSet<LineConnectionFigure> myAssociationFigures;    
	private UmlClassModel model;
	
    public UmlClassModel getModel() {
        return model;
    }
    protected void setModel(UmlClassModel newModel) {
    	model = newModel;
    }
    
    public UmlClassFigure() {
        super(new RectangleFigure());
        setModel(new UmlClassModel());
        
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

        TextFigure nameFigure = new TextFigure("newClass");
        nameCompartment.add(nameFigure);
        nameFigure.addFigureListener(new ClassNameAdapter(this));
        
        String attrName = "newAttribute" + uniqAttrId;
        ++uniqAttrId;
        String attrType = "Object";
        AccessModifier attrAccessMod = AccessModifier.Private;
        TextFigure attrFigure = new TextFigure(attrAccessMod.getSymbol() + " " + attrName + " : " + attrType);
        getModel().addAttribute(new UmlAttributeModel(attrAccessMod, attrName, attrType));        
        attributesCompartment.add(attrFigure);
        attrFigure.addFigureListener(new AttributeAdapter(this));
        
        AccessModifier methodAccessMod = AccessModifier.Public;
        String methodName = "newMethod";
        List<UmlAttributeModel> methodParams = new ArrayList<UmlAttributeModel>();
        //getModel().addMethod(new UmlMethodModel(methodAccessMod, methodName, methodParams));
        methodsCompartment.add(new TextFigure(methodAccessMod.getSymbol() + " " + methodName + "(" + ")"));
        //methodsCompartment.add(new TextFigure("+ newMethod()"));
        
        //ResourceBundleUtil labels = ResourceBundleUtil.getBundle("ui.UmlClass.Labels");
    }
    
    protected TextFigure getNameFigure() {
    	ListFigure nameCompartment = (ListFigure) this.getChild(0);
    	TextFigure nameFigure = (TextFigure) nameCompartment.getChild(0);
    	return nameFigure;
    }
    
    protected ListFigure getAttributesCompartment() {
    	ListFigure attributesCompartment = (ListFigure) this.getChild(2);
    	return attributesCompartment;
    }
    
    protected ListFigure getMethodsCompartment() {
    	ListFigure methodsCompartment = (ListFigure) this.getChild(4);
    	return methodsCompartment;
    }
    
    @Override
    public UmlClassFigure clone() {
    	UmlClassFigure that = (UmlClassFigure) super.clone();
    	that.setModel(new UmlClassModel());
        that.myAssociationFigures = new HashSet<LineConnectionFigure>();
        //that.getNameFigure().setText("newClass");
        List<Figure> attrFigureList = that.getAttributesCompartment().getChildren();
        for(int i = 0; i < attrFigureList.size(); ++i) {
        	((TextFigure)attrFigureList.get(i)).addFigureListener(new AttributeAdapter(that));
        }
        that.getNameFigure().addFigureListener(new ClassNameAdapter(that));
        return that;
    }
    
    @Override
    public void write(DOMOutput out) throws IOException {
        Rectangle2D.Double r = getBounds();
        out.addAttribute("x", r.x);
        out.addAttribute("y", r.y);
        writeAttributes(out);
        out.openElement("classModel");
        
        out.openElement("classAccessModifier");
        out.writeObject(getModel().getAccessModifier());
        out.closeElement();
        
        out.openElement("className");
        out.writeObject(getModel().getName());
        out.closeElement();
        
        out.openElement("attributes");
        for(UmlAttributeModel attr : getModel().getAttributes()) {
        	out.openElement("attrAccessModifier");
        	out.writeObject(attr.getAccessModifier());
        	out.closeElement();
        	
        	out.openElement("attrName");
        	out.writeObject(attr.getName());
        	out.closeElement();
        	
        	out.openElement("attrType");
        	out.writeObject(attr.getType());
        	out.closeElement();
        }
        out.closeElement();
        
        out.openElement("methods");
        for(UmlMethodModel method : getModel().getMethods()) {
        	out.openElement("methodAccessModifier");
        	out.writeObject(method.getAccessModifier());
        	out.closeElement();
        	
        	out.openElement("methodType");
        	out.writeObject(method.getReturnType());
        	out.closeElement();
        	
        	out.openElement("methodName");
        	out.writeObject(method.getName());
        	out.closeElement();
        	
        	out.openElement("params");
        	for(UmlAttributeModel param : method.getParameters()) {
        		out.openElement("paramType");
        		out.writeObject(param.getType());
        		out.closeElement();
        		
        		out.openElement("paramName");
        		out.writeObject(param.getName());
        		out.closeElement();
        	}
        	out.closeElement();
        }
        out.closeElement();
        
        out.closeElement();
    }
    
    @Override
    public void read(DOMInput in) throws IOException {
    	// TODO:
        double x = in.getAttribute("x", 0d);
        double y = in.getAttribute("y", 0d);
        double w = in.getAttribute("w", 0d);
        double h = in.getAttribute("h", 0d);
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        readAttributes(in);
        in.openElement("classModel");
        in.openElement("className");
        //setName((String) in.readObject());
        in.closeElement();
        in.closeElement();
    }
        
    @Override
    public Collection<Action> getActions(Point2D.Double p) {
    	Collection<Action> actions = new ArrayList<Action>();
    	actions.add(new AbstractAction(null) {
    		public final static String id = "edit.addAttribute";
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				ResourceBundleUtil labels = ResourceBundleUtil.getBundle("ui.UmlClass.Labels");
				labels.configureAction(this, id);
		        // add a new attribute text figure
				getAttributesCompartment().add(new TextFigure("- newAttribute" + uniqAttrId + ": Object"));
				//TODO: map to model
		        ++uniqAttrId;
		        changed();
			}
    	});
    	actions.add(new AbstractAction(null) {
    		public final static String id = "edit.addMethod";
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				ResourceBundleUtil labels = ResourceBundleUtil.getBundle("ui.UmlClass.Labels");
		        labels.configureAction(this, id);
				// add a new method text figure
		        getMethodsCompartment().add(new TextFigure("+ newMethod" + uniqMethodId + "()"));
		        //TODO: map to model
	        	++uniqMethodId;
	        	changed();
			}
    	});
    	
    	return actions;        
    }
}