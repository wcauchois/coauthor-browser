package edu.washington.cs.cse403d.coauthor.client.utils;	

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.ResourceManager;

/**
 * Created a filter bar below the coauthor/publication list in
 * the result screen.
 * 
 * @author Kevin Bang
 *
 */
public class FilterPanel extends JPanel {
	private static final long serialVersionUID = -9090832682261141465L;
	private JPanel filterPanel;
	private String theAuthor;
	private JList list;
	
	/**
	 * Constructor
	 * 
	 * @param flag determines whether the filter is for coauthor or publications 
	 * @param defaultListModel listModel of the list to be filtered
	 * @param dataList list of data containing all the items in original list
	 * @param list original JList to be filtered
	 */
	public FilterPanel(JList list, String author) {
		this.list = list;
		this.theAuthor = author;
		createFilterPanel();
		add(filterPanel);
	}

	/**
	 * Create the filter panel
	 */
	private void createFilterPanel() {
		filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
				
		FilterField filterField = new FilterField(list);
		if(theAuthor != null)
			filterField.setPreferredSize(new Dimension(300, 0));
		else
			filterField.setPreferredSize(new Dimension(340, 0));
		filterPanel.add(filterField);
		
		filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
		filterPanel.add(Box.createHorizontalStrut(5));
		
		if(theAuthor != null) {
			filterPanel.add(new VisualizeButton());
			filterPanel.add(Box.createHorizontalStrut(5));
			filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
			filterPanel.add(Box.createHorizontalStrut(5));
		}
		
		if(theAuthor != null)
			filterPanel.add(new HelpMarker(Services.getResourceManager().
				loadStrings("strings.xml").get("CoauthorFilter.help")));
		else
			filterPanel.add(new HelpMarker(Services.getResourceManager().
				loadStrings("strings.xml").get("PublicationFilter.help")));
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	}
	
	private class VisualizeButton extends JLabel {
		private static final long serialVersionUID = 8024005710286098259L;
		private ImageIcon visualizeButton, hoverButton;
		final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
		final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		ResourceManager resourceManager;
		
		public VisualizeButton() {
			resourceManager = Services.getResourceManager();
			this.setToolTipText("Click this button to visualize");
			visualizeButton = resourceManager.loadImageIcon("VisualizeButton.png");
			hoverButton = resourceManager.loadImageIcon("VisualizeButtonHover.png");
			setIcon(visualizeButton);
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					Services.getGraphVizManager().showGraphFor(theAuthor);
				}
				public void mouseEntered(MouseEvent evt) {
					setCursor(handCursor);
					setIcon(hoverButton);
				}
				@Override
				public void mouseExited(MouseEvent evt) {
					setCursor(defaultCursor);
					setIcon(visualizeButton);
				}
				
			});
		}		
	}
}

/*
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
*/