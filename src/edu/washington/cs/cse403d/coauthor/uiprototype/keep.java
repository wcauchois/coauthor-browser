package edu.washington.cs.cse403d.coauthor.uiprototype;	

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
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

import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

	/*
	 * Creates list of articles the author has worked on (as THE author)
	 */
class SingleAuthorSearchArticleResult extends JPanel 
											implements ListSelectionListener {
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	private String theAuthor;
	private JList pubList;
	private List<Publication> publications;
	private DefaultListModel listModel;
	private JTextField filterQuery;
	private JButton filterButton;
	private JButton returnButton;
	private JPanel filterPanel;
	
	public SingleAuthorSearchArticleResult(String author) {
		setLayout(new BorderLayout());
		theAuthor = author;
		initialize();
	}
	
	private void initialize() {
		setVisible(true);
		
		JLabel title = new JLabel("Publications");
		add(title, BorderLayout.PAGE_START);
		
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
		createFilterPanel();
	}
	
	private void buildPubList() {
		buildListHelper();
		pubList.addListSelectionListener(this);
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
	
	
	/*return a new listModel with filtered results
	 * 
	 */
	private DefaultListModel getFilteredResult(String query) {
		DefaultListModel filteredModel = new DefaultListModel();
		CharSequence newQuery = (CharSequence) query.toLowerCase();
		/*
		 * sequence to get the filtered results.
		 */
		int i = 0;
		int j = 0;
		while (i < publications.size()){
			String compare = publications.get(i).getTitle().toLowerCase();
			if (compare.contains(newQuery)) {
				filteredModel.add(j, publications.get(i).getTitle());
				j++;
			}
			i++;
		}
		return filteredModel;
		
	}
	
	
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	private void createFilterPanel() {
		filterPanel = new JPanel();
		filterButton = new JButton("Filter");
		filterButton.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent evt) {
					String query = filterQuery.getText();
					pubList.setModel(getFilteredResult(query));			
				}
		});
		
		returnButton = new JButton("Return");
		returnButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) {
				pubList.setModel(listModel);			
			}
		});
		
		filterQuery = new JTextField(10);		
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));
		
		filterPanel.add(filterQuery);
		filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(filterButton);
		filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
		filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(returnButton);
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		
		add(filterPanel, BorderLayout.PAGE_END);
	}
}