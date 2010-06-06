package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.FilterPanel;
import edu.washington.cs.cse403d.coauthor.client.utils.ListPopupMouseListener;
import edu.washington.cs.cse403d.coauthor.client.utils.StringUtils;
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
	private DefaultListModel listModel;

	/**
	 * constructor for multi-entry author search
	 */
	public AuthorPublicationResult(List<String> authorList) throws Exception {
		setLayout(new BorderLayout());
		setVisible(true);
		
		this.authorList = authorList;
		
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
		s += 6.0f;
		title.setFont(f.deriveFont(s));
		add(title, BorderLayout.PAGE_START);

		// Build the list of publication
		listModel = new DefaultListModel();
		pubList = new JList(listModel);

		publications = CDSI.getPublicationsForAllAuthors(authorList.toArray(new String[0]));

		if (publications.isEmpty()) {
			add(new JLabel(authorList.size() == 1 ? "There are no publications for this individual"
					: "There is no collaboration among these individuals"), BorderLayout.PAGE_END);
		} else {
			buildPubList();
			FilterPanel filterPanel = new FilterPanel(pubList, null);
			buildNavigator(filterPanel);
			// Add the filter panel
			add(filterPanel, BorderLayout.PAGE_END);
		}
	}

	private void buildNavigator(FilterPanel filterPanel) {
		final JList theList = filterPanel.getList();
		final JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem menuItem;
		menuItem = new JMenuItem("Search for this publication");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Services.getBrowser().go(new ArticleResult((String)theList.getSelectedValue()));
			}
		});
		popupMenu.add(menuItem);
		
		menuItem = new JMenuItem("Copy to clipboard");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				StringUtils.copyToClipboard(theList.getSelectedValue());
			}
		});
		popupMenu.add(menuItem);
		
		theList.addMouseListener(new ListPopupMouseListener(popupMenu));
	}

	/**
	 * Internal helper method that builds a scrollable list of publication
	 */
	private void buildPubList() {
		int i = 0;
		while (i < publications.size()) {
			listModel.add(i, publications.get(i).getTitle());
			i++;
		}
		pubList.setLayoutOrientation(JList.VERTICAL);
		pubList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pubList.setVisibleRowCount(6);
		JScrollPane listScroller = new JScrollPane(pubList);
		listScroller.setPreferredSize(new Dimension(400, 140));
		add(listScroller, BorderLayout.CENTER);
	}
}