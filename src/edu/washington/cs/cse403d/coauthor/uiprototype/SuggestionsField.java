package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Implements a Swing text field that may display a list of auto-complete
 * suggestions to the user in a box below the text field.
 */
public abstract class SuggestionsField extends JTextField {
	// The list of suggestions is put inside a scrolling pane
	private JScrollPane suggestionsPane;
	// A widget to display the list of suggestions (which is stored inside
	// the model for this JList).
	private JList suggestionsList = new JList();
	// The popup used to display the suggestionsList below the actual text
	// field -- if null, the suggestions are currently hidden
	private Popup suggestionsPopup = null;
	private PopupFactory popupFactory;
	
	/**
	 * Return a list of suggestions for the provided text. This method
	 * will be invoked every time the content of the text field is
	 * modified by the user.
	 */
	protected abstract List<String> getSuggestions(String text);
	
	public void showSuggestions() {
		if(suggestionsPopup == null
				&& suggestionsList.getModel().getSize() > 0
				&& isDisplayable()) {
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

	public void updateSuggestions() {
		List<String> suggestions = getSuggestions(getText());
		suggestionsList.setListData(suggestions.toArray());
		showSuggestions();
	}
	
	private class KeyboardShortcuts extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent evt) {
			boolean scrollDown = evt.getKeyCode() == KeyEvent.VK_DOWN;
			boolean scrollUp = evt.getKeyCode() == KeyEvent.VK_UP;
			if(scrollUp || scrollDown) {
				// Scroll up or down using the arrow keys
				int newIndex = suggestionsList.getSelectedIndex() + (scrollDown ? 1 : -1);
				suggestionsList.setSelectedIndex(newIndex);
				suggestionsList.ensureIndexIsVisible(newIndex);
			} else if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
				// Select a suggestion by pressing enter
				if(suggestionsPopup != null && !suggestionsList.isSelectionEmpty()) {
					setText((String)suggestionsList.getSelectedValue());
					hideSuggestions();
				}
			} else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// Hide suggestions by pressing escape
				hideSuggestions();
			}
		}
	}
	
	public SuggestionsField() {
		popupFactory = PopupFactory.getSharedInstance();
		getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent evt) { updateSuggestions(); }
			public void insertUpdate(DocumentEvent evt) { updateSuggestions(); }
			public void removeUpdate(DocumentEvent evt) { updateSuggestions(); }
		});
		addKeyListener(new KeyboardShortcuts());
		addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent evt) {
				hideSuggestions();
			}
			@Override public void focusGained(FocusEvent evt) {
				updateSuggestions();
			}
		});
		
		suggestionsPane = new JScrollPane(suggestionsList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		setFont(getFont().deriveFont(Font.BOLD, 14));
		setPreferredSize(new Dimension(150, 24));
		suggestionsPane.setPreferredSize(new Dimension(200, 80));
	}
}
