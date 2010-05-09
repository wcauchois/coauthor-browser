package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
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
 * suggestions to the user in a box below the text field. When the user clicks
 * on a suggestion or selects one using the keyboard, the text of the suggestion
 * replaces the text of the field.
 * 
 * Derived classes should override the getSuggestions() method.
 */
public abstract class SuggestionsField extends JTextField {
	private static final int DEFAULT_POPUP_HEIGHT = 80;
	
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
	
	private boolean shouldShowSuggestions() {
		if(!isDisplayable()) return false;
		if(suggestionsList.getModel().getSize() == 0)
			return false;
		if(suggestionsList.getModel().getSize() == 1) {
			// Don't show suggestions if the text of the field equals the single
			// suggestion (the user has probably already selected the suggestion).
			if(getText().equalsIgnoreCase(
					(String)suggestionsList.getModel().getElementAt(0)))
				return false;
		}
		return true;
	}
	public void showSuggestions() {
		if(shouldShowSuggestions() && suggestionsPopup == null) {
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

	public void updateSuggestions() {
		List<String> suggestions = getSuggestions(getText());
		suggestionsList.setListData(suggestions.toArray());
		Rectangle bounds = suggestionsList.getCellBounds(0, suggestions.size() - 1);
		int preferredHeight = Math.min(DEFAULT_POPUP_HEIGHT,
				(bounds != null) ? (bounds.height + 4) : Integer.MAX_VALUE);
		suggestionsPane.setPreferredSize(new Dimension(
				suggestionsPane.getPreferredSize().width,
				preferredHeight));
		hideSuggestions();
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
				if(newIndex < 0) newIndex = 0;
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
		suggestionsPane.setPreferredSize(new Dimension(226, DEFAULT_POPUP_HEIGHT));
	}
}
