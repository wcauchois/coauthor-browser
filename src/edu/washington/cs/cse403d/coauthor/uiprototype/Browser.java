package edu.washington.cs.cse403d.coauthor.uiprototype;

public interface Browser {
	/**
	 * Go to the specified page and make it the one presented to the user. To
	 * return to the superseded page (for example), you may use goBack().
	 * @param page this will usually be a new instance
	 */
	void go(BrowserPage page);
	/**
	 * Returns whether there are more recent pages in history.
	 */
	boolean canGoForward();
	/**
	 * Returns whether there are less recent pages in history.
	 */
	boolean canGoBack();
	/**
	 * Go forward to return to a more recent page.
	 * <b>Precondition:</b> canGoForward() is true
	 */
	void goForward();
	/**
	 * Go backward to return to a less recent page.
	 * <b>Precondition:</b> canGoBack() is true
	 */
	void goBack();
}
