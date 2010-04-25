package edu.washington.cs.cse403d.coauthor.uiprototype;

// Used to keep track of a global instance of Browser.
public class Services {
	private static Browser theBrowser;
	private static ResourceManager theResourceManager;
	
	public static ResourceManager getResourceManager() {
		if(theResourceManager == null)
			throw new RuntimeException("There is no available resource manager");
		return theResourceManager;
	}
	public static Browser getBrowser() {
		if(theBrowser == null)
			throw new RuntimeException("There is no available browser");
		return theBrowser;
	}
	
	public static void provideResourceManager(ResourceManager resourceManager) {
		theResourceManager = resourceManager;
	}
	public static void provideBrowser(Browser browser) {
		theBrowser = browser;
	}
}
