package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.HyperLinkButton;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * Displays search result information about a publication, including a list of
 * co-authors.
 * 
 * Since many articles in the database does not have full publication data such
 * as journal or ISBN, detailed information is given only when such info is
 * available.
 * 
 * @author Kevin Bang
 */
public class ArticleResult extends BrowserPage {
	private static final long serialVersionUID = 5163500245181788114L;

	private CoauthorDataServiceInterface CDSI = Services.getCoauthorDataServiceInterface();
	private String articleTitle;
	private Publication publication;

	// Fields needed to build the article information section
	private JLabel title;
	private JPanel articleInfo;
	private JPanel authorInfo;
	private JPanel contentPane;
	private JList authorList;

	/**
	 * Constructor.
	 * 
	 * @param query
	 *            the article title
	 */
	public ArticleResult(String query) {
		articleTitle = query;
	}
	
	public ArticleResult(Publication pub) {
		publication = pub;
	}

	@Override
	protected void load() throws Exception {
		if(publication == null)
			publication = CDSI.getPublication(articleTitle);
		initialize();
		setLoaded();
	}

	/**
	 * Internal helper method for the constructor
	 */
	private void initialize() {
		// Wrapping around does not work
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(380, 400));

		// Initialize the base container
		contentPane = new JPanel();
		contentPane.setVisible(true);
		contentPane.setLayout(new BorderLayout());

		// Build components
		buildTitle();
		buildAuthorInfo();
		buildArticleInfo();

		// Build Authors Label
		JLabel authorLabel = new JLabel("Authors:");
		Font f = authorLabel.getFont();
		float s = authorLabel.getFont().getSize2D();
		s += 4.0f;
		authorLabel.setFont(f.deriveFont(s));

		// Build top container which includes title and author label
		JPanel topPart = new JPanel();
		topPart.setLayout(new BoxLayout(topPart, BoxLayout.Y_AXIS));
		topPart.add(title);
		topPart.add(Box.createVerticalStrut(10));
		topPart.add(authorLabel);
		topPart.add(new JSeparator(SwingConstants.HORIZONTAL));

		contentPane.add(topPart, BorderLayout.PAGE_START);
		contentPane.add(authorInfo, BorderLayout.CENTER);
		contentPane.add(articleInfo, BorderLayout.PAGE_END);

		add(contentPane);
	}

	/**
	 * Internal helper method which builds the title component of the result
	 * page
	 */
	private void buildTitle() {
		title = new JLabel(publication.getTitle());
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		title.setToolTipText(publication.getTitle());
	}

	/**
	 * Internal helper method which builds article information.
	 */
	private void buildArticleInfo() {
		articleInfo = new JPanel();
		articleInfo.setVisible(true);
		articleInfo.setLayout(new BoxLayout(articleInfo, BoxLayout.Y_AXIS));

		// Source. Will not be displayed if !hasJournal();
		JLabel sourceLabel;
		if (publication.hasJournal())
			sourceLabel = new JLabel("Source:");
		else
			sourceLabel = new JLabel("Year:");
		Font f = sourceLabel.getFont();
		float s = sourceLabel.getFont().getSize2D();
		s += 4.0f;
		sourceLabel.setFont(f.deriveFont(s));
		articleInfo.add(sourceLabel);
		articleInfo.add(new JSeparator(SwingConstants.HORIZONTAL));

		String source;
		if (publication.hasJournal())
			source = publication.getJournal() + ", " + publication.getYear() + ", Vol. " + publication.getVolume()
					+ ", Number " + publication.getNumber() + ", page " + publication.getPages();
		else
			source = publication.getYear().toString();
		JLabel articleSource = new JLabel(source);
		articleInfo.add(articleSource);
		articleSource.setToolTipText(source);
		articleInfo.add(Box.createVerticalStrut(10));

		// ISBN
		JLabel ISBNLabel = new JLabel("ISBN:");
		f = ISBNLabel.getFont();
		s = ISBNLabel.getFont().getSize2D();
		s += 4.0f;
		ISBNLabel.setFont(f.deriveFont(s));
		articleInfo.add(ISBNLabel);
		articleInfo.add(new JSeparator(SwingConstants.HORIZONTAL));

		JLabel ISBN;
		if (publication.hasIsbn())
			ISBN = new JLabel(publication.getIsbn());
		else
			ISBN = new JLabel("This article does not have ISBN");
		articleInfo.add(ISBN);
		articleInfo.add(Box.createVerticalStrut(10));

		// EE
		JLabel EELabel = new JLabel("Electronic Edition:");
		f = EELabel.getFont();
		s = EELabel.getFont().getSize2D();
		s += 4.0f;
		EELabel.setFont(f.deriveFont(s));
		articleInfo.add(EELabel);
		articleInfo.add(new JSeparator(SwingConstants.HORIZONTAL));

		final HyperLinkButton EE;
		if (publication.getEe() == null)
			EE = new HyperLinkButton("This article does not have an electronic edition", false);
		else {
			EE = new HyperLinkButton(publication.getEe());

			// Open the web-browser when URL is clicked
			EE.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						Desktop.getDesktop().browse(URI.create(EE.getText()));
					} catch(IOException e) {
						JOptionPane.showMessageDialog(ArticleResult.this, "Error",
								"An error was encountered while trying to open your web browser.",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
		articleInfo.add(EE);
	}

	/**
	 * Internal helper method which builds author list content panel
	 */
	private void buildAuthorInfo() {
		authorInfo = new JPanel();
		authorInfo.setVisible(true);
		authorInfo.setLayout(new BoxLayout(authorInfo, BoxLayout.Y_AXIS));

		JLabel authorLabel = new JLabel("Authors:");
		Font f = authorLabel.getFont();
		float s = authorLabel.getFont().getSize2D();
		s += 4.0f;
		authorLabel.setFont(f.deriveFont(s));

		authorList = new ListOfAuthors(publication.getAuthors());
		authorList.setLayoutOrientation(JList.VERTICAL);
		authorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane listScroller = new JScrollPane(authorList);
		listScroller.setPreferredSize(new Dimension(60, 50));
		authorInfo.add(listScroller);
		authorInfo.add(Box.createVerticalStrut(10));
	}

	/**
	 * Returns the title of the page
	 */
	public String getTitle() {
		return "Article Search Result";
	}
}