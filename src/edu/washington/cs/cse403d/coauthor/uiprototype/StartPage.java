package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Font;

import javax.swing.JLabel;

public class StartPage extends BrowserPage {
	public StartPage() {
		JLabel lab = new JLabel("Start Here");
		lab.setFont(lab.getFont().deriveFont(Font.BOLD, 24));
		add(lab);
	}
}
