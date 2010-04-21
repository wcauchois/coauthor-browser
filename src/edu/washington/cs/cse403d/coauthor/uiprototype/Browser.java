package edu.washington.cs.cse403d.coauthor.uiprototype;

public interface Browser {
	boolean go(BrowserPage page, boolean useHistory);
	void go(BrowserPage page);
	boolean canGoForward();
	boolean canGoBack();
	void goForward();
	void goBack();
}
