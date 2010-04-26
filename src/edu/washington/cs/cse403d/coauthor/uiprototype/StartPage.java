package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
	
	/*TODO:
	 * Make Search Button obtain the string from JTextField and store it
	 * 	in a List<String>
	 * Make Search Button return a new articleSearchPage instance whose 
	 * 	parameter is a List<String> which contains the list of authors, first
	 * 	in the list being the author and everyone else co-authors.
	 */
	private class AuthorSearchPane extends JPanel {
		protected JTextField query = new JTextField();
		protected JTextField query2 = new JTextField(); //co-author 1
		protected JTextField query3 = new JTextField(); //co-author 2
		protected JTextField query4 = new JTextField(); //co-author 3
		
		protected JButton submit = new JButton("Search");
		protected JButton submit2 = new JButton("Search");
		protected JButton submit3 = new JButton("Search");
		protected JButton submit4 = new JButton("Search");
		
		protected JLabel label1 = new JLabel("Author");
		protected JLabel label2 = new JLabel("Co-Author 1");
		protected JLabel label3 = new JLabel("Co-Author 2");
		protected JLabel label4 = new JLabel("Co-Author 3");
		
		public AuthorSearchPane() {
			JPanel topPart = new JPanel();
			topPart.add(new JLabel("Please enter your search terms below"));
			topPart.add(new HelpMarker("This would be some explanation"));
			
			JPanel bottomPart = new JPanel();
			bottomPart.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			query.setPreferredSize(new Dimension(100, query.getPreferredSize().height));
			query2.setPreferredSize(new Dimension(100, query2.getPreferredSize().height));
			query3.setPreferredSize(new Dimension(100, query3.getPreferredSize().height));
			query4.setPreferredSize(new Dimension(100, query4.getPreferredSize().height));
			
			/***** First Search menu set *****/
			c.gridx = 0;
			c.gridy = 0;
			bottomPart.add(label1, c);
			
			c.gridx = 1;
			c.gridy = 0;
			c.insets = new Insets(0, 5, 0, 0);
			bottomPart.add(query, c);
			
			c.gridx = 2;
			c.gridy = 0;
			c.insets = new Insets(0, 5, 0, 0);
			bottomPart.add(submit, c);
			
			/***** Second Search menu set *****/
			c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(5, 0, 0, 0);
			bottomPart.add(label2, c);
			
			c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(5, 5, 0, 0);
			bottomPart.add(query2, c);
			
			c.gridx = 2;
			c.gridy = 1;
			c.insets = new Insets(5, 5, 0, 0);
			//bottomPart.add(submit2, c);
			
			/***** Third Search menu set *****/
			c.gridx = 0;
			c.gridy = 2;
			c.insets = new Insets(5, 0, 0, 0);
			bottomPart.add(label3, c);
			
			c.gridx = 1;
			c.gridy = 2;
			c.insets = new Insets(5, 5, 0, 0);
			bottomPart.add(query3, c);
			
			c.gridx = 2;
			c.gridy = 2;
			c.insets = new Insets(5, 5, 0, 0);
			//bottomPart.add(submit3, c);
			
			/***** Fourth Search menu set *****/
			c.gridx = 0;
			c.gridy = 3;
			c.insets = new Insets(5, 0, 0, 0);
			bottomPart.add(label4, c);
			
			c.gridx = 1;
			c.gridy = 3;
			c.insets = new Insets(5, 5, 0, 0);
			bottomPart.add(query4, c);
			
			c.gridx = 2;
			c.gridy = 3;
			c.insets = new Insets(5, 5, 0, 0);
			//bottomPart.add(submit4, c);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(topPart);
			add(bottomPart);
		}
	}
	
	
	/*TODO:
	 * Make Search Button obtain the string from JTextField and store it
	 * Make Search Button return a new articleSearchPage instance whose 
	 * 	parameter is a string which contains the article title.
	 */
	private class ArticleSearchPane extends JPanel {
		protected JTextField query = new JTextField();
		protected JButton submit = new JButton("Search");
		protected JLabel label1 = new JLabel("Article Title");
		
		public ArticleSearchPane() {
			JPanel topPart = new JPanel();
			topPart.add(new JLabel("Please enter your search terms below"));
			topPart.add(new HelpMarker("This would be some explanation"));
			
			JPanel bottomPart = new JPanel();
			bottomPart.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			query.setPreferredSize(new Dimension(100, query.getPreferredSize().height));
			
			c.gridx = 0;
			c.gridy = 0;
			bottomPart.add(label1, c);
			
			c.gridx = 1;
			c.gridy = 0;
			c.insets = new Insets(0, 5, 0, 0);
			bottomPart.add(query, c);
			
			c.gridx = 2;
			c.gridy = 0;
			c.insets = new Insets(0, 5, 0, 0);
			bottomPart.add(submit, c);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(topPart);
			add(bottomPart);
		}
	}
	public String getTitle() {
		return "Start";
	}
}