package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import edu.washington.cs.cse403d.coauthor.client.ResourceManager;
import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.HelpMarker;
import edu.washington.cs.cse403d.coauthor.client.utils.QuerySuggestionsField;

public class ChainSearchPane extends JPanel
{
	protected JTextField author1 = new JTextField();
	protected JTextField author2 = new JTextField();
	protected JButton submit = new JButton("Search");
	public ChainSearchPane() {
		JPanel topPart = new JPanel();
		topPart.add(new JLabel("Please enter your search terms below"));
		topPart.add(new HelpMarker("Click here for more information."));
		
		JPanel bottomPart = new JPanel();
		author1.setPreferredSize(new Dimension(200, author1.getPreferredSize().height));
		bottomPart.add(author1);
		author2.setPreferredSize(new Dimension(200, author2.getPreferredSize().height));
		bottomPart.add(author2);
		
		
		JPanel searchPart = new JPanel();
		searchPart.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Services.getBrowser().go(new ChainSearchResult(author1.getText(), author2.getText()));
			}
		});
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(topPart, BorderLayout.NORTH);
		add(bottomPart, BorderLayout.NORTH);
		add(searchPart, BorderLayout.NORTH);
	}
}
