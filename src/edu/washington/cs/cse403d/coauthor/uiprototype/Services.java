package edu.washington.cs.cse403d.coauthor.uiprototype;

import edu.washington.cs.cse403d.coauthor.dataservice.CoauthorDataServiceInterface;

// Used to keep track of a global instance of Browser.
public class Services {
	private static Browser theBrowser;
	private static ResourceManager theResourceManager;
	private static CoauthorDataServiceInterface theCoauthorDataServiceInterface;
	
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
	public static CoauthorDataServiceInterface getCoauthorDataServiceInterface() {
		return theCoauthorDataServiceInterface;
	}
	
	public static void provideCoauthorDataServiceInterface(
			CoauthorDataServiceInterface coauthorDataServiceInterface) {
		theCoauthorDataServiceInterface = coauthorDataServiceInterface;
	}
	public static void provideResourceManager(ResourceManager resourceManager) {
		theResourceManager = resourceManager;
	}
	public static void provideBrowser(Browser browser) {
		theBrowser = browser;
	}
}
