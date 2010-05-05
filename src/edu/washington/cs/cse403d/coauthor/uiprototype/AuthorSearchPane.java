package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
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

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;

/*
 * TODO: put AuthorsPane into a JScrollPane for when it gets big
 * TODO: fix the layout on the search button
 * TODO: make & use icons for plus and minus buttons
 * TODO: javadoc!!!!
 */
public class AuthorSearchPane extends JPanel {
	private class AuthorField extends JTextField {
		private class TextChangedListener implements DocumentListener {
			public void changedUpdate(DocumentEvent evt) { textValueChanged(); }
			public void insertUpdate(DocumentEvent evt) { textValueChanged(); }
			public void removeUpdate(DocumentEvent evt) { textValueChanged(); }
		}
		private class KeyboardShortcuts extends KeyAdapter {
			@Override
			public void keyPressed(KeyEvent evt) {
				boolean scrollDown = evt.getKeyCode() == KeyEvent.VK_DOWN;
				boolean scrollUp = evt.getKeyCode() == KeyEvent.VK_UP;
				if(scrollUp || scrollDown) {
					int newIndex = suggestionsList.getSelectedIndex() + (scrollDown ? 1 : -1);
					suggestionsList.setSelectedIndex(newIndex);
					suggestionsList.ensureIndexIsVisible(newIndex);
				} else if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
					if(suggestionsPopup != null && !suggestionsList.isSelectionEmpty()) {
						setText((String)suggestionsList.getSelectedValue());
						hideSuggestions();
					}
				} else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
					hideSuggestions();
				}
			}
		}
		public AuthorField() {
			getDocument().addDocumentListener(new TextChangedListener());
			suggestionsPane = new JScrollPane(suggestionsList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			suggestionsPane.setPreferredSize(new Dimension(200, 80));
			addKeyListener(new KeyboardShortcuts());
			setPreferredSize(new Dimension(150, 24));
			setFont(getFont().deriveFont(Font.BOLD, 14));
			addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					hideSuggestions();
				}
			});
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
			suggestionsList.setSelectedIndex(0);
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
							queryResults = c.getAuthors(query);
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
	private List<AuthorField> authorFields = new LinkedList<AuthorField>();
	private Executor executor;
	private PopupFactory popupFactory;
	private class AuthorsPane extends JPanel {
		private class FocusPolicy extends FocusTraversalPolicy {
			private Component getComponentPlusN(Component comp, int n) {
				int i = authorFields.indexOf(comp);
				if(i < 0) return authorFields.get(0);
				else {
					i = (i + n) % authorFields.size();
					if(i < 0) i += authorFields.size();
					return authorFields.get(i);
				}
			}
			@Override
			public Component getComponentAfter(Container cycleRoot, Component comp) {
				return getComponentPlusN(comp, 1);
			}
			@Override
			public Component getComponentBefore(Container cycleRoot, Component comp) {
				return getComponentPlusN(comp, -1);
			}
			@Override
			public Component getDefaultComponent(Container cycleRoot) {
				return authorFields.get(0);
			}
			@Override
			public Component getFirstComponent(Container cycleRoot) {
				return authorFields.get(0);
			}
			@Override
			public Component getLastComponent(Container arg0) {
				return authorFields.get(authorFields.size() - 1);
			}
		}
		private GroupLayout layout;
		private GroupLayout.SequentialGroup hGroup, vGroup;
		private GroupLayout.ParallelGroup[] columns = new GroupLayout.ParallelGroup[N_COLUMNS];
		private List<JComponent[]> rows = new LinkedList<JComponent[]>();
		private static final int N_COLUMNS = 4;
		private AuthorsPane thePane;
		public AuthorsPane() {
			thePane = this;
			setLayout(layout = new GroupLayout(this));
			setFocusTraversalPolicy(new FocusPolicy());
			setFocusCycleRoot(true);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			hGroup = layout.createSequentialGroup();
			for(int i = 0; i < N_COLUMNS; i++) {
				columns[i] = layout.createParallelGroup();
				hGroup.addGroup(columns[i]);
			}
			layout.setHorizontalGroup(hGroup);
			
			vGroup = layout.createSequentialGroup();
			layout.setVerticalGroup(vGroup);
			
			addFirstRow();
		}
		private void addFirstRow() {
			AuthorField field = new AuthorField();
			authorFields.add(field);
			JButton minusButton = new MinusButton();
			minusButton.setEnabled(false);
			addRow(new JLabel("Author:"), field, minusButton, new PlusButton());
		}
		private void refresh() {
			getParent().validate();
			repaint();
		}
		private class MinusButton extends JButton implements ActionListener {
			private JComponent[] otherCells;
			public MinusButton(JComponent... otherCells) {
				super("-");
				this.otherCells = otherCells.clone();
				addActionListener(this);
				setMinimumSize(new Dimension(20, 20));
			}
			public void actionPerformed(ActionEvent evt) {
				for(JComponent cell : otherCells) {
					if(cell instanceof AuthorField)
						authorFields.remove(cell);
					thePane.remove(cell);
				}
				thePane.remove(this);
				refresh();
			}
		}
		private class PlusButton extends JButton implements ActionListener {
			public PlusButton() {
				super("+");
				addActionListener(this);
			}
			public void actionPerformed(ActionEvent evt) {
				AuthorField field = new AuthorField();
				JLabel label = new JLabel("Co-author:");
				JButton plusButton = new PlusButton();
				JButton minusButton = new MinusButton(field, label, plusButton);
				authorFields.add(field);
				addRow(label, field, minusButton, plusButton);
				refresh();
			}
		}
		private void addRow(JComponent... cells) {
			GroupLayout.ParallelGroup row = layout.createParallelGroup(
					GroupLayout.Alignment.BASELINE);
			int i = 0;
			for(JComponent cell : cells) {
				columns[i].addComponent(cell);
				row.addComponent(cell);
				i++;
			}
			vGroup.addGroup(row);
			rows.add(cells.clone());
		}
	}
	private AuthorsPane authorsPane = new AuthorsPane();
	private ImageIcon plusIcon, minusIcon;
	public AuthorSearchPane() {
		executor = Executors.newSingleThreadExecutor();
		popupFactory = PopupFactory.getSharedInstance();
		ResourceManager resourceManager = Services.getResourceManager();
		plusIcon = resourceManager.loadImageIcon("plus.png");
		minusIcon = resourceManager.loadImageIcon("minus.png");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel headerPane = new JPanel();
		headerPane.add(new JLabel("Please enter your search terms below."));
		headerPane.add(new HelpMarker(
				Services.getResourceManager().
				loadStrings("strings.xml").get("AuthorSearchPane.help")));
		add(headerPane);
		
		add(authorsPane);
		add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Services.getBrowser().go(new SearchResultsPage(getAuthors()));
			}
		});
	}
	public List<String> getAuthors() {
		List<String> authors = new ArrayList<String>();
		for(JTextField field : authorFields)
			authors.add(field.getText());
		return authors;
	}
}
