package edu.washington.cs.cse403d.coauthor.uiprototype;

// Used to keep track of a global instance of Browser.
public class BrowserService {
	private static Browser browser;
	public static Browser getBrowser() {
		if(browser == null)
			throw new RuntimeException("there is no available browser");
		return browser;
	}
	public static void provideBrowser(Browser theBrowser) {
		browser = theBrowser;
	}
}
