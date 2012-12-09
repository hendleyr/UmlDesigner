package ui.UmlClass;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Action;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.ListFigure;
import org.jhotdraw.draw.RectangleFigure;
import javax.swing.AbstractAction;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.samples.pert.figures.SeparatorLineFigure;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import domain.UmlClass.AccessModifier;
import domain.UmlClass.AssociationType;
import domain.UmlClass.UmlAssociationModel;
import domain.UmlClass.UmlAttributeModel;
import domain.UmlClass.UmlClassModel;
import domain.UmlClass.UmlMethodModel;

@SuppressWarnings("serial")
public class UmlClassFigure extends GraphicalCompositeFigure {    
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

        // supply a default class name
        ClassNameFigure classNameFigure = new ClassNameFigure("+ newClass", model);
        nameCompartment.add(classNameFigure);
    }
    
    protected ClassNameFigure getNameFigure() {
    	ListFigure nameCompartment = (ListFigure) this.getChild(0);
    	ClassNameFigure classNameFigure = (ClassNameFigure) nameCompartment.getChild(0);
    	return classNameFigure;
    }
    protected ListFigure getAttributesCompartment() {
    	ListFigure attributesCompartment = (ListFigure) this.getChild(2);
    	return attributesCompartment;
    }
    
    protected ListFigure getMethodsCompartment() {
    	ListFigure methodsCompartment = (ListFigure) this.getChild(4);
    	return methodsCompartment;
    }

    //TODO: possibly remove these helpers
	/**
	 * @precondition	nameFigureCompartment has been initialized
	 * @postcondition	nameFigure has been updated with info from model
	 */
    private void drawClass() {
    	String className = getModel().getName();
    	char classAccessMod = getModel().getAccessModifier().getSymbol();
    	getNameFigure().setText(classAccessMod + " " + className);
    }

	/**
	 * @precondition 	attributesCompartment has been initialized
	 * @postcondition	new text figure has been added with info from model
	 * @param 			attrModel	model info to display
	 */
    private void drawAttribute(UmlAttributeModel attrModel) {
    	String attrFigureText = attrModel.getAccessModifier().getSymbol() + " " + attrModel.getName() + " : " + attrModel.getType();
    	AttributeFigure attrFigure = new AttributeFigure(attrFigureText, model, attrModel);
    	getAttributesCompartment().add(attrFigure);
    }
    
    /**
	 * @precondition 				methodsCompartment has been initialized
	 * @postcondition				new text figure has been added with info from model
	 * @param 			methodModel	model info to display
	 */
    private void drawMethod(UmlMethodModel methodModel) {
    	String methodFigureText = methodModel.getAccessModifier().getSymbol() + " " + methodModel.getName() + "(";
    	for (int i = 0; i < methodModel.getParameters().size(); ++i) {
    		if ( i == methodModel.getParameters().size() - 1)
    			methodFigureText+= methodModel.getParameters().get(i).getName() + " : " + methodModel.getParameters().get(i).getType();
    		else
    			methodFigureText+= methodModel.getParameters().get(i).getName() + " : " + methodModel.getParameters().get(i).getType() + ", ";
    	}
    	methodFigureText += ") : " + methodModel.getReturnType();
    	MethodFigure methodFigure = new MethodFigure(methodFigureText, model, methodModel, getNameFigure());
    	getMethodsCompartment().add(methodFigure);
    }
    
    @Override
    public UmlClassFigure clone() {
    	// TODO: i'm not sure if this is correct...
    	UmlClassFigure that = (UmlClassFigure) super.clone();
    	that.setModel(new UmlClassModel());
        
    	// work-around for model issue in the class name figure
        ListFigure nameCompartment = (ListFigure)that.getChild(0);
        nameCompartment.removeAllChildren();
        ClassNameFigure classNameFigure = new ClassNameFigure("+ newClass", that.model);
        nameCompartment.add(classNameFigure);
        
        return that;
    }
    
    //TODO:update for refactoring changes
    @Override
    public void write(DOMOutput out) throws IOException {
        Rectangle2D.Double r = getBounds();
        out.addAttribute("x", r.x);
        out.addAttribute("y", r.y);
        writeAttributes(out);
        out.openElement("classModel");
	    	out.addAttribute("classAbstractFlag", getModel().getAbstractFlag());
	    	out.addAttribute("classInterfaceFlag", getModel().getInterfaceFlag());
        
        	out.openElement("classAccessModifier");
        		out.addText("" + getModel().getAccessModifier().getSymbol());
        	out.closeElement();
        
	        out.openElement("className");
	        	out.addText(getModel().getName());
	        out.closeElement();
	        
	        out.openElement("classAssociations");
		        for(UmlAssociationModel assoc : getModel().getAssociations()) {
		        	out.openElement("assoc");
			        	out.openElement("assocType");
			        		out.addText(assoc.getType().toString());
			        	out.closeElement();
			        	
			        	out.openElement("assocTarget");
			        		out.addText(assoc.getTarget());
			        	out.closeElement();
		        	out.closeElement();
		        }
	        out.closeElement();
        
	        out.openElement("classAttributes");
		        for(UmlAttributeModel attr : getModel().getAttributes()) {
		        	out.openElement("attr");
		        		out.addAttribute("attrStaticFlag", attr.getStaticFlag());
		        	
			        	out.openElement("attrAccessModifier");
			        		out.addText(""+attr.getAccessModifier().getSymbol());
			        	out.closeElement();
			        	
			        	out.openElement("attrName");
			        		out.addText(attr.getName());
			        	out.closeElement();
			        	
			        	out.openElement("attrType");
			        		out.addText(attr.getType());
			        	out.closeElement();
		        	out.closeElement();
		        }
	        out.closeElement();
        
	        out.openElement("methods");
		        for(UmlMethodModel method : getModel().getMethods()) {
		        	out.openElement("method");
		        		out.addAttribute("methodAbstractFlag", method.getAbstractFlag());
		        		out.addAttribute("methodStaticFlag", method.getStaticFlag());
		        		
			        	out.openElement("methodAccessModifier");
			        		out.addText(""+method.getAccessModifier().getSymbol());
			        	out.closeElement();
			        	
			        	out.openElement("methodType");
			        		out.addText(method.getReturnType());
			        	out.closeElement();
			        	
			        	out.openElement("methodName");
			        		out.addText(method.getName());
			        	out.closeElement();
			        	
			        	out.openElement("params");
				        	for(UmlAttributeModel param : method.getParameters()) {
				        		out.openElement("param");
					        		out.openElement("paramType");
					        			out.addText(param.getType());
					        		out.closeElement();
					        		
					        		out.openElement("paramName");
					        			out.addText(param.getName());
					        		out.closeElement();
				        		out.closeElement();
				        	}
			        	out.closeElement();
		        	out.closeElement();
		        }
	        out.closeElement();
	        
        out.closeElement();
    }

    @Override
    public void read(DOMInput in) throws IOException {
    	this.willChange();
    	// remove default text figures
    	getAttributesCompartment().removeAllChildren();
    	getMethodsCompartment().removeAllChildren();
    	
        double x = in.getAttribute("x", 0d);
        double y = in.getAttribute("y", 0d);
        double w = in.getAttribute("w", 0d);
        double h = in.getAttribute("h", 0d);
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        readAttributes(in);
        in.openElement("classModel");
        	getModel().setAbstractFlag(in.getAttribute("classAbstractFlag", false));
        	getModel().setInterfaceFlag(in.getAttribute("classInterfaceFlag", false));
        	readAttributes(in);
        
	        in.openElement("classAccessModifier");
	        	String classAccessMod = in.getText();	// workaround for enum issue
	        	if (classAccessMod.equals("+")) getModel().setAccessModifier(AccessModifier.Public);
	        	else if (classAccessMod.equals("-")) getModel().setAccessModifier(AccessModifier.Private);
	        	else getModel().setAccessModifier(AccessModifier.Protected);
	        in.closeElement();
	        in.openElement("className");
	        	getModel().setName(in.getText());
	        in.closeElement();
	        drawClass();
	        
	        in.openElement("classAssociations");
	        	for (int i = 0; i < in.getElementCount("assoc"); ++i) {
	        		in.openElement("assoc");
			        	in.openElement("assocType");	
			        		String assocText = in.getText();
			        		AssociationType assocType;	// work-around for recurring enum issue...
			        		if (assocText.equals("Inheritance")) assocType = AssociationType.Inheritance;
			        		else if (assocText.equals("Implementation")) assocType = AssociationType.Inheritance;
			        		else if (assocText.equals("Aggregation")) assocType = AssociationType.Inheritance;
			        		else if (assocText.equals("Composition")) assocType = AssociationType.Inheritance;
			        		else if (assocText.equals("Dependency")) assocType = AssociationType.Inheritance;
			        		else assocType = AssociationType.Inheritance;
			        	in.closeElement();
			        	in.openElement("assocTarget");
			        		String assocTarget = in.getText();
			        	in.closeElement();
		        	in.closeElement();
		        	getModel().addAssociation(assocTarget, assocType);
	        	}
	        in.closeElement();
	        
	        in.openElement("classAttributes");
		        for(int i = 0; i < in.getElementCount("attr"); ++i) {
		        	in.openElement("attr");
		        		boolean attrStatic = in.getAttribute("attrStaticFlag", false);
		        		readAttributes(in);
			        	in.openElement("attrAccessModifier");
			        		AccessModifier attrAccessMod;
				        	String attrAccessModText = in.getText();	// workaround for enum issue
				        	if (attrAccessModText.equals("+")) attrAccessMod = AccessModifier.Public;
				        	else if (attrAccessModText.equals("-")) attrAccessMod = AccessModifier.Private;
				        	else attrAccessMod = AccessModifier.Protected;
			        	in.closeElement();
			        	
			        	in.openElement("attrName");
			        		String attrName = in.getText();
			        	in.closeElement();
			        	
			        	in.openElement("attrType");
			        		String attrType = in.getText();
			        	in.closeElement();
		        	in.closeElement();
		        	UmlAttributeModel attrModel = new UmlAttributeModel(attrAccessMod, attrName, attrType);
		        	attrModel.setStaticFlag(attrStatic);
		        	//getModel().addAttribute(attrModel);
		        	drawAttribute(attrModel);
		        }
	        in.closeElement();
	        
	        in.openElement("methods");
	        List<UmlMethodModel> methods = new ArrayList<UmlMethodModel>();
	        for(int i = 0; i < in.getElementCount("method"); ++i) {
	        	in.openElement("method");
	        		boolean methodAbstract = in.getAttribute("methodAbstractFlag", false);
	        		boolean methodStatic = in.getAttribute("methodStaticFlag", false);
	        		readAttributes(in);
		        	in.openElement("methodAccessModifier");
		        		AccessModifier methodAccessMod;
			        	String methodAccessModText = in.getText();	// workaround for enum issue
			        	if (methodAccessModText.equals("+")) methodAccessMod = AccessModifier.Public;
			        	else if (methodAccessModText.equals("-")) methodAccessMod = AccessModifier.Private;
			        	else methodAccessMod = AccessModifier.Protected;
		        	in.closeElement();
		        	
		        	in.openElement("methodType");
		        		String methodType = in.getText();
		        	in.closeElement();
		        	
		        	in.openElement("methodName");
		        		String methodName = in.getText();
		        	in.closeElement();
		        	
		        	in.openElement("params");
		        	List<UmlAttributeModel> methodParams = new ArrayList<UmlAttributeModel>();
			        	for (int j = 0; j < in.getElementCount("param"); ++j) {
			        		in.openElement("param");
				        		in.openElement("paramType");
				        			String paramType = in.getText();
				        		in.closeElement();
				        		
				        		in.openElement("paramName");
				        			String paramName = in.getText();
				        		in.closeElement();
				        	in.closeElement();
			        		methodParams.add(new UmlAttributeModel(null, paramName, paramType));
			        	}
		        	in.closeElement();
	        	in.closeElement();
	        	UmlMethodModel methodModel = new UmlMethodModel(methodAccessMod, methodType, methodName, methodParams);
	        	methodModel.setAbstractFlag(methodAbstract);
	        	methodModel.setStaticFlag(methodStatic);
	        	methods.add(methodModel);
	        	drawMethod(methodModel);
	        }
	        in.closeElement();
	        getModel().setMethods(methods);
        in.closeElement();
        this.changed();
    }
        
    @Override
    public Collection<Action> getActions(Point2D.Double p) {
    	// check if our child compartments contained the right-click (check null for testability workaround)
    	if (p != null) {
    		if(getAttributesCompartment().contains(p)) {
        		for(Figure attrFigure : getAttributesCompartment().getChildren()) {
        			if (attrFigure.contains(p)) return ((AttributeFigure)attrFigure).getActions(p);
        		}
        	}
        	else if(getMethodsCompartment().contains(p)) {
        		for(Figure methodFigure : getMethodsCompartment().getChildren()) {
        			if (methodFigure .contains(p)) return ((MethodFigure)methodFigure ).getActions(p);
        		}
        	}    		
    	}    	
    	// otherwise use actions from self
    	Collection<Action> actions = new ArrayList<Action>();    	
    	actions.add(new AbstractAction("Toggle Interface") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				willChange();
				model.setInterfaceFlag(!model.getInterfaceFlag());
				// if is interface, abstract is redundant
				if (model.getInterfaceFlag()) {
					model.setAbstractFlag(false);
					for (UmlMethodModel method : model.getMethods()){
						method.setStaticFlag(false);
						method.setAbstractFlag(false);
					}
					getNameFigure().set(AttributeKeys.FONT_ITALIC, false);
				}
				getNameFigure().setText(getNameFigure().getText());
				changed();
			}
    	});
    	actions.add(new AbstractAction("Toggle Abstract") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				willChange();
				model.setAbstractFlag(!model.getAbstractFlag());
				model.setInterfaceFlag(false);
				getNameFigure().set(AttributeKeys.FONT_ITALIC, model.getAbstractFlag());
				getNameFigure().setText(getNameFigure().getText());
				changed();
			}
    	});
    	
    	actions.add(new AbstractAction("Add Attribute") {
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				UmlAttributeModel attrModel = new UmlAttributeModel(AccessModifier.Private, "newAttribute", "Object");
				String attrFigureText = attrModel.getAccessModifier().getSymbol() + " " + attrModel.getName() + " : " + attrModel.getType();
				getAttributesCompartment().add(new AttributeFigure(attrFigureText, model, attrModel));
		        changed();
			}
    	});
    	actions.add(new AbstractAction("Add Method") {
			@Override
			public void actionPerformed(ActionEvent e) {
				willChange();
				UmlMethodModel methodModel = new UmlMethodModel(AccessModifier.Public, "Object", "newMethod", new ArrayList<UmlAttributeModel>());
				
		    	String methodFigureText = methodModel.getAccessModifier().getSymbol() + " " + methodModel.getName() + "(";
		    	for (int i = 0; i < methodModel.getParameters().size(); ++i) {
		    		if ( i == methodModel.getParameters().size() - 1)
		    			methodFigureText+= methodModel.getParameters().get(i).getName() + " : " + methodModel.getParameters().get(i).getType();
		    		else
		    			methodFigureText+= methodModel.getParameters().get(i).getName() + " : " + methodModel.getParameters().get(i).getType() + ", ";
		    	}
		    	methodFigureText += ") : " + methodModel.getReturnType();
				
				getMethodsCompartment().add(new MethodFigure(methodFigureText, model, methodModel, getNameFigure()));
	        	changed();
			}
    	});
    	
    	actions.add(new AbstractAction("Generate Code Skeleton File") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String output = getModel().toString();
				String fileName = (model.getName() + ".java");

				try {
					PrintWriter outStream = new PrintWriter(fileName);

					outStream.write(output);
					outStream.close();
				
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
    	});
    	
    	return actions;        
    }
}
