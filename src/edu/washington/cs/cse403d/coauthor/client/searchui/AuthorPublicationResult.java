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
    public AuthorPublicationResult(String author) throws Exception {
    	setLayout(new BorderLayout());
    	theAuthor = author;
    	singleEntryInitialize();
    }
        
    /**
     * constructor for multi-entry author search
     */
    public AuthorPublicationResult(List<String> authors) throws Exception {
    	setLayout(new BorderLayout());
    	theAuthor = authors.get(0);
    	authorList = authors;
    	multiEntryInitialize(); 
    }

    /**
     * Internal helper method for single author search information display
     */
    private void singleEntryInitialize() throws Exception {
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
		publications = CDSI.getPublicationsForAnyAuthor(theAuthor);
    	buildPubList();
    	FilterPanel filterPanel = new FilterPanel(pubList, null);
    	buildNavigator(filterPanel);
    	//Add the filter panel
    	add(filterPanel, BorderLayout.PAGE_END);
    }
        	
    private void buildNavigator(FilterPanel filterPanel) {
    	final JList theList = filterPanel.getList();
    	theList.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent evt) {
    			int selected = theList.getSelectedIndex();
    			
    			String searchFor = ("<html><i>��Search for this article</i></html>");
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
    				String articleTitle = (String) listModel.getElementAt(subMenuSelection - 1);
    				
    				//Remove the submenu before navigating
    				listModel.remove(subMenuSelection);
    				theList.setSelectedIndex(subMenuSelection);
    				listModel.remove(theList.getSelectedIndex());
    				theList.setModel(listModel);
    				
    				Services.getBrowser().go(new ArticleResult(articleTitle));
    				
    			}                               
    			
    			if(evt.getClickCount() == 2) {
    				String author = (String)theList.getSelectedValue();
    				Services.getBrowser().go(new ArticleResult(author));
    			}                                       
    		}
    	});
    	
    }

    /**
     * Internal helper method for multi-entry author search information display
     */
    private void multiEntryInitialize() throws Exception {
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
    	
		publications = CDSI.getPublicationsForAllAuthors(authorArray);
    	if (publications.isEmpty()) {
    		add(new JLabel("There is no collaboration among these individuals")
    		, BorderLayout.PAGE_END);
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
    	int i = 0;                      
    	while (i < publications.size()){
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