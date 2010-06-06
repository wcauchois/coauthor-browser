package edu.washington.cs.cse403d.coauthor.client.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;

/**
 * When attached to a list, this mouse listener will pop up the specified
 * context menu when the user presses right click.
 * @author William Cauchois
 */
public class ListPopupMouseListener extends MouseAdapter {
	private static final int RIGHT_BUTTON = MouseEvent.BUTTON3;
	private JPopupMenu popupMenu;
	
	public ListPopupMouseListener(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}
	
	@Override
	public void mouseClicked(MouseEvent evt) {
		JList theList = (JList)evt.getComponent();
		if(evt.getButton() == RIGHT_BUTTON) {
			int indexToSelect = theList.locationToIndex(evt.getPoint());
			theList.setSelectedIndex(indexToSelect);
			popupMenu.show(theList, evt.getX(), evt.getY());
		}
	}
}
