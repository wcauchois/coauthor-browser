package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

public class StartPage extends BrowserPage {
	public StartPage() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel title = new JLabel("Start Here");
		title.setFont(title.getFont().deriveFont(Font.BOLD, 24));
		add(title);
		
		JButton button = new JButton("Search authors and articles");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BrowserService.getBrowser().go(new SearchPage());
			}
		});
		add(button);
	}
	public String getTitle() {
		return "Start";
	}
}
