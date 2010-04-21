package edu.washington.cs.cse403d.coauthor.uiprototype;

public interface Browser {
	void go(BrowserPage page);
	boolean canGoForward();
	boolean canGoBack();
	void goForward();
	void goBack();
}
