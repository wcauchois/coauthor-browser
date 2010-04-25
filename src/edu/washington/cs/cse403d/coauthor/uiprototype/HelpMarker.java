package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * This component displays a question mark which, when the user hovers over it,
 * displays some explanatory text (presumably about a nearby component). 
 * @author William Cauchois
 */
public class HelpMarker extends JLabel {
	private ImageIcon icon, hoverIcon;
	public HelpMarker(String helpText) {
		ResourceManager resourceManager = Services.getResourceManager();
		icon = resourceManager.loadImageIcon("questionmark.gif");
		hoverIcon = resourceManager.loadImageIcon("questionmark_hover.gif");
		
		setIcon(icon);
		setToolTipText(helpText);
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				setIcon(hoverIcon);
			}
			public void mouseExited(MouseEvent evt) {
				setIcon(icon);
			}
		});
	}
}