package edu.washington.cs.cse403d.coauthor.client.browser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Base class for a "page" in a browser type system. Derived types should
 * encapsulate their entire state, in addition to providing mechanisms (by
 * populating the JPanel) for rendering that state.
 * 
 * @author William Cauchois
 */
public abstract class BrowserPage extends JPanel {
	private static final long serialVersionUID = -3276055792444479236L;

	public BrowserPage() {
		super();
		isLoaded = false;
	}

	/**
	 * Returns the title for this page (like in HTML). Currently, the only place
	 * the title is used is in the CrumbBar, to provide a label for the button
	 * linked to a page.
	 * 
	 * @return "Untitled" by default
	 */
	public String getTitle() {
		return "Untitled";
	}

	public interface NavListener {
		void onEnter(BrowserPage previous);

		void onExit(BrowserPage next);
	}

	private List<NavListener> listeners = new ArrayList<NavListener>();

	private boolean isLoaded;

	/**
	 * @see #onEnter(BrowserPage)
	 * @see #onExit(BrowserPage)
	 * @see NavListener
	 */
	public void addNavListener(NavListener l) {
		listeners.add(l);
	}

	/**
	 * Invoked when the user navigates to this page. May be called multiple
	 * times during the lifetime of a single BrowserPage (as the user goes back
	 * and forward in history).
	 * 
	 * @see #addNavListener(NavListener)
	 * @param previous
	 *            the page the user just navigated away from
	 */
	protected void onEnter(BrowserPage previous) {
		for (NavListener l : listeners)
			l.onEnter(previous);
	}

	/**
	 * Invoked when the user navigates to another page, so that this page is no
	 * longer active. May be called multiple times during the lifetime of a
	 * single BrowserPage (as the user goes back and forward in history).
	 * 
	 * @see #addNavListener(NavListener)
	 * @param next
	 */
	protected void onExit(BrowserPage next) {
		for (NavListener l : listeners)
			l.onExit(next);
	}
	
	/**
	 * @return True if the page has been loaded once.
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	
	/**
	 * Derived classes should call this at the end of their load() method to
	 * ensure that load() is not called multiple times.
	 */
	protected void setLoaded() {
		isLoaded = true;
	}

	/**
	 * Populate the page with data. A loading spinner is displayed while the
	 * load method is executing. Derived classes should call setLoaded() at the
	 * end of their load() method to ensure that load() is not called multiple
	 * times.
	 * 
	 * This method may throw an exception, in which case a generic page displaying
	 * the error message will be constructed by the Browser -- unless the exception
	 * is an instance of PageLoadError, in which case the user will redirected to
	 * the page provided in the constructor for PageLoadError (this can be used to
	 * implement custom error pages).
	 * 
	 * @see PageLoadError
	 */
	protected void load() throws Exception {
		setLoaded();
	}
}