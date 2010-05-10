package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;


/*
 * Fancy JLabel instances.
 */
public class AdvLabel extends JLabel {
	private String text;
	
	public AdvLabel(String text) {
		this.text = text;
		final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
		final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		final String underLinedText = "<html><u>" + text + "</u></html>";
		final String defaultText = text;
		
		setText(defaultText);
		setForeground(Color.BLUE);		
		
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				setCursor(handCursor);
				setText(underLinedText);
			}
			public void mouseExited(MouseEvent evt) {
				setCursor(defaultCursor);
				setText(defaultText);
			}
		});
	}
	
	public AdvLabel(String text, boolean flag) {
		super(text);
	}
	
	public String getOriginalText() {
		return text;
	}
}