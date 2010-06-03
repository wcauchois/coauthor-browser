package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import edu.washington.cs.cse403d.coauthor.client.utils.SuggestionsField;

public class ChainSearchPane extends JPanel
{
	//protected JTextField author1 = new JTextField();
	//protected JTextField author2 = new JTextField();
	protected ChainAuthorField author1 = new ChainAuthorField();
	protected ChainAuthorField author2 = new ChainAuthorField();
	protected JButton submit = new JButton("Search");
	protected List<ChainAuthorField> authorFields = new LinkedList<ChainAuthorField>();
	public ChainSearchPane(BrowserPage parent) {
		authorFields.add(author1);
		authorFields.add(author2);
		JPanel topPart = new JPanel();
		topPart.add(new JLabel("Please enter your search terms below"));
		topPart.add(new HelpMarker("Click here for more information."));
		
		JPanel bottomPart = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		bottomPart.add(new JLabel("Author 1: "), c);
		
		c.gridx = 1;
		c.gridy = 0;
		author1.setPreferredSize(new Dimension(200, author1.getPreferredSize().height));
		bottomPart.add(author1, c);
		
		c.gridx = 0;
		c.gridy = 1;
		bottomPart.add(new JLabel("Author 2: "), c);
		
		c.gridx = 1;
		c.gridy = 1;
		author2.setPreferredSize(new Dimension(200, author2.getPreferredSize().height));
		bottomPart.add(author2, c);
		
		
		JPanel searchPart = new JPanel();
		searchPart.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!author1.getText().equals("") && !author2.getText().equals(""))
					Services.getBrowser().go(new ChainSearchResult(author1.getText(), author2.getText()));
				else
				{
					if (author1.getText().equals(""))
						author1.markInvalid();
					if (author2.getText().equals(""))
						author2.markInvalid();
				}
			}
		});
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(topPart, BorderLayout.NORTH);
		add(bottomPart, BorderLayout.NORTH);
		add(searchPart, BorderLayout.NORTH);
	}
	
	private class ChainAuthorField extends QuerySuggestionsField {
		private static final long serialVersionUID = -5382087475322086605L;
		
		private final Color invalidMarkColor = Color.RED;
		private boolean markedInvalid = false;
		public void markInvalid() {
			markedInvalid = true;
			setBackground(invalidMarkColor);
		}
		@Override
		protected void onSubmit() {
			ChainSearchPane.this.onSubmit();
		}
		public ChainAuthorField() {
			addFocusListener(new FocusAdapter() {
				@Override public void focusGained(FocusEvent evt) {
					if(markedInvalid) {
						// Restore the default background color
						setBackground(SystemColor.text);
					}
				}
			});
		}
		@Override
		protected List<String> issueQuery(String part) throws Exception {
			return Services.getCoauthorDataServiceInterface().getAuthors(part);
		}
	}
	
	protected void onSubmit() {
		if (!author1.getText().equals("") && !author2.getText().equals(""))
			Services.getBrowser().go(new ChainSearchResult(author1.getText(), author2.getText()));
		else
		{
			if (author1.getText().equals(""))
				author1.markInvalid();
			if (author2.getText().equals(""))
				author2.markInvalid();
		}
	}
	public List<String> getAuthors() {
		List<String> authors = new ArrayList<String>();
		boolean haveInvalidFields = false;
		for(ChainAuthorField field : authorFields) {
			if(field.getText().trim().length() == 0) {
				field.markInvalid();
				haveInvalidFields = true;
			} else if(!haveInvalidFields)
				authors.add(field.getText());
		}
		if(haveInvalidFields)
			return null;
		else
			return authors;
	}
}
