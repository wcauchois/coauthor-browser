package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.FilterPanel;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;

public class ChainSearchResult extends JPanel {
	private static final long serialVersionUID = 7570009274799150945L;

	private CoauthorDataServiceInterface CDSI = Services.getCoauthorDataServiceInterface();

	private List<PathLink> chain;
	private DefaultListModel listModel;
	private JList chainList;

	private String author1;
	private String author2;

	/**
	 * Constructor for single author search result screen
	 * 
	 * @param author
	 *            the author that was entered as the query
	 */
	public ChainSearchResult(String author1, String author2) {
		setLayout(new BorderLayout());
		setVisible(true);

		this.author1 = author1;
		this.author2 = author2;

		createChainPanel();
	}

	/**
	 * Constructor for multi-entry author search result screen
	 * 
	 * @param authorList
	 *            list of queries(author names)
	 */
	/**
	 * Internal helper method for single-author search result
	 */
	private void createChainPanel() {
		try {
			chain = CDSI.getOneShortestPathBetweenAuthors(author1, author2, getAutoscrolls());

			JPanel contentPanel = new JPanel();
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
			contentPanel.setVisible(true);

			// Title
			JLabel explanation = new JLabel("These authors have not collaborated yet");
			contentPanel.add(explanation);
			contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

			// Coauthor list label
			JLabel authorChainLabel = new JLabel("Author Chain:");
			Font f = authorChainLabel.getFont();
			float s = authorChainLabel.getFont().getSize2D();
			s += 8.0f;
			authorChainLabel.setFont(f.deriveFont(s));
			contentPanel.add(authorChainLabel);

			// Coauthor list
			listModel = new DefaultListModel();
			chainList = new JList(listModel);
			chainList.setFont(chainList.getFont().deriveFont(Font.PLAIN));

			contentPanel.add(buildChain());

			JPanel number = new JPanel();
			JLabel numberText = new JLabel(author1 + " is ");
			JLabel numberVal = new JLabel("" + (listModel.size()-1));
			JLabel numberText2 = new JLabel(" degrees of ");
			JPanel number2 = new JPanel();
			JLabel numberText3 = new JLabel(" separation away from " + author2);
			numberText.setFont(f.deriveFont(s));
			numberVal.setFont(f.deriveFont(s));
			numberText2.setFont(f.deriveFont(s));
			numberText3.setFont(f.deriveFont(s));
			number.add(numberText);
			number.add(numberVal);
			number.add(numberText2);
			number2.add(numberText3);

			FilterPanel filterPanel = new FilterPanel(chainList, author1, author2);
			buildNavigator(filterPanel);
			add(filterPanel);
			add(contentPanel, BorderLayout.PAGE_START);
			add(number, BorderLayout.CENTER);
			add(number2, BorderLayout.PAGE_END);
		} catch (RemoteException e) {
			System.out.println("Invalid author(s)");
		}
	}

	private void buildNavigator(FilterPanel filterPanel) {
		final JList theList = filterPanel.getList();
		theList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int selected = theList.getSelectedIndex();

				String searchFor = ("<html><i>°ÊSearch for this author</i></html>");
				String closeMenu = ("<html><i>°ÊClose this submenu</i></html>");
				if (!theList.isSelectionEmpty() && !theList.getSelectedValue().equals(closeMenu)
						&& !theList.getSelectedValue().equals(searchFor)) {
					if (selected + 1 == listModel.getSize() || listModel.getElementAt(selected + 1) != searchFor) {

						selected = theList.getSelectedIndex();
						listModel.insertElementAt(searchFor, selected + 1);
						listModel.insertElementAt(closeMenu, selected + 2);

						theList.setModel(listModel);
						theList.setSelectedIndex(selected);
					}
				}

				if (!theList.isSelectionEmpty() && theList.getSelectedValue().equals(closeMenu)) {
					listModel.remove(selected);
					theList.setSelectedIndex(selected - 1);
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
					theList.setModel(listModel);

					Services.getBrowser().go(new AuthorResult(author));
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
	private JScrollPane buildChain() {
		buildListHelper();
		chainList.setLayoutOrientation(JList.VERTICAL);
		chainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chainList.setVisibleRowCount(5);
		JScrollPane listScroller = new JScrollPane(chainList);
		listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		return listScroller;
	}

	/**
	 * add author names to the JList, single author search case
	 */
	private void buildListHelper() {
		int i = 0;
		while (i < chain.size()) {
			listModel.add(i, chain.get(i).toString().substring(0, chain.get(i).toString().indexOf('-')));
			i++;
		}
		listModel.add(i, chain.get(i - 1).toString().substring(chain.get(i - 1).toString().indexOf('>') + 1));
		author1 = listModel.get(0).toString();
		author2 = listModel.get(i).toString();
	}

	public String getTitle() {
		return author1.trim() + " & " + author2.trim();
	}
}