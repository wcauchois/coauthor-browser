package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Dimension;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/*
	 * This will produce the search result screen with single-entry
	 * 	author search.
	 */
class SingleAuthorSearchResult extends JPanel implements ListSelectionListener{
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	private String theAuthor; //the query(author name)
	private List<String> coauthors;
	private DefaultListModel listModel;
	private JList coauthorList;
	
	/*
	 * Constructor
	 */
	public SingleAuthorSearchResult(String author) {
		this.theAuthor = author;
		initialize();
	}
	
	private void initialize() {
		setVisible(true);			
		
		//The single entry query
		JLabel author = new JLabel(theAuthor);
		add(author);
		
		//Set up the co-author list
		listModel = new DefaultListModel();
		coauthorList = new JList(listModel);
		try {
			coauthors = CDSI.getCoauthors(theAuthor);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Um, this isn't really supposed to happen",
					"Error!",JOptionPane.ERROR_MESSAGE);
		}			
		buildCoauthorList();
	}
	
	/*
	 * Builds the co-author list
	 */
	private void buildCoauthorList() {
		buildListHelper();
		coauthorList.addListSelectionListener(this);
		coauthorList.setLayoutOrientation(JList.VERTICAL);
		coauthorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);			
		JScrollPane listScroller = new JScrollPane(coauthorList);
		listScroller.setPreferredSize(new Dimension(230, 80));
		add(listScroller);
	}
	/*
	 * add author names to the JList
	 */
	private void buildListHelper() {
		int i = 0;			
		while (i < coauthors.size()){
			listModel.add(i, coauthors.get(i));
			i++;
		}
	}
		public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			//Need to make the selection as a List, since the method provided
			//in CoauthorDataServiceInterface.java only takes List<String>
			String selection = (String) listModel.get(coauthorList.getSelectedIndex());
			List<String> author = new ArrayList<String>();
			author.add(selection);
			
			//get the new browser
			Services.getBrowser().go(new AuthorSearchResults(author));
		}			
	}
}