package edu.washington.cs.cse403d.coauthor.client.searchui;	

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

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

	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	private String theAuthor;
	private List<String> authorList;
	private JList pubList;
	private List<Publication> publications;
	private DefaultListModel listModel;
	
	/**
	 * Constructor for a single author search
	 * 
	 * @param author the author name that was passed as the query
	 */
	public AuthorPublicationResult(String author) {
		setLayout(new BorderLayout());
		theAuthor = author;
		singleEntryInitialize();
	}
	
	/**
	 * constructor for multi-entry author search
	 */
	public AuthorPublicationResult(List<String> authors) {
		setLayout(new BorderLayout());
		theAuthor = authors.get(0);
		authorList = authors;
		multiEntryInitialize();	
	}

	/**
	 * Internal helper method for single author search information display
	 */
	private void singleEntryInitialize() {
		setVisible(true);
		
		//Title
		JLabel title = new JLabel("Publications");
		Font f = title.getFont();
		Float s = title.getFont().getSize2D();
		s += 6.0f;
		title.setFont(f.deriveFont(s));
		add(title, BorderLayout.PAGE_START);
		
		//Build the list of publication
		listModel = new DefaultListModel();
		pubList = new JList(listModel);		
		try {
			publications = CDSI.getPublicationsForAnyAuthor(theAuthor);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Um, this isn't really supposed to happen",
					"Error!",JOptionPane.ERROR_MESSAGE);
		}
		buildPubList(listModel);
		
		//Add the filter panel
		add(new FilterPanel(pubList, null), BorderLayout.PAGE_END);
	}
	
	/**
	 * Internal helper method for multi-entry author search information display
	 */
	private void multiEntryInitialize() {
		setVisible(true);
		
		//Title
		JLabel title = new JLabel("Collaborations");
		Font f = title.getFont();
		Float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		add(title, BorderLayout.PAGE_START);
		
		//Build the list of collaboration
		listModel = new DefaultListModel();
		pubList = new JList(listModel);
		
		//Build the list
		int size = authorList.size();
		String[] authorArray = new String[size];
		for (int i = 0; i < size; i++) {
			authorArray[i] = authorList.get(i);
		}
		
		try {
			publications = CDSI.getPublicationsForAllAuthors(authorArray);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Um, this isn't really supposed to happen",
					"Error!",JOptionPane.ERROR_MESSAGE);
		}
		if (publications.isEmpty()) {
			add(new JLabel("There is no collaboration among these individuals")
			, BorderLayout.PAGE_END);
		} else {
			buildPubList(listModel);
			add(new FilterPanel(pubList, null), BorderLayout.PAGE_END);
		}
	}	
	
	/**
	 * Internal helper method that builds a scrollable list of publication 
	 */
	private void buildPubList(final DefaultListModel listModel) {
		int i = 0;			
		while (i < publications.size()){
			listModel.add(i, publications.get(i).getTitle());
				i++;
		}
		
		pubList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int selected = pubList.getSelectedIndex();
				
				String searchFor = ("<html><i>°ÊSearch for this article</i></html>");
				String closeMenu = ("<html><i>°ÊClose this submenu</i></html>");
				if(!pubList.getSelectedValue().equals(closeMenu) &&
						!pubList.getSelectedValue().equals(searchFor)){
					if( selected + 1 == listModel.size() ||
							listModel.getElementAt(selected + 1) != searchFor) {
						selected = pubList.getSelectedIndex();
						listModel.insertElementAt(searchFor, selected + 1);
						listModel.insertElementAt(closeMenu, selected + 2);
						pubList.setModel(listModel);
						pubList.setSelectedIndex(selected);
					}
				}
				
				if(pubList.getSelectedValue().equals(closeMenu)){
					listModel.remove(selected);
					pubList.setSelectedIndex(selected -1);
					listModel.remove(pubList.getSelectedIndex());
					pubList.setModel(listModel);
				}
				
				int subMenuSelection;
				
				if(!pubList.isSelectionEmpty())
					subMenuSelection = pubList.getSelectedIndex();
				else
					subMenuSelection = selected - 2;
				
				String selectedItem = (String) listModel.getElementAt(subMenuSelection);
				
				if (selectedItem.equals(searchFor)) {
					String articleTitle = (String) listModel.getElementAt(subMenuSelection - 1);
					Services.getBrowser().go(new ArticleResult(articleTitle));
				}				
				
				if(evt.getClickCount() == 2) {
					String article = (String)pubList.getSelectedValue();
					Services.getBrowser().go(new ArticleResult(article));
				}
			}
		});
		pubList.setLayoutOrientation(JList.VERTICAL);
		pubList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);			
		JScrollPane listScroller = new JScrollPane(pubList);
		listScroller.setPreferredSize(new Dimension(400, 120));
		add(listScroller, BorderLayout.CENTER);			
	}
}