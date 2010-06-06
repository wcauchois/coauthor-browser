package edu.washington.cs.cse403d.coauthor.client.browser;

public class PageLoadError extends Exception {
	private BrowserPage redirect = null;
	public PageLoadError(BrowserPage redirectTo) {
		redirect = redirectTo;
	}
	public BrowserPage getRedirect() {
		return redirect;
	}
}
