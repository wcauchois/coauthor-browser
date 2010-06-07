package edu.washington.cs.cse403d.coauthor.client.utils;

import java.awt.Dimension;

import javax.swing.JTextArea;

/**
 * A widget derived from JTextArea that functions like a label with line wrapping. 
 * @author William Cauchois
 */
public class LineWrappedLabel extends JTextArea {
	private static final long serialVersionUID = -2521508440088589555L;
	public LineWrappedLabel(String text) {
		setText(text);
		
		setLineWrap(true);
		setWrapStyleWord(true);
		setEditable(false);
		setBorder(null);
		setBackground(null);
	}
	/**
	 * Approximate the height (in pixels) of this LineWrappedLabel when it is
	 * set to be the specified width. This method is provided since a bug in
	 * Swing prevents getPreferredSize() from returning the right height by
	 * default. 
	 */
	public int getActualHeight(int width) {
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4446522
		setSize(new Dimension(width, 0));
		getUI().getRootView(this).setSize(this.getWidth(), 0f);
		return getPreferredSize().height;
	}
}
