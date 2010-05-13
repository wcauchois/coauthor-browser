package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;


/*
 * Fancy JLabel instances.
 */
public class ClickableJLabel extends JLabel {
	private String text;
	
	public ClickableJLabel(String text) {
		this.text = text;
		final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
		final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		final String defaultText = text;
		final Border noLine = BorderFactory.createEmptyBorder();
		final Border underline = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLUE);
		
		setText(defaultText);
		setForeground(Color.BLUE);		
		
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				setCursor(handCursor);
				setBorder(underline);
			}
			public void mouseExited(MouseEvent evt) {
				setCursor(defaultCursor);
				setBorder(noLine);
			}
		});
	}
	
	public ClickableJLabel(String text, boolean flag) {
		super(text);
	}
	
	public String getOriginalText() {
		return text;
	}
}