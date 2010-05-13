package edu.washington.cs.cse403d.coauthor.client.searchui;	

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.FilterPanel;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

	/*
	 * Creates list of articles the author has worked on (as THE author)
	 * 
	 * TODO: work on the formatting of the filter panel instance(not aligned properly)
	 */
class AuthorSearchArticleResult extends JPanel {
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	private String theAuthor;
	private List<String> authorList;
	private JList pubList;
	private List<Publication> publications;
	private DefaultListModel listModel;
	private JTextField filterQuery;
	private JButton filterButton;
	private JButton returnButton;
	private JPanel filterPanel;
	//private String[] 
	
	public AuthorSearchArticleResult(String author) {
		setLayout(new BorderLayout());
		theAuthor = author;
		singleEntryInitialize();
	}
	
	/*for multi-entry search 
	 * 
	 */
	public AuthorSearchArticleResult(List<String> authors) {
		setLayout(new BorderLayout());
		theAuthor = authors.get(0);
		authorList = authors;
		multiEntryInitialize();	
	}

	private void singleEntryInitialize() {
		setVisible(true);
		
		//TODO: create BoxLayout that contains Publication, 
		//		help marker, and JSeperator.
		//		Do this for every header
		JLabel title = new JLabel("Publications");
		Font f = title.getFont();
		Float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		add(title, BorderLayout.PAGE_START);
		
		listModel = new DefaultListModel();
		pubList = new JList(listModel);
		
		String[] authorArray = new String[1];
		authorArray[0] = theAuthor;
		
		try {
			publications = CDSI.getPublicationsForAnyAuthor(theAuthor);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Um, this isn't really supposed to happen",
					"Error!",JOptionPane.ERROR_MESSAGE);
		}
		buildPubList();
		add(new FilterPanel("Pub", listModel, CDSI, pubList, authorArray), BorderLayout.PAGE_END);
	}
	
	private void multiEntryInitialize() {
		setVisible(true);
		
		JLabel title = new JLabel("Collaborations");
		Font f = title.getFont();
		Float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		add(title, BorderLayout.PAGE_START);
		
		listModel = new DefaultListModel();
		pubList = new JList(listModel);
		
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
			add(new JLabel("There is no collaboration among these individuals"), BorderLayout.PAGE_END);
		} else {
			buildPubList();
			add(new FilterPanel("Pub", listModel, CDSI, pubList, authorArray), BorderLayout.PAGE_END);
		}		
	}
	
	
	private void buildPubList() {
		buildListHelper();
		pubList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					String article = (String)pubList.getSelectedValue();
					Services.getBrowser().go(new ArticleSearchResults(article));
				}
			}
		});
		pubList.setLayoutOrientation(JList.VERTICAL);
		pubList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);			
		JScrollPane listScroller = new JScrollPane(pubList);
		listScroller.setPreferredSize(new Dimension(400, 120));
		add(listScroller, BorderLayout.CENTER);			
	}

	private void buildListHelper() {
		int i = 0;			
		while (i < publications.size()){
			listModel.add(i, publications.get(i).getTitle());
				i++;
			}
	}
	/*
	public void valueChanged(ListSelectionEvent evt) {
		if (!evt.getValueIsAdjusting()) {
			String selection = (String) ((DefaultListModel) pubList.getModel())
											.get(pubList.getSelectedIndex());
			
			//get the new browser
			Services.getBrowser().go(new ArticleSearchResults(selection));
		}		
	}	*/
}