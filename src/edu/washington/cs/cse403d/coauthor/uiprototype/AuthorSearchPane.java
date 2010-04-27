package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.washington.cs.cse403d.coauthor.dataservice.CoauthorDataServiceInterface;

public class AuthorSearchPane extends JPanel {
	private class AuthorField extends JTextField {
		public AuthorField() {
			getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent evt) {
					textValueChanged();
				}
				@Override
				public void insertUpdate(DocumentEvent evt) {
					textValueChanged();
				}
				@Override
				public void removeUpdate(DocumentEvent evt) {
					textValueChanged();
				}
			});
			suggestionsPane = new JScrollPane(suggestionsList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			suggestionsPane.setPreferredSize(new Dimension(200, 80));
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent evt) {
					if(evt.getKeyCode() == KeyEvent.VK_DOWN) {
						suggestionsList.setSelectedIndex(
							suggestionsList.getSelectedIndex() + 1);
					} else if(evt.getKeyCode() == KeyEvent.VK_UP) {
						suggestionsList.setSelectedIndex(
								suggestionsList.getSelectedIndex() + 1);
					} else if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
						if(suggestionsPopup != null && !suggestionsList.isSelectionEmpty()) {
							setText((String)suggestionsList.getSelectedValue());
							hideSuggestions();
						}
					} else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
						hideSuggestions();
					}
				}
			});
			setPreferredSize(new Dimension(150, 24));
			setFont(getFont().deriveFont(Font.BOLD, 14));
		}
		private JScrollPane suggestionsPane;
		private JList suggestionsList = new JList();
		private Popup suggestionsPopup = null;
		private String query = "";
		private List<String> queryResults = new ArrayList<String>();
		public void showSuggestions() {
			if(suggestionsPopup == null) {
				Point location = getLocationOnScreen();
				int offsetY = getSize().height;
				suggestionsPopup = popupFactory.getPopup(
					this, suggestionsPane, location.x, location.y + offsetY);
				suggestionsPopup.show();
			}
		}
		public void hideSuggestions() {
			if(suggestionsPopup != null) {
				suggestionsPopup.hide();
				suggestionsPopup = null;
			}
		}
		private void updateSuggestionsList(String filter) {
			List<String> filteredResults = new ArrayList<String>();
			for(String result : queryResults) {
				if(filter.isEmpty() || result.toLowerCase().contains(filter.toLowerCase()))
					filteredResults.add(result);
			}
			if(filteredResults.size() == 1 &&
					filteredResults.get(0).toLowerCase().equals(getText().toLowerCase()))
				hideSuggestions();
			else {
				suggestionsList.setListData(filteredResults.toArray());
				showSuggestions();
			}
		}
		private void updateSuggestions(String queryPart, String filterPart) {
			final String filter = filterPart;
			if(!queryPart.isEmpty() && !queryPart.equals(query)) {
				query = queryPart;
				final CoauthorDataServiceInterface c = Services.getCoauthorDataServiceInterface();
				executor.execute(new Runnable() {
					public void run() {
						try {
							queryResults = c.getAuthors("+" + query);
						} catch(RemoteException e) {
							System.err.println("Couldn't get suggestions for author");
							return;
						}
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								updateSuggestionsList(filter);
							}
						});
					}
				});
			} else
				updateSuggestionsList(filter);
		}
		private void textValueChanged() {
			List<String> parts = new LinkedList<String>(Arrays.asList(getText().split("[ .,]+")));
			if(parts.size() > 2) {
				Iterator<String> iter = parts.iterator();
				while(iter.hasNext()) {
					if(iter.next().length() <= 1)
						iter.remove();
				}
			}
			String queryPart = (parts.size() > 0) ? parts.get(0) : "";
			String filterPart = (parts.size() > 1) ? parts.get(1) : "";
			updateSuggestions(queryPart, filterPart);
		}
	}
	private JButton submit = new JButton("Search");
	private List<AuthorField> authors = new LinkedList<AuthorField>();
	private Executor executor;
	private PopupFactory popupFactory;
	private JPanel authorsPane;
	private void addAuthor() {
		AuthorField field = new AuthorField();
		boolean isFirst = authors.size() == 0;
		authors.add(field);
		
		JPanel row = new JPanel();
		JLabel label = new JLabel(isFirst ? "Author: " : "Co-author: ");
		label.setPreferredSize(new Dimension(75, label.getPreferredSize().height));
		row.add(label);
		row.add(field);
		if(!isFirst) {
			JButton removeButton = new JButton("-");
			row.add(removeButton);
		}
		authorsPane.add(row);
	}
	public AuthorSearchPane() {
		executor = Executors.newSingleThreadExecutor();
		popupFactory = PopupFactory.getSharedInstance();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel headerPane = new JPanel();
		headerPane.add(new JLabel("Please enter your search terms below"));
		headerPane.add(new HelpMarker("This would be some explanation"));
		add(headerPane);
		
		authorsPane = new JPanel();
		// XXX: need better layout
		authorsPane.setLayout(new BoxLayout(authorsPane, BoxLayout.Y_AXIS));
		addAuthor();
		addAuthor();
		addAuthor();
		add(authorsPane);
	}
}
