package edu.washington.cs.cse403d.coauthor.client.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	private JScrollPane listPane;
	// A widget to display the list of suggestions (which is stored inside
	// the model for this JList).
	private JList list = new JList();
	// The popup used to display the suggestionsList below the actual text
	// field -- if null, the suggestions are currently hidden
	private Popup popup = null;
	private PopupFactory popupFactory;
	
	/**
	 * Return a list of suggestions for the provided text. This method
	 * will be invoked every time the content of the text field is
	 * modified by the user.
	 */
	protected abstract List<String> getSuggestions(String text);
	
	private boolean shouldShowSuggestions() {
		if(!isDisplayable()) return false;
		if(list.getModel().getSize() == 0)
			return false;
		if(list.getModel().getSize() == 1) {
			// Don't show suggestions if the text of the field equals the single
			// suggestion (the user has probably already selected the suggestion).
			if(getText().equalsIgnoreCase(
					(String)list.getModel().getElementAt(0)))
				return false;
		}
		return true;
	}
	public void showSuggestions() {
		if(shouldShowSuggestions() && popup == null) {
			assert list.getModel().getSize() > 0;

			// Resize the suggestions pane to better fit its content (that is, if it
			// has only a few items we make it smaller).
			Rectangle bounds = null;
			try {
				bounds = list.getCellBounds(0, list.getModel().getSize() - 1);
			} catch(ArrayIndexOutOfBoundsException e) {
				// HACK(wcauchois): this was getting thrown randomly and I couldn't figure out why
			}
					
			int preferredHeight = Math.min(DEFAULT_POPUP_HEIGHT,
					(bounds != null) ? (bounds.height + 4) : Integer.MAX_VALUE);
			listPane.setPreferredSize(new Dimension(
					listPane.getPreferredSize().width,
					preferredHeight));
			
			Point location = getLocationOnScreen();
			int offsetY = getSize().height;
			popup = popupFactory.getPopup(
					null, listPane, location.x, location.y + offsetY);
			
			popup.show();
		}
	}
	
	public void hideSuggestions() {
		if(popup != null) {
			popup.hide();
			popup = null;
		}
	}

	public void updateSuggestions() {
		List<String> suggestions = getSuggestions(getText());
		list.setListData(suggestions.toArray());
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
				int newIndex = list.getSelectedIndex() + (scrollDown ? 1 : -1);
				if(newIndex < 0) newIndex = 0;
				list.setSelectedIndex(newIndex);
				list.ensureIndexIsVisible(newIndex);
			} else if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
				// Select a suggestion by pressing enter
				if(popup != null) {
					if(!list.isSelectionEmpty()) {
						setText((String)list.getSelectedValue());
						hideSuggestions();
					}
				} else
					onSubmit();
			} else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// Hide suggestions by pressing escape
				hideSuggestions();
			}
		}
	}
	
	/**
	 * Called when the user presses enter (and the suggestions popup is not
	 * visible). This could be used to, for example, submit a form when the
	 * user hits enter.
	 */
	protected void onSubmit() { }
	
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
		
		listPane = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				setText((String)list.getModel().getElementAt(index));
			}
		});
		
		setFont(getFont().deriveFont(Font.BOLD, 14));
		setPreferredSize(new Dimension(150, 24));
		listPane.setPreferredSize(new Dimension(226, DEFAULT_POPUP_HEIGHT));
	}
}
