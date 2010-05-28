package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.HelpMarker;

public class ArticleSearchPane extends JPanel {
	private static final long serialVersionUID = -2380014657227857630L;
	
	protected JTextField query = new JTextField();
	protected JButton submit = new JButton("Search");
	public ArticleSearchPane() {
		JPanel topPart = new JPanel();
		topPart.add(new JLabel("Please enter your search terms below"));
		topPart.add(new HelpMarker("Click here for more information."));
		
		JPanel bottomPart = new JPanel();
		query.setPreferredSize(new Dimension(100, query.getPreferredSize().height));
		bottomPart.add(query);
		bottomPart.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Services.getBrowser().go(new ArticleSearchResults(query.getText()));
			}
		});
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(topPart);
		add(bottomPart);
	}
}
