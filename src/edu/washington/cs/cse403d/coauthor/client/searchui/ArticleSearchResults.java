package edu.washington.cs.cse403d.coauthor.client.searchui;

import javax.swing.JLabel;

import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;

public class ArticleSearchResults extends BrowserPage {
	private static final long serialVersionUID = -5261731609522734709L;

	private String search;

	public ArticleSearchResults(String theQuery) {
		search = theQuery;

	}

	public String getTitle() {
		return search.trim();
	}

	@Override
	protected void load() {
		add(new JLabel("Search results for " + search));
	}
}