package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
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
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;

/**
 * In author search, builds and displays coauthors for a single author
 * 
 * @author Kevin Bang
 * @author Jeff Prouty
 */
class AuthorCoauthorResult extends JPanel {
	private static final long serialVersionUID = 4589385555472884742L;

	private final CoauthorDataServiceInterface CDSI = Services.getCoauthorDataServiceInterface();

	private final String author;
	private DefaultListModel listModel;
	private JList coauthorList;
	private List<String> coauthorNames;

	/**
	 * Constructor for single author search result screen
	 * 
	 * @param author
	 *            the author that was entered as the query
	 */
	public AuthorCoauthorResult(String author) throws Exception {
		setLayout(new BorderLayout());
		setVisible(true);

		this.author = author;

		createPanel();
	}

	/**
	 * Internal helper method for single-author search result
	 */
	private void createPanel() throws Exception {
		// Coauthor list label
		JLabel coauthorsTitle = new JLabel("Coauthors");
		Font f = coauthorsTitle.getFont();
		float s = coauthorsTitle.getFont().getSize2D();
		s += 8.0f;
		coauthorsTitle.setFont(f.deriveFont(s));
		add(coauthorsTitle, BorderLayout.PAGE_START);

		// Coauthor list
		listModel = new DefaultListModel();
		coauthorList = new JList(listModel);
		coauthorNames = CDSI.getCoauthors(author);
		add(buildCoauthorList(), BorderLayout.CENTER);

		FilterPanel filterPanel = new FilterPanel(coauthorList, author);
		add(filterPanel, BorderLayout.PAGE_END);
		buildNavigator(filterPanel);
	}

	private void buildNavigator(FilterPanel filterPanel) {
		final JList theList = filterPanel.getList();
		final JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem menuItem;
		menuItem = new JMenuItem("Search for this author");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Services.getBrowser().go(new AuthorResult((String)theList.getSelectedValue()));
			}
		});
		popupMenu.add(menuItem);
		
		menuItem = new JMenuItem("Add author to search");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				List<String> newAuthorSearch = Arrays.asList(author, (String)theList.getSelectedValue());
				Services.getBrowser().go(new AuthorResult(newAuthorSearch));
			}
		});
		popupMenu.add(menuItem);
		
		theList.addMouseListener(new ListPopupMouseListener(popupMenu));
	}

	/**
	 * Builds the co-author list for single author search result
	 */
	private JScrollPane buildCoauthorList() {
		buildListHelper();
		coauthorList.setLayoutOrientation(JList.VERTICAL);
		coauthorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		coauthorList.setVisibleRowCount(7);
		JScrollPane listScroller = new JScrollPane(coauthorList);
		listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		return listScroller;
	}

	/**
	 * add author names to the JList, single author search case
	 */
	private void buildListHelper() {
		int i = 0;
		while (i < coauthorNames.size()) {
			listModel.add(i, coauthorNames.get(i));
			i++;
		}
	}
}