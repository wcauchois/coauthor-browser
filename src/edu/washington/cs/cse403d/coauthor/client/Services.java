package edu.washington.cs.cse403d.coauthor.client;

import edu.washington.cs.cse403d.coauthor.client.browser.Browser;
import edu.washington.cs.cse403d.coauthor.client.graphviz.GraphVizManager;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;

/**
 * Has several static methods that can be used to get shared instances of globally
 * available objects, including the current Browser instance, ResourceManager, and
 * CoauthorDataServiceInterface. These services are initialized by calling the
 * provide*() methods when the application is started.
 * @see Browser
 * @see ResourceManager
 * @see CoauthorDataServiceInterface
 * @author William Cauchois
 *
 */
public class Services {
	private static Browser theBrowser;
	private static ResourceManager theResourceManager;
	private static CoauthorDataServiceInterface theCoauthorDataServiceInterface;
	private static GraphVizManager theGraphVizManager;
	
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
	public static GraphVizManager getGraphVizManager() {
		return theGraphVizManager;
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
	public static void provideGraphVizManager(GraphVizManager graphVizManager) {
		theGraphVizManager = graphVizManager;
	}
}
