package edu.washington.cs.cse403d.coauthor.client.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.List;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;


/**
 * A widget that looks like a hyper-link.
 * @author Kevin Bang
 */
public class HyperLinkButton extends JLabel {
	public HyperLinkButton(String text) {
		super(text);
		final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
		final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		final Border emptyBorder = BorderFactory.createEmptyBorder();
		final Border underline = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLUE);
		setForeground(Color.BLUE);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent evt) {
				setCursor(handCursor);
				setBorder(underline);
			}
			@Override
			public void mouseExited(MouseEvent evt) {
				setCursor(defaultCursor);
				setBorder(emptyBorder);
			}
			@Override
			public void mouseClicked(MouseEvent evt) {
				ActionEvent actionEvt = new ActionEvent(this, 0, getText());
				for(ActionListener l : listeners)
					l.actionPerformed(actionEvt);
			}
		});
	}
	
	private List<ActionListener> listeners = new ArrayList<ActionListener>();
	/**
	 * Add an ActionListener to be notified when the user clicks on this
	 * HyperLinkButton.
	 */
	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}
}