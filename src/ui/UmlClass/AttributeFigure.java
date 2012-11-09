package ui.UmlClass;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.TextFigure;

import domain.UmlClass.AccessModifier;
import domain.UmlClass.UmlAttributeModel;
import domain.UmlClass.UmlClassModel;

@SuppressWarnings("serial")
public class AttributeFigure extends TextFigure{
	private UmlClassModel _classModel;
	private UmlAttributeModel _attrModel;
	
    public AttributeFigure(String text, UmlClassModel classModel, UmlAttributeModel attrModel) {
    	setAttributeEnabled(AttributeKeys.FONT_ITALIC, true);
    	setAttributeEnabled(AttributeKeys.FONT_UNDERLINE, true);
    	
        int uniqAttrId = 1;
        _classModel = classModel;
        _attrModel = attrModel;
        String originalAttrName = _attrModel.getName();
        
        // attempt to add the attrModel supplied by constructor (should typically work); if we fail, try derivations until we work
    	while(!_classModel.addAttribute(_attrModel)) {
    		_attrModel.setName(originalAttrName + uniqAttrId);
    		++uniqAttrId;
    	}
    	setText(text);
    }
    
    @Override
    public void setText(String text) {
    	if (_classModel == null) return;
    	_classModel.removeAttribute(_attrModel);
    	// VERIFY AND MAP USER INPUT TO MODELS
    	// new strat: reinitialize the model's attr list; examine all text figures in  
    	// the compartment, map them into attrModels, then re-add them to the classModel
    	// if user entered bad data, we assign default values w/ uniqAttrId
    	Pattern p = Pattern.compile("([\\w]+)");	// regex for words
		
    	// assess <accessmod>
    	AccessModifier attrAccessModifier;
    	if (text.startsWith("+")) {
    		attrAccessModifier = AccessModifier.Public;
    	}
    	else if (text.startsWith("-")) {
    		attrAccessModifier = AccessModifier.Private;
    	}
    	else if (text.startsWith("#")) {
    		attrAccessModifier = AccessModifier.Protected;
    	}
    	else if (text.startsWith("~")) {
    		attrAccessModifier = AccessModifier.Default;
    	}
    	else attrAccessModifier = AccessModifier.Private;
		
		// assess <name> : <type>
		String attrType;
		String attrName;
    	if (text.indexOf(':') == -1) {
    		attrType = "Object";
    		
    		Matcher m = p.matcher(text.substring(0));
    		m.find();
    		attrName = m.group();
    	}
    	else {
    		//TODO: use fully qualified java identifier pattern
        	Matcher m = p.matcher(text.substring(text.indexOf(':')));
        	m.find();
        	attrType = m.group();
    		
        	m = p.matcher(text.substring(0, text.indexOf(':')));
        	m.find();
        	attrName = m.group();
    	}
    	//
    	_attrModel.setAccessModifier(attrAccessModifier);
    	_attrModel.setName(attrName);
    	_attrModel.setType(attrType);
    	
    	int uniqAttrId = 1;
    	String originalAttrName = _attrModel.getName();
    	while(!_classModel.addAttribute(_attrModel)) {
    		_attrModel.setName(originalAttrName + uniqAttrId);
    		++uniqAttrId;    		
    	}
    	super.setText(_attrModel.getAccessModifier().getSymbol() + " " + _attrModel.getName() + " : " + _attrModel.getType());
    }
    
    @Override
    public Collection<Action> getActions(Point2D.Double p) {
    	Collection<Action> actions = new ArrayList<Action>();
    	actions.add(new AbstractAction("Toggle Static") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AttributeFigure.this.willChange();
				willChange();
				_attrModel.setStaticFlag(!_attrModel.getStaticFlag());
				set(AttributeKeys.FONT_UNDERLINE, _attrModel.getStaticFlag());
				changed();
				AttributeFigure.this.changed();
			}
    	});
		return actions;
    }
}