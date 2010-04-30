package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class SearchPage extends BrowserPage {
	public SearchPage() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel title = new JLabel("Search");
		title.setFont(title.getFont().deriveFont(Font.BOLD, 24));
		add(title);
		final JTextField query = new JTextField();
		add(query);
		JButton submit = new JButton("Go");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String theQuery = query.getText();
				if(theQuery.length() == 0)
					JOptionPane.showMessageDialog(null, "Please enter a query");
				else {
					query.setText("");
//					Services.getBrowser().go(
//						new SearchResultsPage(theQuery));
				}
			}
		});
		add(submit);
	}
	public String getTitle() {
		return "Search";
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return new Class[] { StartPage.class };
	}
}
