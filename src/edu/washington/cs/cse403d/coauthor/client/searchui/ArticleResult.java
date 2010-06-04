package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
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
	private DefaultListModel listModel;

	/**
	 * Constructor.
	 * 
	 * @param query
	 *            the article title
	 */
	public ArticleResult(String query) {
		articleTitle = query;
	}

	@Override
	protected void load() {
		try {
			publication = CDSI.getPublication(articleTitle);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, "Um, this isn't really supposed to happen", "Error!",
					JOptionPane.ERROR_MESSAGE);
		}
		initialize();
		// System.out.println(super.getContainerListeners().toString());
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

			// Accesses the clipboard when URL is clicked
			EE.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					StringSelection data = new StringSelection(EE.getText());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data, data);
					showCopyNotice();
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

		// Add list of authors
		buildList();
		authorList = new JList(listModel);
		authorList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int selected = authorList.getSelectedIndex();
				
				String searchFor = ("<html><i>°ÊSearch for this author</i></html>");
				String closeMenu = ("<html><i>°ÊClose this submenu</i></html>");
				if(!authorList.getSelectedValue().equals(closeMenu) &&
						!authorList.getSelectedValue().equals(searchFor)){
					if( selected + 1 == listModel.size() ||
							listModel.getElementAt(selected + 1) != searchFor) {
						selected = authorList.getSelectedIndex();
						listModel.insertElementAt(searchFor, selected + 1);
						listModel.insertElementAt(closeMenu, selected + 2);
						authorList.setModel(listModel);
						authorList.setSelectedIndex(selected);
					}
				}
				
				if(authorList.getSelectedValue().equals(closeMenu)){
					listModel.remove(selected);
					authorList.setSelectedIndex(selected -1);
					listModel.remove(authorList.getSelectedIndex());
					authorList.setModel(listModel);
				}
				
				int subMenuSelection;
				
				if(!authorList.isSelectionEmpty())
					subMenuSelection = authorList.getSelectedIndex();
				else
					subMenuSelection = selected - 2;
				
				String selectedItem = (String) listModel.getElementAt(subMenuSelection);
				
				if (selectedItem.equals(searchFor)) {
					String articleTitle = (String) listModel.getElementAt(subMenuSelection - 1);
					Services.getBrowser().go(new AuthorResult(articleTitle));
				}				
			
				if (evt.getClickCount() == 2) {
					String author = (String) authorList.getSelectedValue();
					Services.getBrowser().go(new AuthorResult(author));
				}
			}
		});
		authorList.setLayoutOrientation(JList.VERTICAL);
		authorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane listScroller = new JScrollPane(authorList);
		listScroller.setPreferredSize(new Dimension(60, 60));
		authorInfo.add(listScroller);
		authorInfo.add(Box.createVerticalStrut(10));

	}

	/**
	 * Internal helper method that builds a JList containing author names
	 */
	private void buildList() {
		listModel = new DefaultListModel();
		List<String> list = publication.getAuthors();
		int i = 0;
		while (i < list.size()) {
			listModel.add(i, list.get(i));
			i++;
		}
	}

	/**
	 * Displays a notice message regarding clipboard interaction
	 */
	private void showCopyNotice() {
		JOptionPane.showMessageDialog(this, "The URL has been copied to the clipboard.", "Notice",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Returns the title of the page
	 */
	public String getTitle() {
		return "Article Search Result";
	}
}