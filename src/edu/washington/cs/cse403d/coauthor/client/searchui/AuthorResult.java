package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.browser.MessagePage;
import edu.washington.cs.cse403d.coauthor.client.browser.PageLoadError;
import edu.washington.cs.cse403d.coauthor.client.utils.GoBackActionListener;
import edu.washington.cs.cse403d.coauthor.client.utils.HyperLinkButton;
import edu.washington.cs.cse403d.coauthor.client.utils.StringUtils;
import edu.washington.cs.cse403d.coauthor.shared.model.AuthorNotFoundException;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * This page displays search results information for single or multiple authors.
 * 
 * @author Kevin Bang
 */
public class AuthorResult extends BrowserPage {
	private static final long serialVersionUID = -3718649382487574216L;

	private List<String> authorNames;

	/**
	 * Constructor for single author search case
	 * 
	 * @param singleAuthor
	 *            the query
	 */
	public AuthorResult(String singleAuthor) {
		this(getProperName(Arrays.asList(singleAuthor)));
	}

	/**
	 * Constructor for multi entry search case
	 * 
	 * @param queries
	 */
	public AuthorResult(List<String> queries) {
		this.authorNames = getProperName(queries);
	}
	
	private class AuthorSuggestions extends JPanel {
		public AuthorSuggestions(String searchQuery) {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			List<String> suggestions = null;
			try {
				suggestions = Services.getCoauthorDataServiceInterface(
						).getAuthorSuggestions(searchQuery);
			} catch(Exception e) {
				e.printStackTrace(System.err);
			}
			
			add(Box.createVerticalStrut(20));
			JLabel didYouMean = new JLabel("Did you mean...");
			didYouMean.setHorizontalAlignment(JLabel.CENTER);
			didYouMean.setFont(didYouMean.getFont().deriveFont(Font.BOLD, 16));
			add(didYouMean);
			add(Box.createVerticalStrut(10));
			
			final JList suggestionsList = new JList(suggestions.toArray());
			JScrollPane suggestionsScroller = new JScrollPane(suggestionsList);
			add(suggestionsScroller);
			
			JButton search = new JButton("Search");
			JPanel justifier = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			justifier.add(search);
			add(justifier);
			
			search.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					if(suggestionsList.getSelectedIndex() >= 0) {
						Services.getBrowser().go(
								new AuthorResult(suggestionsList.getSelectedValue().toString()));
					}
				}
			});
			
			setPreferredSize(new Dimension(200, 150));
		}
	}

	@Override
	protected void load() throws Exception {
		setVisible(true);
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;

		try {
			switch (authorNames.size()) {
			case 0:
				throw new IllegalStateException("No authors to search over");
			case 1:
				add(createAuthorHeader(authorNames.get(0), false), c);
				c.gridy++;
				// Single Author Results
				// Add the CoauthorResults
				add(new AuthorCoauthorResult(authorNames.get(0)), c);
				c.gridy++;
				// Add the PublicationResults
				add(new AuthorPublicationResult(authorNames), c);
				break;
			case 2:
				// Multi Author Publication Search if exists
				// ELSE Chain Search
				// Add the Multi Author Header
				add(createMultiAuthorHeader(authorNames), c);
				c.gridy++;
				try {
					List<Publication> groupPublications = Services.getCoauthorDataServiceInterface()
							.getPublicationsForAllAuthors(authorNames.toArray(new String[0]));

					if (groupPublications.isEmpty()) {
						// Add the chain search panel
						add(new ChainSearchResult(authorNames.get(0), authorNames.get(1)), c);
					} else {
						// Results in two queries being performed. However, they
						// are IDENTICAL and therefore will have cache hit with
						// low latency.
						// Add the publications shared by these authors
						add(new AuthorPublicationResult(authorNames), c);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			default:
				// Multi Author Publication Search
				add(createMultiAuthorHeader(authorNames), c);
				c.gridy++;
				// Add the publications shared by these authors
				add(new AuthorPublicationResult(authorNames), c);
				break;
			}
		} catch(AuthorNotFoundException e) {
			MessagePage message = new MessagePage(MessagePage.WARNING, "Author not found",
					"Sorry, but the author you requested (" + e.getMessage() + ") was not found. Please make sure you entered their full name correctly and try again.",
					MessagePage.GO_BACK);
			message.addActionListener(new GoBackActionListener());
			if(authorNames.size() == 1) {
				JPanel wrapper = new JPanel();
				wrapper.add(new AuthorSuggestions(authorNames.get(0)));
				message.add(wrapper, BorderLayout.SOUTH);
			}
			throw new PageLoadError(message);
		}

		setLoaded();
	}

	private JPanel createAuthorHeader(final String author, boolean asLink) {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		result.setVisible(true);

		// The Author Name.
		JLabel title = new JLabel("Author");
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		result.add(title);

		result.add(new JSeparator(SwingConstants.HORIZONTAL));

		if (asLink) {
			HyperLinkButton authorLink = new HyperLinkButton(author);
			authorLink.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Services.getBrowser().go(new AuthorResult(author));
				}
			});
			result.add(authorLink);
		} else {
			JLabel authorLabel = new JLabel(author);
			result.add(authorLabel);
		}
		
		result.add(Box.createVerticalStrut(10));

		return result;
	}

	private JPanel createMultiAuthorHeader(List<String> authorNames) {
		JPanel result = createAuthorHeader(authorNames.get(0), true);

		// Coauthors
		JLabel coauthor = new JLabel(authorNames.size() > 2 ? "Coauthors" : "Coauthor");
		Font f = coauthor.getFont();
		float s = coauthor.getFont().getSize2D();
		s += 8.0f;
		coauthor.setFont(f.deriveFont(s));
		result.add(coauthor);
		result.add(new JSeparator(SwingConstants.HORIZONTAL));

		for (int i = 1; i < authorNames.size(); ++i) {
			result.add(createCoauthorLink(authorNames.get(i)));
			result.add(Box.createVerticalStrut(3));
		}
		result.add(Box.createVerticalStrut(10));

		return result;
	}

	/**
	 * Create a formatted display of coauthors for multi author search result
	 */
	private HyperLinkButton createCoauthorLink(String coauthorName) {
		final HyperLinkButton coauthor = new HyperLinkButton(coauthorName);
		coauthor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Services.getBrowser().go(new AuthorResult(Arrays.asList(coauthor.getText())));
			}
		});
		return coauthor;
	}

	public String getTitle() {
		if (authorNames.size() == 1) {
			return authorNames.get(0);
		} else {
			return StringUtils.elide(StringUtils.join(", ", authorNames), 20);
		}
	}
	
	private static List<String> getProperName(List<String> names) {
		for (int i = 0; i < names.size(); i++) {
			String newName = toTitleCase(names.get(i));
			names.set(i, newName);			
		}		
		return names;
		
	}
	
	private static String toTitleCase(String s) {
		char[] chars = s.trim().toLowerCase().toCharArray();
		boolean found = false;
	 
		for (int i=0; i<chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i])) {
				found = false;
			}
		}
	 
		return String.valueOf(chars);
	}
}