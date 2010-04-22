package edu.washington.cs.cse403d.coauthor.uiprototype;

import javax.swing.JPanel;

/**
 * Base class for a "page" in a browser type system. Derived types should
 * encapsulate their entire state, in addition to providing mechanisms
 * (by populating the JPanel) for rendering that state. 
 * @author Bill Cauchois
 */
public abstract class BrowserPage extends JPanel {
	public String getTitle() {
		return "Untitled";
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return null;
	}
}
