package uml.designer;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.MDIApplication;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.SDIApplication;

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
		
		DefaultApplicationModel model = new UmlDesignerApplicationModel();	//TODO: uml designer model
		model.setName("UML Designer");
		model.setVersion(Main.class.getPackage().getImplementationVersion());
		model.setCopyright("Copyright 2006-2010 (c) by the authors of JHotDraw and all its contributors.\n"
				+ "This software is licensed under LGPL and Creative Commons 3.0 Attribution.");
		model.setViewClassName("org.jhotdraw.samples.pert.PertView");	// TODO:
		app.setModel(model);
		app.launch(args);
	}
}
