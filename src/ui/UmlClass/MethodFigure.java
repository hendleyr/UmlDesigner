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
import domain.UmlClass.UmlMethodModel;

@SuppressWarnings("serial")
public class MethodFigure extends TextFigure{
	private UmlClassModel _classModel;
	private UmlMethodModel _methodModel;
	private ClassNameFigure _classNameFigure; // needed in order to alter text attribute of the class name figure when making a method abstract
	
    public MethodFigure(String text, UmlClassModel classModel, UmlMethodModel methodModel, ClassNameFigure classNameFigure) {
    	setAttributeEnabled(AttributeKeys.FONT_ITALIC, true);
    	setAttributeEnabled(AttributeKeys.FONT_UNDERLINE, true);
    	
        int uniqMethodId = 1;
        _classModel = classModel;
        _methodModel = methodModel;
        _classNameFigure = classNameFigure;
        String originalMethodName = _methodModel.getName();
        while(!_classModel.addMethod(_methodModel)) {
        	_methodModel.setName(originalMethodName + uniqMethodId);
        	++uniqMethodId;
        }
        setText(text);
    }
    
    @Override
    public void setText(String text) {
    	if (_classModel == null) return;
        // strat: remove the method model from the class model, perform operations on the text, 
    	// map the results to the method model, re-add the method model
    	_classModel.removeMethod(_methodModel);
    	
    	Pattern p = Pattern.compile("([\\w]+)");	// regex for words
		
    	// assess <accessmod>
    	AccessModifier methodAccessModifier;
    	if (text.startsWith("+")) {
    		methodAccessModifier = AccessModifier.Public;
    	}
    	else if (text.startsWith("-")) {
    		methodAccessModifier = AccessModifier.Private;
    	}
    	else if (text.startsWith("#")) {
    		methodAccessModifier = AccessModifier.Protected;
    	}
    	else if (text.startsWith("~")) {
    		methodAccessModifier = AccessModifier.Default;
    	}
    	else methodAccessModifier = AccessModifier.Private;
    		
    	// assess <name>(<param>:<paramType>) : <type>
    	String methodType;
    	String methodName;

    	ArrayList<UmlAttributeModel> params = new ArrayList<UmlAttributeModel>();
    		
    	//if user input does not have a valid set of open and close parentheses or has no colons,
    	//assign default values
    	if (text.indexOf('(') == -1 || text.indexOf(')') == -1 || 
    			text.indexOf(':') == -1 || text.indexOf(')') < text.indexOf('(')) {
    		methodType = "Object";
    		
    		Matcher m = p.matcher(text.substring(0));
    		m.find();
    		methodName = m.group();
    	}
    	else {
    		String nameSubstring = text.substring(0, text.indexOf('('));
    		String paramSubstring = text.substring(text.indexOf('(') + 1,
    				text.indexOf(')'));
    		String afterParen = text.substring(text.indexOf(')'));
    		
    		if (afterParen.indexOf(':') == -1){
    			methodType = "Object";	
    		}
    		else {
    			String typeSubstring = afterParen.substring(afterParen.indexOf(':'));
    			
    			Matcher m = p.matcher(typeSubstring);
    			m.find();
    			methodType = m.group();
    		}

    		Matcher m = p.matcher(nameSubstring);
    		m.find();
    		methodName = m.group();
    			
    		int commaIndex = paramSubstring.indexOf(',');
    		String paramName;
    		String paramType;
    		
    		if (paramSubstring.equals("")){
    		}
    		
    		//case for only 1 param; the rest of this could probably be more elegant but
    		//this should do for our purposes.
    		else if (commaIndex == -1){
    			if (paramSubstring.indexOf(':') == -1){
    				
    				m = p.matcher(paramSubstring);
    				m.find();
    				paramName = m.group();
    				
    				paramType = "Object";
    			}
    			else {
    				m = p.matcher(paramSubstring.substring(0, paramSubstring.indexOf(':')));
    				m.find();
    				paramName = m.group();
    				
    				m = p.matcher(paramSubstring.substring(paramSubstring.indexOf(':')));
    				m.find();
    				paramType = m.group();
    			}
				params.add(new UmlAttributeModel(AccessModifier.Private, paramName, paramType));
    		}
    		//case for multiple parameters
    		else {
    			String[] paramStrings = paramSubstring.split(",");
    			for (String param : paramStrings){
    				if (param.indexOf(':') == -1){
    					m = p.matcher(param);
    					m.find();
    					paramName = m.group();
    					paramType = "Object";
    				}
    				else {
    					m = p.matcher(param.substring(0, param.indexOf(':')));
    					m.find();
    					paramName = m.group();
    					
    					m = p.matcher(param.substring(param.indexOf(':')));
    					m.find();
    					paramType = m.group();
    				}
    				params.add(new UmlAttributeModel(AccessModifier.Private, paramName, paramType));
    			}
    		}        			
    	}

    	String paramString = "";
    	if (params.size() == 1){
    		paramString = params.get(0).getName() + " : " + params.get(0).getType();
    	}
    	else if (params.size() > 1){
    		for (int i = 0; i < params.size() - 1; i++){
    			paramString += params.get(i).getName() + " : " + params.get(i).getType() + ", ";
    		}
    		paramString += params.get(params.size() - 1).getName() + " : " + params.get(params.size() - 1).getType();
    	}
    	
    	_methodModel.setAccessModifier(methodAccessModifier);
    	_methodModel.setReturnType(methodType);
    	_methodModel.setName(methodName);
    	_methodModel.setParameters(params);
    	
    	int uniqMethodId = 1;
    	String originalMethodName = _methodModel.getName();
    	while(!_classModel.addMethod(_methodModel)) {
    		_methodModel.setName(originalMethodName + uniqMethodId);
    		++uniqMethodId;    		
    	}
    	
        super.setText(_methodModel.getAccessModifier().getSymbol() + " " + _methodModel.getName() + "(" + paramString +
				")" + " : " + _methodModel.getReturnType());
    }
    
    @Override
    public Collection<Action> getActions(Point2D.Double p) {
    	Collection<Action> actions = new ArrayList<Action>();
    	actions.add(new AbstractAction("Abstract Method") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// N.B.: not sure why willChanged() needs to be invoked in pairs like this, but it works most reliably when you do... sigh
				MethodFigure.this.willChange();
				willChange();
				_methodModel.setAbstractFlag(!_methodModel.getAbstractFlag());
				// if abstract, cannot be static; also, class must be abstract
				if (_methodModel.getAbstractFlag()) {
					_methodModel.setStaticFlag(false);
					set(AttributeKeys.FONT_UNDERLINE, false);
					_classModel.setAbstractFlag(true);
					_classNameFigure.willChange();
					_classNameFigure.set(AttributeKeys.FONT_ITALIC, true);
					_classNameFigure.changed();
				}
				set(AttributeKeys.FONT_ITALIC, _methodModel.getAbstractFlag());
				changed();
				MethodFigure.this.changed();
			}
    	});
    	actions.add(new AbstractAction("Static Method") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MethodFigure.this.willChange();
				willChange();
				_methodModel.setStaticFlag(!_methodModel.getStaticFlag());
				// if static, cannot be abstract
				if (_methodModel.getStaticFlag()) {
					_methodModel.setAbstractFlag(false);
					set(AttributeKeys.FONT_ITALIC, false);
				}
				set(AttributeKeys.FONT_UNDERLINE, _methodModel.getStaticFlag());
				changed();
				MethodFigure.this.changed();
			}
    	});
		return actions;
    }
}