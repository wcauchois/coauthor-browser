package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.FilterPanel;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * In author search result, builds and displays publication data for the author.
 * 
 * @author Kevin Bang
 */
class AuthorPublicationResult extends JPanel {
	private static final long serialVersionUID = -5845394814423230884L;

	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = Services
			.getCoauthorDataServiceInterface();

	private final List<String> authorList;
	private JList pubList;
	private List<Publication> publications;

	/**
	 * constructor for multi-entry author search
	 */
	public AuthorPublicationResult(List<String> authorList) throws Exception {
		setLayout(new BorderLayout());
		setVisible(true);
		
		this.authorList = authorList;

		Thread.sleep(10000);
		
		initialize();
	}

	/**
	 * Internal helper method for single author search information display
	 */
	private void initialize() throws Exception {
		// Title
		JLabel title = new JLabel(authorList.size() == 1 ? "Publications" : "Collaborations");
		Font f = title.getFont();
		Float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		add(title, BorderLayout.PAGE_START);

		publications = CDSI.getPublicationsForAllAuthors(authorList.toArray(new String[0]));

		if (publications.isEmpty()) {
			add(new JLabel(authorList.size() == 1 ? "There are no publications for this individual"
					: "There is no collaboration among these individuals"), BorderLayout.PAGE_END);
		} else {
			buildPubList();
			FilterPanel filterPanel = new FilterPanel(pubList, null);
			add(filterPanel, BorderLayout.PAGE_END);
		}
	}
	
	/**
	 * Internal helper method that builds a scrollable list of publication
	 */
	private void buildPubList() {
		pubList = new ListOfArticles(publications, 360);
		JScrollPane listScroller = new JScrollPane(pubList);
		listScroller.setPreferredSize(new Dimension(400, 200));
		add(listScroller, BorderLayout.CENTER);
	}
}