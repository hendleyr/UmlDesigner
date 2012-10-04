package ui;

import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JToolBar;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.MenuBuilder;
import org.jhotdraw.app.View;

import edu.umd.cs.findbugs.annotations.Nullable;

@SuppressWarnings("serial")
public class UmlDesignerApplicationModel extends DefaultApplicationModel {
	
	/** Default constructor; creates a new instance. */
	public UmlDesignerApplicationModel() {}
	
	@Override
    public void initView(Application a, @Nullable View p) {
		// TODO:
	}
	
	@Override
    public ActionMap createActionMap(Application a, @Nullable View v) {
		return null;// TODO:
    }
    
	/**
     * Creates toolbars for the application.
     */
	@Override
    public List<JToolBar> createToolBars(Application a, @Nullable View pr) {
		return null;// TODO:
	}
	
	/** Creates the MenuBuilder. */
    @Override
    protected MenuBuilder createMenuBuilder() {
    	return null;// TODO:
    }
}
