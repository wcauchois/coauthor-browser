package edu.washington.cs.cse403d.coauthor.client.utils;	

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

	/*
	 * Creates list of articles the author has worked on (as THE author)
	 * 
	 * TODO: make the article panel work for the multi-author case
	 */

public class FilterPanel extends JPanel {
	private List<Publication> publications;
	private List<String> coauthors;
	private DefaultListModel listModel;
	private boolean filterFlag;
	
	private JTextField filterQuery;
	private JButton filterButton;
	private JButton returnButton;
	private JPanel filterPanel;
	private String flag;
	private JList list;
	
	@SuppressWarnings("unchecked")
	public FilterPanel(String flag, DefaultListModel defaultListModel,
			List dataList, JList list, String[] authorArray) {
		
		filterFlag = false;
		listModel = defaultListModel;
		this.flag = flag;
		this.list = list;
		
		
		if(flag == "Coauthor")
			coauthors = dataList;			
		else
			publications = dataList;		
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
	
	private void createFilterPanel() {
		filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
		/*
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
		});*/
		
		filterQuery = new JTextField(30);
		
		//Default message
		String filterHelp = "Type query here to filter the result";		
		filterQuery.setText(filterHelp);
		
		filterQuery.addMouseListener(new MouseAdapter () {
			public void mouseClicked(MouseEvent evt) {
				filterQuery.setText(null);
				filterFlag = true;
			}
		});
		
		filterQuery.getDocument().addDocumentListener(new DocumentListener() { 
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				int length = filterQuery.getDocument().getLength();
				String query = "";
				if (filterFlag) {
					try {
						query = filterQuery.getDocument().getText(0, length);
					} catch (BadLocationException e) {
						System.out.println("String Fetch Failed");
					}
					list.setModel(getFilteredResult(query));
				}
				}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				int length = filterQuery.getDocument().getLength();
				String query = "";
				if (filterFlag) {
					try {
						query = filterQuery.getDocument().getText(0, length);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						System.out.println("String Fetch Failed");
					}
					list.setModel(getFilteredResult(query));
				}
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				int length = filterQuery.getDocument().getLength();
				String query = "";
				if (filterFlag) {
					try {
						query = filterQuery.getDocument().getText(0, length);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						System.out.println("String Fetch Failed");
					}
					list.setModel(getFilteredResult(query));
				}		
			}
		});
		
		
		
		
		filterPanel.add(filterQuery);
		filterPanel.add(Box.createHorizontalStrut(5));
		//filterPanel.add(filterButton);
		//filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
		filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(new HelpMarker(Services.getResourceManager().
				loadStrings("strings.xml").get("Filter.help")));
		//filterPanel.add(returnButton);
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	}
}