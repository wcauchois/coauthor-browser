package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.List;

import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.browser.MessagePage;
import edu.washington.cs.cse403d.coauthor.client.browser.PageLoadError;
import edu.washington.cs.cse403d.coauthor.client.utils.GoBackActionListener;
import edu.washington.cs.cse403d.coauthor.client.utils.StringUtils;

/**
 * This page displays search results information for single or multiple authors.
 * 
 * @author Kevin Bang
 */
public class AuthorResult extends BrowserPage {
	private static final long serialVersionUID = -3718649382487574216L;

	private List<String> queries;

	/**
	 * Constructor for single author search case
	 * 
	 * @param singleAuthor
	 *            the query
	 */
	public AuthorResult(String singleAuthor) {
		this(Arrays.asList(singleAuthor));
	}

	/**
	 * Constructor for multi entry search case
	 * 
	 * @param queries
	 */
	public AuthorResult(List<String> queries) {
		this.queries = queries;
	}

	@Override
	protected void load() throws PageLoadError {
		this.setVisible(true);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;

		try {
			if (queries.size() == 1) {
				add(new AuthorCoauthorResult(queries.get(0)), c);
			} else
				add(new AuthorCoauthorResult(queries), c);
	
			c.gridx = 0;
			c.gridy = 1;
			if (queries.size() == 1)
				add(new AuthorPublicationResult(queries.get(0)), c);
			else
				add(new AuthorPublicationResult(queries), c);
		} catch(Exception e) {
			MessagePage message = new MessagePage(
					MessagePage.ERROR,
					"Failed to complete search",
					"There was an error while attempting to complete your search (" +
					e.getMessage() + ").",
					MessagePage.GO_BACK);
			message.addActionListener(new GoBackActionListener());
			throw new PageLoadError(message);
		}
		
		setLoaded();
	}

	public String getTitle() {
		if (queries.size() == 1) {
			return queries.get(0);
		} else {
			return StringUtils.elide(StringUtils.join(", ", queries), 20);
		}
	}
}