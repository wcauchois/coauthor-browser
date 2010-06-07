package edu.washington.cs.cse403d.coauthor.client.browser;

public class PageLoadError extends Exception {
	private static final long serialVersionUID = -8813901115319776756L;
	private BrowserPage redirect = null;
	public PageLoadError(BrowserPage redirectTo) {
		redirect = redirectTo;
	}
	public BrowserPage getRedirect() {
		return redirect;
	}
}
