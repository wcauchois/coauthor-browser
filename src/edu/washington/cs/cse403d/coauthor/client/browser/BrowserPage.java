package edu.washington.cs.cse403d.coauthor.client.browser;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;


/**
 * Base class for a "page" in a browser type system. Derived types should
 * encapsulate their entire state, in addition to providing mechanisms
 * (by populating the JPanel) for rendering that state.
 * @author William Cauchois
 */
public abstract class BrowserPage extends JPanel {
	/**
	 * Returns the title for this page (like in HTML). Currently, the only place
	 * the title is used is in the CrumbBar, to provide a label for the button
	 * linked to a page.
	 * @return "Untitled" by default
	 */
	public String getTitle() {
		return "Untitled";
	}
	/**
	 * Return a list of classes which should be upwards in the hierarchy relative
	 * to this page, from most recent to least recent.
	 * 
	 * For example, if users usually go from Foo to Bar to Baz (where each of these
	 * classes is derived from BrowserPage). The implicit order here is
	 * Foo -> Bar -> Baz. If we are at Baz, then, the CrumbBar should display links
	 * to Bar and Foo for easy access. Therefore Baz would return the array
	 * { Bar.class, Foo.class }, and the CrumbBar would find instances of those classes
	 * in history and create links to them.
	 * 
	 * Note that order is important!
	 * 
	 * @see CrumbBar
	 * @return an array of classes derived from BrowserPage
	 */
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return null;
	}
	
	public interface NavListener {
		void onEnter(BrowserPage previous);
		void onExit(BrowserPage next);
	}
	private List<NavListener> listeners = new ArrayList<NavListener>();
	/**
	 * @see #onEnter(BrowserPage)
	 * @see #onExit(BrowserPage)
	 * @see NavListener
	 */
	public void addNavListener(NavListener l) {
		listeners.add(l);
	}
	
	/**
	 * Invoked when the user navigates to this page. May be called
	 * multiple times during the lifetime of a single BrowserPage
	 * (as the user goes back and forward in history).
	 * @see #addNavListener(NavListener)
	 * @param previous the page the user just navigated away from
	 */
	protected void onEnter(BrowserPage previous) {
		for(NavListener l : listeners)
			l.onEnter(previous);
	}
	
	/**
	 * Invoked when the user navigates to another page, so that this
	 * page is no longer active. May be called multiple times during
	 * the lifetime of a single BrowserPage (as the user goes back
	 * and forward in history).
	 * @see #addNavListener(NavListener)
	 * @param next
	 */
	protected void onExit(BrowserPage next) {
		for(NavListener l : listeners)
			l.onExit(next);
	}
}
