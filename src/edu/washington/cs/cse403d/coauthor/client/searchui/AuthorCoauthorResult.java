package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Component;
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
import edu.washington.cs.cse403d.coauthor.client.utils.FilterPanel;
import edu.washington.cs.cse403d.coauthor.client.utils.HyperLinkButton;


/**
 * In author search, builds and displays coauthor data of the author
 * 
 * @author Kevin Bang
 */
class AuthorCoauthorResult extends JPanel {
	private static final long serialVersionUID = 4589385555472884742L;

	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	private String theAuthor;
	private List<String> theAuthorList;
	private DefaultListModel listModel;
	private JList coauthorList;
	
	private JPanel singleEntryTop;
	private JPanel multiEntryTop;
	
	/**
	 * Constructor for single author search result screen
	 * 
	 * @param author the author that was entered as the query
	 */
	public AuthorCoauthorResult(String author) {
		setVisible(true);
		setLayout(new BorderLayout());
		List<String> n = null;
		try {
			n = CDSI.getAuthors(author);
		} catch (Exception e1) {
			// TODO Go to the error page
			JOptionPane.showMessageDialog(this,
					"Invalid Author Name!",
					"Error!",JOptionPane.ERROR_MESSAGE);
		}
		if (n.size() > 0)
			this.theAuthor = n.get(0);
		else
			JOptionPane.showMessageDialog(this,
					"Invalid Author Name!",
					"Error!",JOptionPane.ERROR_MESSAGE);		
		singleEntryInitialize();
	}
	
	/**
	 * Constructor for multi-entry author search result screen
	 * 
	 * @param authorList list of queries(author names)
	 */
	public AuthorCoauthorResult(List<String> authorList) {
		setVisible(true);
		setLayout(new BorderLayout());
		this.theAuthor = authorList.get(0);
		this.theAuthorList = authorList;
		multiEntryInitialize();			
	}
	
	/**
	 * Internal helper method for single-author search result
	 */
	private void singleEntryInitialize() {
		singleEntryTop = new JPanel();
		singleEntryTop.setLayout(new BoxLayout(singleEntryTop, BoxLayout.Y_AXIS));
		singleEntryTop.setVisible(true);	
	
		//Title
		JLabel title = new JLabel("Results for: " + theAuthor);
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 10.0f;
		title.setFont(f.deriveFont(s));
		singleEntryTop.add(title);
		singleEntryTop.add(Box.createRigidArea(new Dimension(0, 5)));
		
		//Coauthor list label
		JLabel coauthors = new JLabel("Coauthors");
		f = coauthors.getFont();
		s = coauthors.getFont().getSize2D();
		s += 6.0f;
		coauthors.setFont(f.deriveFont(s));
		singleEntryTop.add(coauthors);
		
		//Coauthor list
		listModel = new DefaultListModel();
		coauthorList = new JList(listModel);
		try {
			theAuthorList = CDSI.getCoauthors(theAuthor);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Um, this isn't really supposed to happen",
					"Error!",JOptionPane.ERROR_MESSAGE);
		}			
		buildCoauthorList(listModel);
		add(new FilterPanel(coauthorList, theAuthor));
		add(singleEntryTop, BorderLayout.PAGE_START);
	}
	
	/**
	 * Internal helper method for multi-entry coauthor search result
	 */
	private void multiEntryInitialize() {
		multiEntryTopBuild();
		add(multiEntryTop, BorderLayout.PAGE_START);
	}	
	
	/**
	 * Internal helper method for multiEntryInitialize()
	 */
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
		
		HyperLinkButton author = new HyperLinkButton(theAuthor);
		author.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Services.getBrowser().go(new AuthorResult(theAuthor));				
			}
		});
		multiEntryTop.add(author);
		multiEntryTop.add(Box.createVerticalStrut(10));
		
		//Coauthors
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
	
	/**
	 * Builds the co-author list for single author search result
	 */
	private void buildCoauthorList(final DefaultListModel listModel) {
		if (theAuthor != null)
			buildListHelper();
		else
			buildListHelper2();
		coauthorList.addMouseListener(new MouseAdapter() {
			
			
			public void mouseClicked(MouseEvent evt) {
				int selected = coauthorList.getSelectedIndex();
				
				String searchFor = ("<html><i>→Search for this author</i></html>");
				String coauthorSearchFor = ("<html><i>→Perform coauthor search on this author</i></html>");
				String closeMenu = ("<html><i>→Close this submenu</i></html>");
				if(!coauthorList.getSelectedValue().equals(closeMenu) &&
						!coauthorList.getSelectedValue().equals(coauthorSearchFor) &&
						!coauthorList.getSelectedValue().equals(searchFor)){
					if( selected + 1 == listModel.size() ||
							listModel.getElementAt(selected + 1) != searchFor) {
						selected = coauthorList.getSelectedIndex();
						listModel.insertElementAt(searchFor, selected + 1);
						listModel.insertElementAt(coauthorSearchFor, selected + 2);
						listModel.insertElementAt(closeMenu, selected + 3);
						coauthorList.setModel(listModel);
						coauthorList.setSelectedIndex(selected);
					}
				}
				
				if(coauthorList.getSelectedValue().equals(closeMenu)){
					listModel.remove(selected);
					coauthorList.setSelectedIndex(selected -1);
					listModel.remove(coauthorList.getSelectedIndex());
					coauthorList.setSelectedIndex(selected -2);
					listModel.remove(coauthorList.getSelectedIndex());
					coauthorList.setModel(listModel);
				}
				
				int subMenuSelection;
				
				if(!coauthorList.isSelectionEmpty())
					subMenuSelection = coauthorList.getSelectedIndex();
				else
					subMenuSelection = selected - 3;
				
				String selectedItem = (String) listModel.getElementAt(subMenuSelection);
				
				if (selectedItem.equals(searchFor)) {
					String coauthor = (String) listModel.getElementAt(subMenuSelection - 1);
					Services.getBrowser().go(new AuthorResult(coauthor));
				} else if (selectedItem.equals(coauthorSearchFor)) {
					List<String> selectedList = new ArrayList<String>(2);
					selectedList.add(theAuthor);
					selectedList.add((String) listModel.getElementAt(subMenuSelection - 2));
					Services.getBrowser().go(new AuthorResult(selectedList));
				}
				
				if(evt.getClickCount() == 2) {
					//String selection = (String) coauthorList.getSelectedValue();
					//if(!selection.equals(searchFor) && 
					//		!selection.equals(coauthorSearchFor) &&
					//		!selection.equals(closeMenu)) {
						String coauthor = (String)coauthorList.getSelectedValue();
						Services.getBrowser().go(new AuthorResult(coauthor));
					//}
				}
			}
		});
		coauthorList.setLayoutOrientation(JList.VERTICAL);
		coauthorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		coauthorList.setVisibleRowCount(7);
		JScrollPane listScroller = new JScrollPane(coauthorList);
		listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		singleEntryTop.add(listScroller);
	}
	
	/**
	 * add author names to the JList, single author search case
	 */
	private void buildListHelper() {
		int i = 0;			
		while (i < theAuthorList.size()){
			listModel.add(i, theAuthorList.get(i));
			i++;
		}
	}
	
	/**
	 * add author names to the JList, multi author search case
	 */
	private void buildListHelper2() {
		int i = 1;
		while (i < theAuthorList.size()) {
			listModel.add(i - 1, theAuthorList.get(i));
			i++;
		}
	}
	
	/**
	 * Create a formatted display of coauthors for multi author search result
	 */
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