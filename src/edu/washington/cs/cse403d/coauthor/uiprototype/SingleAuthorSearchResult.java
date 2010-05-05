package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/*
 * TODO: implement a filter function in the coauthor list.
 * 		Work on formatting
 * 		 
 */
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
	private JTextField filterQuery;
	private JButton filterButton;
	private JButton returnButton;
	private JPanel filterPanel;
	
	/*
	 * Constructor
	 */
	public SingleAuthorSearchResult(String author) {
		setLayout(new BorderLayout());
		this.theAuthor = author;
		initialize();
	}
	
	private void initialize() {
		setVisible(true);			
		
		//The single entry query
		JLabel title = new JLabel(theAuthor);
		add(title, BorderLayout.PAGE_START);
		
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
		createFilterPanel();
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
		add(listScroller, BorderLayout.CENTER);
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
			String selection = (String) ((DefaultListModel) coauthorList.getModel())
											.get(coauthorList.getSelectedIndex());
			//Make a List, since AuthorSearchResults only takes list
			List<String> author = new ArrayList<String>();
			author.add(selection);
			
			//get the new browser
			Services.getBrowser().go(new AuthorSearchResults(author));
		}			
	}
	
	private DefaultListModel getFilteredResult(String query) {
		DefaultListModel filteredModel = new DefaultListModel();
		CharSequence newQuery = (CharSequence) query.toLowerCase();
		/*
		 * sequence to get the filtered results.
		 */
		int i = 0;
		int j = 0;
		while (i < coauthors.size()){
			String compare = coauthors.get(i).toLowerCase();
			if (compare.contains(newQuery)) {
				filteredModel.add(j, coauthors.get(i));
				j++;
			}
			i++;
		}
		return filteredModel;
	}
	
	private void createFilterPanel() {
		filterPanel = new JPanel();
		filterButton = new JButton("Filter");
		filterButton.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent evt) {
					String query = filterQuery.getText();
					coauthorList.setModel(getFilteredResult(query));			
				}
		});
		
		returnButton = new JButton("Return");
		returnButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) {
				coauthorList.setModel(listModel);			
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