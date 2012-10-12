
import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.MDIApplication;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.SDIApplication;
import ui.UmlDesignerApplicationModel;

public class Main {
	public static void main(String[] args) {
		Application app;
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("mac")) {
			app = new OSXApplication();
		} else if (os.startsWith("win")) {
			app = new MDIApplication();
		} else {
			app = new SDIApplication();
		}
		
		DefaultApplicationModel model = new UmlDesignerApplicationModel();
		model.setName("UML Designer");
		model.setCopyright("");
		model.setViewClassName("ui.UmlDesignerView");
		app.setModel(model);
		app.launch(args);
	}
}