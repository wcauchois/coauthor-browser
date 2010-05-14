package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.List;

import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.StringUtils;

/**
 * This page displays search results information for single or multiple
 * authors.
 * @author Kevin Bang
 */
public class AuthorResult extends BrowserPage {
	private List<String> queries;
	
	public AuthorResult(String singleAuthor) {
		this(Arrays.asList(singleAuthor));
	}
	public AuthorResult(List<String> queries) {
		this.queries = queries;
		initialize();				
	}
	
	private void initialize() {
		this.setVisible(true);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		
		if(queries.size() == 1) {
			add(new MultipleAuthorResult(queries.get(0)), c);
		} else
			add(new MultipleAuthorResult(queries), c);
		
		c.gridx = 0;
		c.gridy = 1;
		if(queries.size() == 1) 
			add(new SingleAuthorResult(queries.get(0)), c);
		else
			add(new SingleAuthorResult(queries), c);
	}
	
	public String getTitle() {
		if(queries.size() == 1) {
			return queries.get(0);
		} else {
			return StringUtils.elide(
					StringUtils.join(", ", queries), 20);
		}
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return new Class[] { SearchPage.class };
	}	
}