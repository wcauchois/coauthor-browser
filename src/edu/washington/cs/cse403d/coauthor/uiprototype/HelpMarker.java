package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

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
			public void mouseClicked(MouseEvent evt) {
				setIcon(icon);
				JOptionPane.showMessageDialog(null, "To search for an author, begin typing there name into the space below \n" +
						"and select their name when it appears.  Add or remove co-authors as needed \n" +
						"and click 'Search' when you have finished entering your information.", "Help", 0, icon);
			}
			public void mouseExited(MouseEvent evt) {
				setIcon(icon);
			}
		});
	}
}