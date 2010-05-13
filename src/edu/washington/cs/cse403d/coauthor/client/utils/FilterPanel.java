package edu.washington.cs.cse403d.coauthor.client.utils;	

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

	/*
	 * Creates list of articles the author has worked on (as THE author)
	 * 
	 * TODO: make the article panel work for the multi-author case
	 */
public class FilterPanel extends JPanel implements ListSelectionListener {
	private CoauthorDataServiceInterface CDSI;
	private List<Publication> publications;
	private List<String> coauthors;
	private DefaultListModel listModel;
	private JTextField filterQuery;
	private JButton filterButton;
	private JButton returnButton;
	private JPanel filterPanel;
	private String flag;
	private JList list;
	private String theAuthor;
	private String[] authorArray;
	
	public FilterPanel(String flag, DefaultListModel defaultListModel,
			CoauthorDataServiceInterface CDSI, JList list, String[] authorArray) {
		listModel = defaultListModel;
		theAuthor = authorArray[0];
		this.flag = flag;
		this.CDSI = CDSI;
		this.list = list;
		
		
		if(flag == "Coauthor") {
			try {
				coauthors = CDSI.getCoauthors(theAuthor);
			} catch (RemoteException e) {
				JOptionPane.showMessageDialog(this,
						"Um, this isn't really supposed to happen",
						"Error!",JOptionPane.ERROR_MESSAGE);
			}
		} else {
			try {
				publications = CDSI.getPublicationsForAllAuthors(authorArray);
			} catch (RemoteException e) {
				JOptionPane.showMessageDialog(this,
						"Um, this isn't really supposed to happen",
						"Error!",JOptionPane.ERROR_MESSAGE);
			}
		}		
		createFilterPanel();
		add(filterPanel);
	}
	
	public FilterPanel() {
		setVisible(true);
		createFilterPanel();
		add(filterPanel);
	}
	
	/*
	 *return a new listModel with filtered results 
	 */
	private DefaultListModel getFilteredResult(String query) {
		if (query == null)
			return listModel;
		
		DefaultListModel filteredModel = new DefaultListModel();
		CharSequence newQuery = (CharSequence) query.toLowerCase();
		/*
		 * sequence to get the filtered results.
		 */
		int i = 0;
		int j = 0;
		if (flag == "Pub") {
			while (i < publications.size()){
				String compare = publications.get(i).getTitle().toLowerCase();
				if (compare.contains(newQuery)) {
					filteredModel.add(j, publications.get(i).getTitle());
					j++;
				}
				i++;
			}
		} else {
			while (i < coauthors.size()){
				String compare = coauthors.get(i).toLowerCase();
				if (compare.contains(newQuery)) {
					filteredModel.add(j, coauthors.get(i));
					j++;
				}
				i++;
			}
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
					list.setModel(getFilteredResult(query));			
				}
		});
		
		returnButton = new JButton("Return");
		returnButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) {
				list.setModel(listModel);			
			}
		});
		
		filterQuery = new JTextField(10);
		filterQuery.getDocument().addDocumentListener(new DocumentListener() { 
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				int length = filterQuery.getDocument().getLength();
				String query = "";
				try {
					query = filterQuery.getDocument().getText(0, length);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					System.out.println("String Fetch Failed");
				}
				list.setModel(getFilteredResult(query));
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				int length = filterQuery.getDocument().getLength();
				String query = "";
				try {
					query = filterQuery.getDocument().getText(0, length);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					System.out.println("String Fetch Failed");
				}
				list.setModel(getFilteredResult(query));
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				int length = filterQuery.getDocument().getLength();
				String query = "";
				try {
					query = filterQuery.getDocument().getText(0, length);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					System.out.println("String Fetch Failed");
				}
				list.setModel(getFilteredResult(query));
			}			
		});
		
		
		
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));
		
		filterPanel.add(filterQuery);
		filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(filterButton);
		filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
		filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(returnButton);
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	}
}