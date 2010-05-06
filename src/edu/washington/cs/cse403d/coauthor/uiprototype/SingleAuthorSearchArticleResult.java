package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Dimension;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

	/*
	 * Creates list of articles the author has worked on (as THE author)
	 */
class SingleAuthorSearchArticleResult extends JPanel 
											implements ListSelectionListener {
	private edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface CDSI = 
		Services.getCoauthorDataServiceInterface();
	
	private String theAuthor;
	private JList pubList;
	private List<Publication> publications;
	private DefaultListModel listModel;
	
	public SingleAuthorSearchArticleResult(String author) {
		theAuthor = author;
		initialize();
	}
	
	private void initialize() {
		setVisible(true);
			listModel = new DefaultListModel();
		pubList = new JList(listModel);
		try {
			publications = CDSI.getPublicationsForAnyAuthor(theAuthor);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Um, this isn't really supposed to happen",
					"Error!",JOptionPane.ERROR_MESSAGE);
		}
		buildPubList();
	}
	
	private void buildPubList() {
		buildListHelper();
		pubList.addListSelectionListener(this);
		pubList.setLayoutOrientation(JList.VERTICAL);
		pubList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);			
		JScrollPane listScroller = new JScrollPane(pubList);
		listScroller.setPreferredSize(new Dimension(400, 120));
		add(listScroller);			
	}

	private void buildListHelper() {
		int i = 0;			
		while (i < publications.size()){
			listModel.add(i, publications.get(i).getTitle());
				i++;
			}
	}
	
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}