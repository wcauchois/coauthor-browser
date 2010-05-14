package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;


public class AuthorListDisplay extends JPanel{
	private List<String> authorList;
	private JPanel authorListPanel;
	
	public AuthorListDisplay(List<String> authorList) {
		this.authorList = authorList;
		multiEntryTopBuild();
		add(authorListPanel);
	}
	
	private void multiEntryTopBuild() {
		authorListPanel = new JPanel();
		authorListPanel.setLayout(new BoxLayout(authorListPanel, BoxLayout.Y_AXIS));
		authorListPanel.setVisible(true);
		
		//The Author Name.
		JLabel title = new JLabel("Author");
		Font f = title.getFont();
		float s = title.getFont().getSize2D();
		s += 8.0f;
		title.setFont(f.deriveFont(s));
		authorListPanel.add(title);
		
		authorListPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		final JLabel author = new JLabel(authorList.get(0));		
		authorListPanel.add(author);
		author.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				List<String> list = new ArrayList<String>();
				list.add(author.getText());
				Services.getBrowser().go(new AuthorSearchResultsMain(list));
			}
		});
		
		authorListPanel.add(Box.createVerticalStrut(10));
		
		JLabel coauthor = new JLabel("Coauthors");
		f = coauthor.getFont();
		s = coauthor.getFont().getSize2D();
		s += 8.0f;
		coauthor.setFont(f.deriveFont(s));
		authorListPanel.add(coauthor);
		authorListPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
				
		addCoauthors();
		authorListPanel.add(Box.createVerticalStrut(10));
	}
	
	private void addCoauthors() {
		int i = 1;
		//implement an actionListner in here
		while (i < authorList.size()) {
			final JLabel coauthor = new JLabel(authorList.get(i));
			authorListPanel.add(coauthor);
			coauthor.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					List<String> list = new ArrayList<String>();
					list.add(coauthor.getText());
					Services.getBrowser().go(new AuthorSearchResultsMain(list));
				}
			});
			authorListPanel.add(Box.createVerticalStrut(2));
			i++;
		}
	}	
}
