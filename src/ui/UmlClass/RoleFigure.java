package ui.UmlClass;

import org.jhotdraw.draw.TextFigure;

import domain.UmlClass.UmlAssociationModel;

@SuppressWarnings("serial")
public class RoleFigure extends TextFigure {
	//private UmlClassModel _classModel;
	private UmlAssociationModel _assocModel;
	
	public RoleFigure(String text, UmlAssociationModel assocModel) {
		_assocModel = assocModel;
		_assocModel.setRoleName(text);
		setText(text);
	}
	
	@Override
	public void setText(String text) {
		_assocModel.setRoleName(text);
		super.setText(text);
	}
}
