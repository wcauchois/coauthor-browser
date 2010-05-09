package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
 * TODO: Make changes to the formatting. Helpmarker is not appearing correctly
 * 
 * 			Maybe gridbaglayout?
 * 
 */
/*
	 * This will produce the search result screen with single-entry
	 * 	author search.
	 */
class AuthorSearchCoauthorResult extends JPanel implements ListSelectionListener{
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	private String theAuthor; //the query(author name)
	//private List<String> theAuthorList;
	private List<String> coauthors;
	private DefaultListModel listModel;
	private JList coauthorList;
	
	private JPanel multiEntryTop;
	/*
	 * Constructor
	 */
	public AuthorSearchCoauthorResult(String author) {
		setLayout(new BorderLayout());
		this.theAuthor = author;
		singleEntryInitialize();
	}
	
	/*
	 * If given list of authors
	 */
	public AuthorSearchCoauthorResult(List<String> authorList) {
		setLayout(new BorderLayout());
		this.theAuthor = authorList.get(0);
	//	this.theAuthorList = authorList;
		this.coauthors = authorList;
		multiEntryInitialize();			
	}
	
	private void singleEntryInitialize() {
		setVisible(true);			
		
		//The single entry query. Increase size
		JLabel title = new JLabel(theAuthor);
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		
		
		add(title, BorderLayout.PAGE_START);
		//HelpMarker not appearing right now.
		add(new HelpMarker(
				Services.getResourceManager().
				loadStrings("strings.xml").get("AuthorSearchPane.help")), 
				BorderLayout.PAGE_END);
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
		add(new FilterPanel("Coauthor", listModel, CDSI, coauthorList, theAuthor), BorderLayout.PAGE_END);
		//createFilterPanel();
	}
	
	private void multiEntryInitialize() {
		setVisible(true);			
		multiEntryTopBuild();
		add(multiEntryTop, BorderLayout.PAGE_START);
		//listModel = new DefaultListModel();
		//coauthorList = new JList(listModel);
		//buildCoauthorList();
		//add(new FilterPanel("Coauthor", listModel, CDSI, coauthorList, theAuthorList.get(0)), BorderLayout.PAGE_END);
	
	}	
	
	/*
	 * Builds the co-author list
	 */
	private void buildCoauthorList() {
		if (theAuthor != null)
			buildListHelper();
		else
			buildListHelper2();
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
	
	/*
	 * for multi-entry query
	 */
	private void buildListHelper2() {
		int i = 1;
		while (i < coauthors.size()) {
			listModel.add(i - 1, coauthors.get(i));
			i++;
		}
	}
	
	private void multiEntryTopBuild() {
		multiEntryTop = new JPanel();
		multiEntryTop.setLayout(new GridBagLayout());
		multiEntryTop.setVisible(true);
		multiEntryTop.setPreferredSize(new Dimension(200, 100));
		GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.HORIZONTAL;
		
		//The Author Name.
		JLabel title = new JLabel("Author");
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		multiEntryTop.add(title, c);
		
		
		//JLabel bullet = new JLabel("¡Ü");
		
		final JLabel author = new JLabel(theAuthor);		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		//multiEntryTop.add(bullet, c);
		multiEntryTop.add(author, c);
		author.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				List<String> list = new ArrayList<String>();
				list.add(author.getText());
				Services.getBrowser().go(new AuthorSearchResultsMain(list));
			}
		});
		
		JLabel coauthor = new JLabel("Coauthors");
		f = coauthor.getFont();
		s = coauthor.getFont().getSize2D();
		s += 8.0f;
		coauthor.setFont(f.deriveFont(s));
		c.weighty = 0.1;
		c.gridx = 0;
		c.gridy = 2;
		multiEntryTop.add(coauthor, c);		
		
		addCoauthors(2, c);
	}
	
	private void addCoauthors(int gridy, GridBagConstraints c) {
		int i = 1;
		gridy++;
		//implement an actionListner in here
		while (i < coauthors.size()) {
			final JLabel coauthor = new JLabel(coauthors.get(i));
			c.gridy = gridy;
			multiEntryTop.add(coauthor, c);
			coauthor.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					List<String> list = new ArrayList<String>();
					list.add(coauthor.getText());
					Services.getBrowser().go(new AuthorSearchResultsMain(list));
				}
			});
			gridy++;
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
			Services.getBrowser().go(new AuthorSearchResultsMain(author));
		}		
	}	
}

/*

private DefaultListModel getFilteredResult(String query) {
	DefaultListModel filteredModel = new DefaultListModel();
	CharSequence newQuery = (CharSequence) query.toLowerCase();
	
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
*/