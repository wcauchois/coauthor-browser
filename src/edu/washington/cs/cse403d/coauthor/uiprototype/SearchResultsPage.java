package edu.washington.cs.cse403d.coauthor.uiprototype;

public class SearchResultsPage extends BrowserPage {
	private String query;
	public SearchResultsPage(String query) {
		this.query = query;
	}
	public String getTitle() {
		return "Results for \"" + query + "\"";
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return new Class[] { SearchPage.class, StartPage.class };
	}
}
