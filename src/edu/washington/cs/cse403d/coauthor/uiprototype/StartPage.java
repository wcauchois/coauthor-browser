package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class StartPage extends BrowserPage {
	public StartPage() {
		JTabbedPane tabbedPane = new JTabbedPane();
		setLayout(new BorderLayout(10, 20));
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.setPreferredSize(new Dimension(400, 200));
		
		JLabel logo = new JLabel("Constant Time Industries Logo");
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		logo.setFont(logo.getFont().deriveFont(Font.BOLD, 22));
		add(logo, BorderLayout.NORTH);
		
		tabbedPane.addTab("Author Search", new AuthorSearchPane());
		tabbedPane.addTab("Article Search", new ArticleSearchPane());
	}
	private class SearchPane extends JPanel {
		protected JTextField query = new JTextField();
		protected JButton submit = new JButton("Search");
		public SearchPane() {
			JPanel topPart = new JPanel();
			topPart.add(new JLabel("Please enter your search terms below"));
			topPart.add(new HelpMarker("This would be some explanation"));
			
			JPanel bottomPart = new JPanel();
			query.setPreferredSize(new Dimension(100, query.getPreferredSize().height));
			bottomPart.add(query);
			bottomPart.add(submit);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(topPart);
			add(bottomPart);
		}
	}
	private class AuthorSearchPane extends SearchPane {
	}
	private class ArticleSearchPane extends SearchPane {
	}
	public String getTitle() {
		return "Start";
	}
}