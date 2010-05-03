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

public class AuthorSearchResults extends BrowserPage {
	private List<String> queries;
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	/*
	 * This will produce the search result screen with single-entry
	 * 	author search.
	 */
	private class SingleAuthorSearchResult extends JPanel implements ListSelectionListener{
		private String theAuthor;
		private List<String> coauthors;
		private DefaultListModel listModel;
		private JList coauthorList;
		
		public SingleAuthorSearchResult(String author) {
			this.theAuthor = author;
			initialize();
			printTest();
		}
		
		private void initialize() {
			setVisible(true);			
			JLabel author = new JLabel(theAuthor);
			author.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					Services.getBrowser().go(new AuthorSearchResults(createList(theAuthor)));
				}
			});
			add(author);
			
			listModel = new DefaultListModel();
			coauthorList = new JList(listModel);
			try {
				coauthors = CDSI.getCoauthors(theAuthor);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this,
						"Um, this isn't really supposed to happen",
						"Error!",JOptionPane.ERROR_MESSAGE);
			}
			
			buildList();
		}
		
		
		private void buildList() {
			buildListHelper();
			coauthorList.addListSelectionListener(this);
			coauthorList.setLayoutOrientation(JList.VERTICAL);
			coauthorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);			
			JScrollPane listScroller = new JScrollPane(coauthorList);
			listScroller.setPreferredSize(new Dimension(230, 80));
			add(listScroller);
		}
		
		private void buildListHelper() {
			int i = 0;			
			while (i < coauthors.size()){
				listModel.add(i, coauthors.get(i));
				i++;
			}
		}
		
		private void printTest() {
			int capacity = coauthors.size();
			int i = 0;
			while (i < capacity) {
				System.out.println(coauthors.get(i));
				i++;
			}
		}
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				String selection = (String) listModel.get(coauthorList.getSelectedIndex());
				List<String> author = new ArrayList<String>();
				author.add(selection);
				Services.getBrowser().go(new AuthorSearchResults(author));
			}			
		}
	}
	
	
	
	public AuthorSearchResults(List<String> queries) {
		this.queries = queries;
		System.out.println("Moved to search pane");
		this.setVisible(true);
		if(queries.size() == 1)
			add(new SingleAuthorSearchResult(queries.get(0)));
		else
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
}