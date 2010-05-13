package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
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
		contentPane = new JPanel();
		contentPane.setVisible(true);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		buildTitle();
		buildAuthorInfo();
		buildArticleInfo();
		contentPane.add(title);
		contentPane.add(Box.createVerticalStrut(10));
		contentPane.add(authorInfo);
		contentPane.add(articleInfo);
		
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
		
		//List of authors for this article
		List<String> authors = publication.getAuthors();
		
		JLabel authorLabel = new JLabel("Authors:");
		Font f = authorLabel.getFont();
		float s = authorLabel.getFont().getSize2D();
		s += 4.0f;
		authorLabel.setFont(f.deriveFont(s));
		authorInfo.add(authorLabel);
		authorInfo.add(new JSeparator(SwingConstants.HORIZONTAL));
		//Add list of authors
		int i = 0;
		while (i < authors.size()) {
			final HyperLinkButton author = new HyperLinkButton(authors.get(i));		
			authorInfo.add(author);
			author.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					List<String> list = new ArrayList<String>();
					list.add(author.getText());
					Services.getBrowser().go(new AuthorResult(list));
				}
			});
			authorInfo.add(Box.createVerticalStrut(3));
			i++;
		}		
		authorInfo.add(Box.createVerticalStrut(10));
	}
	
	//Potentially TOO long...
	public String getTitle() {
		return articleTitle;
	}
	@SuppressWarnings("unchecked")
	public Class[] getCrumbs() {
		return new Class[] { SearchPage.class };
	}
}