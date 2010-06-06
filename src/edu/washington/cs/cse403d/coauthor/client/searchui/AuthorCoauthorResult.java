package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.FilterPanel;
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
		theList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int selected = theList.getSelectedIndex();

				String searchFor = ("<html><i>→Search for this author</i></html>");
				String coauthorSearchFor = ("<html><i>→Perform coauthor search on this author</i></html>");
				String closeMenu = ("<html><i>→Close this submenu</i></html>");
				if (!theList.isSelectionEmpty() && !theList.getSelectedValue().equals(closeMenu)
						&& !theList.getSelectedValue().equals(searchFor)
						&& !theList.getSelectedValue().equals(coauthorSearchFor)) {
					if (selected + 1 == listModel.getSize() || listModel.getElementAt(selected + 1) != searchFor) {

						selected = theList.getSelectedIndex();
						listModel.insertElementAt(searchFor, selected + 1);
						listModel.insertElementAt(coauthorSearchFor, selected + 2);
						listModel.insertElementAt(closeMenu, selected + 3);

						theList.setModel(listModel);
						theList.setSelectedIndex(selected);
					}
				}

				if (!theList.isSelectionEmpty() && theList.getSelectedValue().equals(closeMenu)) {
					listModel.remove(selected);
					theList.setSelectedIndex(selected - 1);
					listModel.remove(theList.getSelectedIndex());
					theList.setSelectedIndex(selected - 2);
					listModel.remove(theList.getSelectedIndex());
					theList.setModel(listModel);
				}

				int subMenuSelection;
				String selectedItem;
				if (!theList.isSelectionEmpty()) {
					subMenuSelection = theList.getSelectedIndex();
					selectedItem = (String) listModel.getElementAt(subMenuSelection);
				} else {
					subMenuSelection = selected - 2;
					selectedItem = "";
				}

				if (selectedItem.equals(searchFor)) {
					String author = (String) listModel.getElementAt(subMenuSelection - 1);

					// Remove the submenu before navigating
					listModel.remove(subMenuSelection);
					theList.setSelectedIndex(subMenuSelection);
					listModel.remove(theList.getSelectedIndex());
					theList.setSelectedIndex(subMenuSelection);
					listModel.remove(theList.getSelectedIndex());
					theList.setModel(listModel);

					Services.getBrowser().go(new AuthorResult(author));

				} else if (selectedItem.equals(coauthorSearchFor)) {
					List<String> selectedList = new ArrayList<String>(2);
					selectedList.add(author);
					selectedList.add((String) listModel.getElementAt(subMenuSelection - 2));

					// Remove the submenu before navigating
					subMenuSelection--;
					listModel.remove(subMenuSelection);
					theList.setSelectedIndex(subMenuSelection);
					listModel.remove(theList.getSelectedIndex());
					theList.setSelectedIndex(subMenuSelection);
					listModel.remove(theList.getSelectedIndex());

					Services.getBrowser().go(new AuthorResult(selectedList));
				}

				if (evt.getClickCount() == 2) {
					String author = (String) theList.getSelectedValue();
					Services.getBrowser().go(new AuthorResult(author));
				}
			}
		});
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