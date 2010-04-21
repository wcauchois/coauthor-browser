package edu.washington.cs.cse403d.coauthor.uiprototype;

public class BrowserService {
	private static Browser browse;
	public static Browser getBrowser() {
		if(browse == null)
			throw new RuntimeException("there is no available browser");
		return browse;
	}
	public static void provideBrowser(Browser theBrowser) {
		browse = theBrowser;
	}
}
