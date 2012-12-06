package ui.UmlClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jhotdraw.draw.TextFigure;

import domain.UmlClass.UmlAssociationModel;

@SuppressWarnings("serial")
public class MultiplicityFigure extends TextFigure {
	private UmlAssociationModel _assocModel;
	
	public MultiplicityFigure(String text, UmlAssociationModel assocModel) {
		_assocModel = assocModel;
		_assocModel.setMultiplicity(text);
		setText(text);
	}
	
	@Override
	public void setText(String text) {
    	// avoid some issues w/ factory
    	if (_assocModel == null) return;
		String mult;
		Pattern p = Pattern.compile("([\\d]|\\Q*\\E)([.]{2}+([\\d]|\\Q*\\E))?");	// regex for multiplicities: (digit | *) + optional(.. (digit | *))
																					// can use a Collection to handle multiplicities of *; we really only care about
																					// the upper bound also
		Matcher m = p.matcher(text);
		// regex didn't find anything, so we'll assign a default multiplicity
		if (!m.find() || m.group().length() < 0) {
			mult="1";
		}
		else mult = m.group();
		_assocModel.setMultiplicity(mult);
		super.setText(mult);
	}
}
