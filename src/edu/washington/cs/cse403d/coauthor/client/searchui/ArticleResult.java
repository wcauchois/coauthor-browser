package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.HyperLinkButton;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * Displays search result information about a publication, including a list
 * of co-authors.
 * @author Kevin Bang
 */
public class ArticleResult extends BrowserPage {
	private CoauthorDataServiceInterface CDSI =	Services.getCoauthorDataServiceInterface();
	private String articleTitle;
	private JLabel title;
	private JPanel articleInfo;
	private JPanel authorInfo;
	private Publication publication;
	private JPanel contentPane;
	private JList authorList;
	private DefaultListModel listModel;
	
	public ArticleResult(String query) {
		articleTitle = query;
		try {
			publication = CDSI.getPublication(query);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Um, this isn't really supposed to happen",
					"Error!",JOptionPane.ERROR_MESSAGE);
		}
		initialize();
	}
	
	private void initialize() {
		//Wrapping around does not work
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(380, 400));
		
		
		contentPane = new JPanel();
		contentPane.setVisible(true);
		contentPane.setLayout(new BorderLayout());
		
		buildTitle();
		buildAuthorInfo();
		buildArticleInfo();
		
		JLabel authorLabel = new JLabel("Authors:");
		Font f = authorLabel.getFont();
		float s = authorLabel.getFont().getSize2D();
		s += 4.0f;
		authorLabel.setFont(f.deriveFont(s));		
		
		JPanel topPart = new JPanel();
		topPart.setLayout(new BoxLayout(topPart, BoxLayout.Y_AXIS));
		topPart.add(title);
		topPart.add(Box.createVerticalStrut(10));
		topPart.add(authorLabel);
		topPart.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		contentPane.add(topPart, BorderLayout.PAGE_START);
		contentPane.add(authorInfo, BorderLayout.CENTER);
		contentPane.add(articleInfo, BorderLayout.PAGE_END);
		
		add(contentPane);
	}
	
	private void buildTitle() {
		title = new JLabel(publication.getTitle());
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
	}
	
	private void buildArticleInfo() {
		articleInfo = new JPanel();
		articleInfo.setVisible(true);
		articleInfo.setLayout(new BoxLayout(articleInfo, BoxLayout.Y_AXIS));
		
		//Source. Will not be displayed if !hasJournal();
		JLabel sourceLabel;
		if(publication.hasJournal())
			sourceLabel = new JLabel("Source:");
		else
			sourceLabel = new JLabel("Year:");
		Font f = sourceLabel.getFont();
		float s = sourceLabel.getFont().getSize2D();
		s += 4.0f;
		sourceLabel.setFont(f.deriveFont(s));
		articleInfo.add(sourceLabel);
		articleInfo.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		String source;
		if(publication.hasJournal())
			source = publication.getJournal() + ", " + publication.getYear() 
						+ ", Vol. "	+ publication.getVolume() + ", Number " 
						+ publication.getNumber() + ", page " + publication.getPages();
		else
			source = publication.getYear().toString();
		JLabel articleSource = new JLabel(source);
		articleInfo.add(articleSource);
		articleInfo.add(Box.createVerticalStrut(10));
		
		//ISBN
		JLabel ISBNLabel = new JLabel("ISBN:");
		f = ISBNLabel.getFont();
		s = ISBNLabel.getFont().getSize2D();
		s += 4.0f;
		ISBNLabel.setFont(f.deriveFont(s));
		articleInfo.add(ISBNLabel);
		articleInfo.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		JLabel ISBN;
		if(publication.hasIsbn()) 
			ISBN = new JLabel(publication.getIsbn());
		else
			ISBN = new JLabel("This article does not have ISBN");
		articleInfo.add(ISBN);
		articleInfo.add(Box.createVerticalStrut(10));
		
		//EE
		JLabel EELabel = new JLabel("Electronic Edition:");
		f = EELabel.getFont();
		s = EELabel.getFont().getSize2D();
		s += 4.0f;
		EELabel.setFont(f.deriveFont(s));
		articleInfo.add(EELabel);
		articleInfo.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		final HyperLinkButton EE;
		if(publication.getEe() == null)
			EE = new HyperLinkButton("This article does not have an electronic edition");
		else {
			EE = new HyperLinkButton(publication.getEe());
			//Clipboard Access
			EE.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					StringSelection data = new StringSelection(EE.getText());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(data, data);
					showCopyNotice();
				}
			});
		}
		articleInfo.add(EE);
	}
	
	private void showCopyNotice() {
		JOptionPane.showMessageDialog(this,
				"The URL has been copied to the clipboard.",
				"Notice", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void buildAuthorInfo() {
		authorInfo = new JPanel();
		authorInfo.setVisible(true);
		authorInfo.setLayout(new BoxLayout(authorInfo, BoxLayout.Y_AXIS));
		
		JLabel authorLabel = new JLabel("Authors:");
		Font f = authorLabel.getFont();
		float s = authorLabel.getFont().getSize2D();
		s += 4.0f;
		authorLabel.setFont(f.deriveFont(s));		
		
		//Add list of authors
		buildList();
		authorList = new JList(listModel);
		authorList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					String article = (String)authorList.getSelectedValue();
					Services.getBrowser().go(new ArticleResult(article));
				}
			}
		});
		authorList.setLayoutOrientation(JList.VERTICAL);
		authorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);			
		JScrollPane listScroller = new JScrollPane(authorList);
		listScroller.setPreferredSize(new Dimension(60, 120));
		authorInfo.add(listScroller);
		authorInfo.add(Box.createVerticalStrut(10));
		
	}
	
	private void buildList() {
		listModel = new DefaultListModel();
		List<String> list = publication.getAuthors();
		int i = 0;
		while (i < list.size()) {
			listModel.add(i, list.get(i));
			i++;
		}
	}
	
	public String getTitle() {
		return articleTitle;
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return new Class[] { SearchPage.class };
	}
}