package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

public class AuthorSearchResults extends BrowserPage {
	private List<String> queries;
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	public AuthorSearchResults(List<String> queries) {
		this.queries = queries;
		System.out.println("Moved to search pane");
		this.setVisible(true);
		if(queries.size() == 1) {
			add(new SingleAuthorSearchResult(queries.get(0)));
			add(new SingleAuthorSearchArticleResult(queries.get(0)));
		} else
			add(new SingleAuthorSearchResult(queries.get(0)));
	}
	
	public String getTitle() {
		return "Results for \"" + queries.get(0) + "\"";
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return new Class[] { StartPage.class };
	}
	
	private List<String> createList(String str){
		List<String> authors = new ArrayList<String>();
		authors.add(str);
		return authors;
	}
	
	
	

	/*
	 * Creates list of articles the author has worked on (as THE author)
	 */
	private class SingleAuthorSearchArticleResult extends JPanel 
												implements ListSelectionListener {
		private String theAuthor;
		private JList pubList;
		private List<Publication> publications;
		private DefaultListModel listModel;
		
		public SingleAuthorSearchArticleResult(String author) {
			theAuthor = author;
			initialize();
		}
		
		private void initialize() {
			setVisible(true);

			listModel = new DefaultListModel();
			pubList = new JList(listModel);
			try {
				publications = CDSI.getPublicationsForAnyAuthor(theAuthor);
			} catch (RemoteException e) {
				JOptionPane.showMessageDialog(this,
						"Um, this isn't really supposed to happen",
						"Error!",JOptionPane.ERROR_MESSAGE);
			}
			buildPubList();
			
		}
		
		private void buildPubList() {
			int i = 0;			
			while (i < publications.size()){
				listModel.add(i, publications.get(i).getTitle());
				i++;
			}
			pubList.addListSelectionListener(this);
			pubList.setLayoutOrientation(JList.VERTICAL);
			pubList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);			
			JScrollPane listScroller = new JScrollPane(pubList);
			listScroller.setPreferredSize(new Dimension(230, 80));
			add(listScroller);			
		}
		
		public void valueChanged(ListSelectionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	/*
	 * This will produce the search result screen with single-entry
	 * 	author search.
	 */
	private class SingleAuthorSearchResult extends JPanel implements ListSelectionListener{
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
		 * add author names to the list
		 */
		private void buildListHelper() {
			int i = 0;			
			while (i < coauthors.size()){
				listModel.add(i, coauthors.get(i));
				i++;
			}
		}
		
		/*
		 * Testing purpose. Will be removed later
		 */
		private void printTest() {
			int capacity = coauthors.size();
			int i = 0;
			while (i < capacity) {
				System.out.println(coauthors.get(i));
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
	
	
	
	
}