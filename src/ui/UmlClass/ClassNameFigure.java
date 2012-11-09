package ui.UmlClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jhotdraw.draw.TextFigure;

import domain.UmlClass.AccessModifier;
import domain.UmlClass.UmlClassModel;

@SuppressWarnings("serial")
public class ClassNameFigure extends TextFigure{
	private UmlClassModel _classModel;
	
    public ClassNameFigure(String text, UmlClassModel classModel) {
        _classModel = classModel;
        setText(text);
    }
    
    @Override
    public void setText(String text) {
    	// VERIFY AND MAP USER INPUT TO MODEL
    	// remove the interface tag before examining the string
    	if (_classModel == null) return;
    	text = text.replace("<<interface>>\n", "");
    	
    	AccessModifier modelAccessModifier;
    	if (text.startsWith("+")) {
    		modelAccessModifier = AccessModifier.Public;
    	}
    	else if (text.startsWith("-")) {
    		modelAccessModifier = AccessModifier.Private;
    		// can't have private interface
    		if (_classModel.getInterfaceFlag()) modelAccessModifier = AccessModifier.Public;
    	}
    	else if (text.startsWith("#")) {
    		modelAccessModifier = AccessModifier.Protected;
    	}
    	else if (text.startsWith("~")) {
    		modelAccessModifier = AccessModifier.Default;
    	}
    	else modelAccessModifier = AccessModifier.Public;
    	_classModel.setAccessModifier(modelAccessModifier);
    	
    	Pattern p = Pattern.compile("([\\w]+\\.)*[\\w]+");
    	Matcher m = p.matcher(text);
    	m.find();
    	
    	if (m.group().length() > 0) _classModel.setName(m.group());
    	else _classModel.setName("newClass");
        
    	String className = _classModel.getName();
    	char classAccessMod = _classModel.getAccessModifier().getSymbol();
    	if (_classModel.getInterfaceFlag()) {
    		super.setText("<<interface>>\n" + classAccessMod + " " + className);
    	}
    	else {
    		super.setText(classAccessMod + " " + className);
    	}
    }
}