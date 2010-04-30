package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.util.List;

public class SearchResultsPage extends BrowserPage {
	//private String query;
	private List<String> queries;
	/*
	public SearchResultsPage(String query) {
		this.query = query;
	}*/
	
	public SearchResultsPage(List<String> queries) {
		this.queries = queries;
		System.out.println("Moved to search pane");
		this.setVisible(true);
	}
	
	public String getTitle() {
		return "Results for \"" + queries.get(0) + "\"";
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return new Class[] { SearchPage.class, StartPage.class };
	}
}
