package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SearchResultsPage extends BrowserPage {
	private List<String> queries;
	
	private class SingleAuthorSearchResult extends JPanel {
		private String author;
		
		public SingleAuthorSearchResult() {
			
		}
	}
	
	
	
	public SearchResultsPage(List<String> queries) {
		this.queries = queries;
		System.out.println("Moved to search pane");
		this.setVisible(true);
		initialize();
	}
	
	public String getTitle() {
		return "Results for \"" + queries.get(0) + "\"";
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return new Class[] { StartPage.class };
	}
	
	private void initialize() {
		final String test = "test";
		JPanel panel = new JPanel();
		panel.setVisible(true);
		add(panel);
		JLabel label = new JLabel(test);
		label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Services.getBrowser().go(new SearchResultsPage(createList(test)));
			}
		});
		panel.add(label);
	}
	
	private List<String> createList(String str){
		List<String> authors = new ArrayList<String>();
		authors.add(str);
		return authors;
	}	
}
