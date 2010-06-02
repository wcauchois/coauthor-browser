package edu.washington.cs.cse403d.coauthor.client.browser;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.washington.cs.cse403d.coauthor.client.Services;

/**
 * Base class for a "page" in a browser type system. Derived types should
 * encapsulate their entire state, in addition to providing mechanisms (by
 * populating the JPanel) for rendering that state.
 * 
 * @author William Cauchois
 */
public abstract class BrowserPage extends JPanel {
	private static final long serialVersionUID = -3276055792444479236L;

	private final JPanel loadingPanel;
	private boolean isLoading;

	public BrowserPage() {
		super();
		
		isLoaded = false;

		loadingPanel = new JPanel();
		loadingPanel.setLayout(new BoxLayout(loadingPanel, BoxLayout.Y_AXIS));

		JLabel icon = new JLabel(Services.getResourceManager().loadImageIcon("Loading.gif"));
		icon.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel text = new JLabel("Loading...");
		text.setAlignmentX(Component.CENTER_ALIGNMENT);

		loadingPanel.add(icon);
		loadingPanel.add(text);
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
	
	public void setIsLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	/**
	 * Populate the page with data. A loading spinner is displayed while the
	 * load method is executing.
	 */
	abstract protected void load();

	/**
	 * Put the loading icon and text in this BrowserPage
	 */
	public void setIsLoading(boolean isLoading) {
		if (isLoading && !this.isLoading) {
			// Pop out a new loading popup
			this.isLoading = true;

			add(loadingPanel);
		} else if (!isLoading && this.isLoading) {
			// Remove the existing popup
			this.isLoading = false;

			remove(loadingPanel);
		}
	}
}
