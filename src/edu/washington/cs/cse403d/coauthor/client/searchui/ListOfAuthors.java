package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.ListPopupMouseListener;
import edu.washington.cs.cse403d.coauthor.client.utils.StringUtils;

/**
 * Display a list of authors. Includes some common operations on authors in a
 * context menu (search for this author, copy name to clipboard) -- to add more
 * actions, override buildContextMenu().
 * @author William Cauchois
 */
public class ListOfAuthors extends JList {
	public ListOfAuthors(List<String> theAuthors) {
		// setListData(theAuthors.toArray());
		// HACK(wcauchois): The above doesn't work because FilterPanel expects lists to use DefaultListModel.
		// So, we do this (ugly):
		DefaultListModel model = new DefaultListModel();
		setModel(model);
		for(String author : theAuthors)
			model.addElement(author);
		
		addMouseListener(new ListPopupMouseListener(buildContextMenu()));
	}
	protected JPopupMenu buildContextMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem menuItem;
		
		menuItem = new JMenuItem("Search for this author");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Services.getBrowser().go(new AuthorResult((String)getSelectedValue()));
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Copy to clipboard");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				StringUtils.copyToClipboard((String)getSelectedValue());
			}
		});
		menu.add(menuItem);
		
		return menu;
	}
}
