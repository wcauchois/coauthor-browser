package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.HelpMarker;
import edu.washington.cs.cse403d.coauthor.uiprototype.graphvis.PrefuseVisualization;

/**
 * This is the first page the user sees when he opens the coauthor application.
 * Right now, it displays the logo along with tabs for article and author search. 
 * @author William Cauchois
 */
public class SearchPage extends BrowserPage {
	public static final int AUTHOR_SEARCH = 0;
	public static final int ARTICLE_SEARCH = 1;
	public SearchPage() {
		this(AUTHOR_SEARCH);
	}
	public SearchPage(int searchType) {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(tabbedPane.getFont().deriveFont(Font.ITALIC, 14));
		setLayout(new BorderLayout(10, 20));
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.setPreferredSize(new Dimension(400, 250));
		
		JLabel logo = new JLabel(Services.getResourceManager().loadImageIcon("logo2.png"));
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		add(logo, BorderLayout.NORTH);
		
		tabbedPane.addTab("Author Search", new AuthorSearchPane(this));
		tabbedPane.addTab("Article Search", new ArticleSearchPane());
		tabbedPane.addTab("Visual Search", new VisualSearchPane());
		
		if(searchType == AUTHOR_SEARCH)
			tabbedPane.setSelectedIndex(0);
		else if(searchType == ARTICLE_SEARCH)
			tabbedPane.setSelectedIndex(1);
	}
	// XXX: move AuthorSearchPane out too!!
	private class SearchPane extends JPanel {
		protected JTextField query = new JTextField();
		protected JButton submit = new JButton("Search");
		public SearchPane() {
			JPanel topPart = new JPanel();
			topPart.add(new JLabel("Please enter your search terms below"));
			topPart.add(new HelpMarker("Click here for more information."));
			
			JPanel bottomPart = new JPanel();
			query.setPreferredSize(new Dimension(100, query.getPreferredSize().height));
			bottomPart.add(query);
			bottomPart.add(submit);
			submit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					Services.getBrowser().go(new ArticleResult(query.getText()));
				}
			});
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(topPart);
			add(bottomPart);
		}
	}
	
	private class ArticleSearchPane extends SearchPane {
		
	}
	
	private class VisualSearchPane extends JPanel 
	{
		private JTextField field = new JTextField();
		private JButton search = new JButton("Search");
		private PrefuseVisualization pv;
		
		public VisualSearchPane() 
		{
			pv = new PrefuseVisualization();
			field.setPreferredSize(new Dimension(150, field.getPreferredSize().height));
			JPanel searchPane = new JPanel();
			searchPane.add(field);
			searchPane.add(search);
			add(searchPane,BorderLayout.CENTER);
			
			final JPanel submitPane = new JPanel();
			submitPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			add(submitPane, BorderLayout.SOUTH);
			submitPane.add(pv.getD());
			
			search.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					submitPane.remove(pv.getD());
					pv = new PrefuseVisualization(field.getText());
					submitPane.add(pv.getD());
				}
			});
		}
	}
	
	public String getTitle() {
		return "Search";
	}
}