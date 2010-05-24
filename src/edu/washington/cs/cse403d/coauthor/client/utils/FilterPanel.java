package edu.washington.cs.cse403d.coauthor.client.utils;	

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * Created a filter bar below the coauthor/publication list in
 * the result screen.
 * 
 * @author Kevin Bang
 *
 */
public class FilterPanel extends JPanel {
	private static final long serialVersionUID = -9090832682261141465L;
	
	private List<Publication> publications;
	private List<String> coauthors;
	private DefaultListModel listModel;
	
	private boolean filterFlag; //Decides whether to remove the default text or not
	
	private JTextField filterQuery;
	private JPanel filterPanel;
	private String flag;
	private JList list;
	
	/**
	 * Constructor
	 * 
	 * @param flag determines whether the filter is for coauthor or publications 
	 * @param defaultListModel listModel of the list to be filtered
	 * @param dataList list of data containing all the items in original list
	 * @param list original JList to be filtered
	 */
	@SuppressWarnings("unchecked")
	public FilterPanel(String flag, DefaultListModel defaultListModel,
			List dataList, JList list) {
		
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
	
	/**
	 *return a new listModel with filtered results 
	 */
	private DefaultListModel getFilteredResult(String query) {
		if (query == null)
			return listModel;
		
		DefaultListModel filteredModel = new DefaultListModel();
		CharSequence newQuery = (CharSequence) query.toLowerCase();
		
		//sequence to get the filtered results.		
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
	
	/**
	 * Create the filter panel
	 */
	private void createFilterPanel() {
		filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
		
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
		filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
		filterPanel.add(Box.createHorizontalStrut(5));
		if(flag == "Coauthor")
			filterPanel.add(new HelpMarker(Services.getResourceManager().
				loadStrings("strings.xml").get("CoauthorFilter.help")));
		else
			filterPanel.add(new HelpMarker(Services.getResourceManager().
				loadStrings("strings.xml").get("PublicationFilter.help")));
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	}
}