package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

public class AuthorSearchResults extends BrowserPage {
	private List<String> queries;
	
	public AuthorSearchResults(List<String> queries) {
		this.queries = queries;
		initialize();
				
	}
	
	private void initialize() {
		this.setVisible(true);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		
		// XXX(wcauchois): kevin you need to fix this!
//		if(queries.size() == 1) {
//			add(new SingleAuthorSearchResult(queries.get(0)), c);
//		} else
//			add(new SingleAuthorSearchResult(queries), c);
//		
//		c.gridx = 0;
//		c.gridy = 1;
//		if(queries.size() == 1) {
//			add(new SingleAuthorSearchArticleResult(queries.get(0)), c);
//		} else
//			add(new SingleAuthorSearchArticleResult(queries.get(0)), c);
	}
	
	public String getTitle() {
		return "Results for \"" + queries.get(0) + "\"";
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return new Class[] { SearchPage.class };
	}	
}