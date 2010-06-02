package edu.washington.cs.cse403d.coauthor.client.searchui;

import javax.swing.JLabel;

import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;

public class ArticleSearchResults extends BrowserPage {
	private String search;
	public ArticleSearchResults(String theQuery) {
		search = theQuery;
		add(new JLabel("Search results for " + theQuery));
	}
	
	public String getTitle()
	{
		return search.trim();
	}
}