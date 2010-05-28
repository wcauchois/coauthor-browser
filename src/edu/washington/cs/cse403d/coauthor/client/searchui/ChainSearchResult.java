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
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.FilterPanel;
import edu.washington.cs.cse403d.coauthor.client.utils.HyperLinkButton;
import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;

public class ChainSearchResult extends BrowserPage
{
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	private List<PathLink> chain;
	private DefaultListModel listModel;
	private JList coauthorList;
	
	private JPanel singleEntryTop;
	private JPanel multiEntryTop;
	
	/**
	 * Constructor for single author search result screen
	 * 
	 * @param author the author that was entered as the query
	 */
	public ChainSearchResult(String author1, String author2) {
		setVisible(true);
		setLayout(new BorderLayout());
		try {
			chain = CDSI.getOneShortestPathBetweenAuthors(author1, author2, getAutoscrolls());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < chain.size(); i++)
			System.out.println(chain.get(i).toString());
		singleEntryInitialize();
	}
	
	/**
	 * Constructor for multi-entry author search result screen
	 * 
	 * @param authorList list of queries(author names)
	 */
	/**
	 * Internal helper method for single-author search result
	 */
	private void singleEntryInitialize() {
		singleEntryTop = new JPanel();
		singleEntryTop.setLayout(new BoxLayout(singleEntryTop, BoxLayout.Y_AXIS));
		singleEntryTop.setVisible(true);	
	
		//Title
		JLabel title = new JLabel("Results for Chain Search: ");
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 10.0f;
		title.setFont(f.deriveFont(s));
		singleEntryTop.add(title);
		singleEntryTop.add(Box.createRigidArea(new Dimension(0, 5)));
		
		//Coauthor list label
		JLabel coauthors = new JLabel("Author Chain:");
		f = coauthors.getFont();
		s = coauthors.getFont().getSize2D();
		s += 6.0f;
		coauthors.setFont(f.deriveFont(s));
		singleEntryTop.add(coauthors);
		
		//Coauthor list
		listModel = new DefaultListModel();
		coauthorList = new JList(listModel);
		
		buildCoauthorList();
		add(new FilterPanel(coauthorList));
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
		
		//Coauthors
		JLabel coauthor = new JLabel("Coauthors");
		f = coauthor.getFont();
		s = coauthor.getFont().getSize2D();
		s += 8.0f;
		coauthor.setFont(f.deriveFont(s));
		multiEntryTop.add(coauthor);
		multiEntryTop.add(new JSeparator(SwingConstants.HORIZONTAL));
				
		addCoauthors();
		JScrollPane listScroller = new JScrollPane(coauthorList);
		listScroller.setPreferredSize(new Dimension(60, 60));
		multiEntryTop.add(listScroller);
		multiEntryTop.add(Box.createVerticalStrut(10));
	}
	
	/**
	 * Builds the co-author list for single author search result
	 */
	private void buildCoauthorList() {
		buildListHelper();
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
		coauthorList.setVisibleRowCount(5);
		JScrollPane listScroller = new JScrollPane(coauthorList);
		listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		singleEntryTop.add(listScroller);
	}
	
	/**
	 * add author names to the JList, single author search case
	 */
	private void buildListHelper() {
		int i = 0;			
		while (i < chain.size()){
			listModel.add(i, chain.get(i).toString().substring(0, chain.get(i).toString().indexOf('-')));
			i++;
		}
	}
	
	/**
	 * Create a formatted display of coauthors for multi author search result
	 */
	private void addCoauthors() {
		int i = 1;	
		while (i < chain.size()) {
			final HyperLinkButton coauthor = new HyperLinkButton(chain.get(i).toString().substring(0, chain.get(i).toString().indexOf('-')));
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
