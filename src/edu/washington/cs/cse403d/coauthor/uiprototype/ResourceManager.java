package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ResourceManager {
	private Map<String, Object> resources = new HashMap<String, Object>();
	private String pathPrefix;
	private URL getResourceURL(String name) {
		return getClass().getResource(pathPrefix + name);
	}
	public ImageIcon loadImageIcon(String name) {
		if(resources.containsKey(name))
			return (ImageIcon)resources.get(name);
		else {
			URL url = getResourceURL(name);
			if(url == null)
				throw new ResourceException(name);
			return new ImageIcon(url, name);
		}
	}
	private static class ResourceException extends RuntimeException {
		public ResourceException(String resourceName, Throwable cause) {
			super("Couldn't load resource " + resourceName, cause);
		}
		public ResourceException(String resourceName) {
			this(resourceName, null);
		}
	}
	private static class StringsBuilder extends DefaultHandler {
		private Map<String, String> strings;
		private String currentString = null;
		private String currentText = null;
		public StringsBuilder(Map<String, String> strings) {
			this.strings = strings;
		}
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if(localName == "string")
				currentString = attributes.getValue("name");
		}
		@Override
		public void characters(char[] ch, int start, int length) {
			currentText = String.copyValueOf(ch, start, length).trim();
		}
		@Override
		public void endElement(String uri, String localName, String qName) {
			if(localName == "string")
				strings.put(currentString, currentText);
		}
	}
	public Map<String, String> loadStrings(String name) {
		if(resources.containsKey(name))
			return (Map<String, String>)resources.get(name);
		else {
			URL url = getResourceURL(name);
			Map<String, String> strings = new HashMap<String, String>();
			try {
				XMLReader xr = XMLReaderFactory.createXMLReader();
				StringsBuilder handler = new StringsBuilder(strings);
				xr.setContentHandler(handler);
				xr.setErrorHandler(handler);
				xr.parse(new InputSource(new FileReader(url.getFile())));
			} catch(Throwable t) {
				throw new ResourceException(name, t);
			}
			return strings;
		}
	}
	public ResourceManager(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}	
}