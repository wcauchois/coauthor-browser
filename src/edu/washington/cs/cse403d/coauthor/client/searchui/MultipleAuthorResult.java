package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.HyperLinkButton;
import edu.washington.cs.cse403d.coauthor.client.utils.FilterPanel;
import edu.washington.cs.cse403d.coauthor.client.utils.HelpMarker;


/**
 * Displays search result information about multiple authors.
 * @author Kevin Bang
 */
public class MultipleAuthorResult extends JPanel {
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	private String theAuthor; //the query(author name)
	private List<String> theAuthorList;
	//private List<String> coauthors;
	private DefaultListModel listModel;
	private JList coauthorList;
	
	private JPanel multiEntryTop;
	/*
	 * Constructor
	 */
	public MultipleAuthorResult(String author) {
		setLayout(new BorderLayout());
		this.theAuthor = author;
		singleEntryInitialize();
	}
	
	/*
	 * If given list of authors
	 */
	public MultipleAuthorResult(List<String> authorList) {
		setLayout(new BorderLayout());
		this.theAuthor = authorList.get(0);
		this.theAuthorList = authorList;
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
		
		
		String[] authorArray = new String[1];
		authorArray[0] = theAuthor; 
		try {
			theAuthorList = CDSI.getCoauthors(theAuthor);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Um, this isn't really supposed to happen",
					"Error!",JOptionPane.ERROR_MESSAGE);
		}			
		buildCoauthorList();
		add(new FilterPanel("Coauthor", listModel, theAuthorList
				, coauthorList, authorArray), BorderLayout.PAGE_END);
	}
	
	private void multiEntryInitialize() {
		setVisible(true);			
		multiEntryTopBuild();
		add(multiEntryTop, BorderLayout.PAGE_START);
	}	
	
	/**
	 * Builds the co-author list
	 */
	private void buildCoauthorList() {
		if (theAuthor != null)
			buildListHelper();
		else
			buildListHelper2();
		//coauthorList.addListSelectionListener(this);
		coauthorList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					String coauthor = (String)coauthorList.getSelectedValue();
					Services.getBrowser().go(new AuthorResult(coauthor));
				}
			}
		});
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
		while (i < theAuthorList.size()){
			listModel.add(i, theAuthorList.get(i));
			i++;
		}
	}
	
	/*
	 * for multi-entry query
	 */
	private void buildListHelper2() {
		int i = 1;
		while (i < theAuthorList.size()) {
			listModel.add(i - 1, theAuthorList.get(i));
			i++;
		}
	}
	
	private void multiEntryTopBuild() {
		multiEntryTop = new JPanel();
		multiEntryTop.setLayout(new BoxLayout(multiEntryTop, BoxLayout.Y_AXIS));
		multiEntryTop.setVisible(true);
		
		//The Author Name.
		JLabel title = new JLabel("Author");
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		multiEntryTop.add(title);
		
		multiEntryTop.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		final HyperLinkButton author = new HyperLinkButton(theAuthor);		
		multiEntryTop.add(author);		
		multiEntryTop.add(Box.createVerticalStrut(10));
		
		JLabel coauthor = new JLabel("Coauthors");
		f = coauthor.getFont();
		s = coauthor.getFont().getSize2D();
		s += 8.0f;
		coauthor.setFont(f.deriveFont(s));
		multiEntryTop.add(coauthor);
		multiEntryTop.add(new JSeparator(SwingConstants.HORIZONTAL));
				
		addCoauthors();
		multiEntryTop.add(Box.createVerticalStrut(10));
	}
	
	private void addCoauthors() {
		int i = 1;
	
		while (i < theAuthorList.size()) {
			final HyperLinkButton coauthor = new HyperLinkButton(theAuthorList.get(i));
			multiEntryTop.add(coauthor);
			coauthor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					List<String> list = new ArrayList<String>();
					list.add(coauthor.getText());
					Services.getBrowser().go(new AuthorResult(list));
				}
			});
			multiEntryTop.add(Box.createVerticalStrut(3));
			i++;
		}
	}
}