package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.FilterPanel;
import edu.washington.cs.cse403d.coauthor.client.utils.HyperLinkButton;
import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;

public class ChainSearchResult extends BrowserPage {
	private static final long serialVersionUID = 7570009274799150945L;
	
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = Services
		.getCoauthorDataServiceInterface();
	
	private List<PathLink> chain;
	private DefaultListModel listModel;
	private JList chainList;
	
	private JPanel contentPanel;
	
	private String author1;
	private String author2;
	
	/**
	 * Constructor for single author search result screen
	 *
	 * @param author
	 *            the author that was entered as the query
	 */
	public ChainSearchResult(final String author1, final String author2) {
		this.author1 = author1;
		this.author2 = author2;
	}

	@Override
	protected void load() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setVisible(true);
		setLayout(new BorderLayout());
		boolean valid = true;
		try {
			chain = CDSI.getOneShortestPathBetweenAuthors(author1, author2, getAutoscrolls());
		} catch (RemoteException e) {
			System.out.println("Invalid author(s)");
			valid = false;
		}
		if (valid)
			singleEntryInitialize();
		
		setLoaded();
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
	private void singleEntryInitialize() {
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setVisible(true);
		
		// Title
		JLabel title = new JLabel("Results for Chain Search: ");
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 10.0f;
		title.setFont(f.deriveFont(s));
		contentPanel.add(title);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		// Coauthor list label
		JLabel authorChainLabel = new JLabel("Author Chain:");
		f = authorChainLabel.getFont();
		s = authorChainLabel.getFont().getSize2D();
		s += 6.0f;
		authorChainLabel.setFont(f.deriveFont(s));
		contentPanel.add(authorChainLabel);
		
		// Coauthor list
		listModel = new DefaultListModel();
		chainList = new JList(listModel);
		chainList.setFont(chainList.getFont().deriveFont(Font.PLAIN));
		
		buildChain();
		
		JPanel number = new JPanel();
		JLabel numberText = new JLabel("The number of authors in this chain is: ");
		JLabel numberVal = new JLabel("" + listModel.size());
		numberText.setFont(f.deriveFont(s));
		numberVal.setFont(f.deriveFont(s));
		number.add(numberText);
		number.add(numberVal);
		
		FilterPanel filterPanel = new FilterPanel(chainList, author1, author2);
		buildNavigator(filterPanel);
		add(filterPanel);
		add(contentPanel, BorderLayout.PAGE_START);
		add(number, BorderLayout.PAGE_END);
	}

	private void buildNavigator(FilterPanel filterPanel) {
		final JList theList = filterPanel.getList();
		theList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int selected = theList.getSelectedIndex();
				
				String searchFor = ("<html><i>��Search for this author</i></html>");
				String closeMenu = ("<html><i>��Close this submenu</i></html>");
					if(!theList.isSelectionEmpty() && !theList.getSelectedValue().equals(closeMenu) &&
							!theList.getSelectedValue().equals(searchFor)){
						if( selected + 1 == listModel.getSize() ||
								listModel.getElementAt(selected + 1) != searchFor) {
							
							selected = theList.getSelectedIndex();
							listModel.insertElementAt(searchFor, selected + 1);
							listModel.insertElementAt(closeMenu, selected + 2);
							
							theList.setModel(listModel);
							theList.setSelectedIndex(selected);
						}
					}
					
					if(!theList.isSelectionEmpty() && theList.getSelectedValue().equals(closeMenu)){
						listModel.remove(selected);
						theList.setSelectedIndex(selected -1);
						listModel.remove(theList.getSelectedIndex());
						theList.setModel(listModel);
					}
					
					int subMenuSelection;
					String selectedItem;
					if(!theList.isSelectionEmpty()) {
						subMenuSelection = theList.getSelectedIndex();
						selectedItem = (String) listModel.getElementAt(subMenuSelection);
					} else {
						subMenuSelection = selected - 2;
						selectedItem = "";
					}                              
					
					if (selectedItem.equals(searchFor)) {
						String author = (String) listModel.getElementAt(subMenuSelection - 1);
						
						//Remove the submenu before navigating
						listModel.remove(subMenuSelection);
						theList.setSelectedIndex(subMenuSelection);
						listModel.remove(theList.getSelectedIndex());
						theList.setModel(listModel);
						
						Services.getBrowser().go(new AuthorResult(author));                            
					}
					if(evt.getClickCount() == 2) {
						String author = (String)theList.getSelectedValue();
						Services.getBrowser().go(new AuthorResult(author));
					}
			}
		});
	}

	/**
	 * Builds the co-author list for single author search result
	 */
	private void buildChain() {
		buildListHelper();
		chainList.setLayoutOrientation(JList.VERTICAL);
		chainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chainList.setVisibleRowCount(5);
		JScrollPane listScroller = new JScrollPane(chainList);
		listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPanel.add(listScroller);
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
		author2 = chain.get(i - 1).toString().substring(chain.get(i - 1).toString().indexOf('>') + 1);
		this.author1 = (String) listModel.get(0);
	}

	public String getTitle() {
		return author1.trim() + " & " + author2.trim();
	}
}