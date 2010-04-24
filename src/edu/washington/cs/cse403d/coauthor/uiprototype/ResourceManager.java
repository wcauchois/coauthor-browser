package edu.washington.cs.cse403d.coauthor.uiprototype;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;


/* TODO
 * -set up an iterator that contains the filenames in given directory
 * -load ImageIcons using those image names
 * 
 * Need To Know
 * -how to refer to the root directory of the repo
 * -how to obtain names of files within given directory
 */

public class ResourceManager extends Hashtable {
	private Hashtable<String, ImageIcon> manager;
	private List<String> fileNameList;
	private Iterator<String> fileNameIterator = fileNameList.iterator();
	private String path;     // root/assets  (how to refer to the root of repo?)
	private int count;
	
	public ResourceManager() {
		manager = new Hashtable<String, ImageIcon>();
		count = 0;
	}
	
	private void add(String key, ImageIcon icon) {
		manager.put(key, icon);
	}
	
	//build the list of string, which contains filenames
	private void buildList() {
		
	}
	
	//using the list of string, fill the hashtable
	private void load() {
		ImageIcon icon = loadImageIcon("images/questionmark.gif", "Question Mark");
	}
	
	private ImageIcon loadImageIcon(String path, String description) {
		URL url = getClass().getResource(path);
		if (url != null) {
			return new ImageIcon(url, description);
		} else {
			System.err.println("No such file exists: " + path);
			return null;
		}
	}
	
}