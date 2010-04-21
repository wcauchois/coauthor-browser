package edu.washington.cs.cse403d.coauthor.uiprototype;

public class BrowserService {
	private Browser browse;
	public Browser getBrowser() {
		if(browse == null)
			throw new RuntimeException("there is no available browser");
		return browse;
	}
	public void provideBrowser(Browser browse) {
		this.browse = browse;
	}
}
