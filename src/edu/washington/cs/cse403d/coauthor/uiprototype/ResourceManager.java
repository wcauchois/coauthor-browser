package edu.washington.cs.cse403d.coauthor.uiprototype;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class ResourceManager {
	private Map<String, Object> resources = new HashMap<String, Object>();
	private String baseDir;
	private URL getResourceURL(String name) {
		return getClass().getResource(baseDir + name);
	}
	public ImageIcon loadImageIcon(String name) {
		return loadImageIcon(name, name);
	}
	public ImageIcon loadImageIcon(String name, String description) {
		if(resources.containsKey(name))
			return (ImageIcon)resources.get(name);
		else {
			URL imageURL = getResourceURL(name);
			if(imageURL == null)
				throw new RuntimeException("Could not locate resource: " + name);
			return new ImageIcon(imageURL, description);
		}
	}
	public ResourceManager(String baseDir) {
		this.baseDir = baseDir;
	}
}