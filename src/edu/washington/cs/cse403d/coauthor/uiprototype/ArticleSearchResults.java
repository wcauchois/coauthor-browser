package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Dimension;
import java.awt.Font;
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

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;


public class ArticleSearchResults extends BrowserPage {
	private JLabel title;
	private CoauthorDataServiceInterface CDSI =	Services.getCoauthorDataServiceInterface();
	private List<Publication> publication;
	
	public ArticleSearchResults(String query) {
		title = new JLabel(query);
		initialize();
	}
	
	private void initialize() {
		this.setVisible(true);
		
		//publication = CDSI.getPublicationTitles(title.getText());
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		//Title Label Formatting
		c.gridx = 0;
		c.gridy = 0;		
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		add(title, c);
		
	}
	
}
