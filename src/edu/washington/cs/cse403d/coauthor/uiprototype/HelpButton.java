package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * This component displays a question mark which, when the user hovers over it,
 * displays some explanatory text about a nearby component. 
 * @author William Cauchois
 */
public class HelpButton extends JLabel {
	private static boolean resourcesLoaded = false;
	private static ImageIcon icon, hoverIcon;
	public HelpButton(String helpText) {
		if(!resourcesLoaded) {
			icon = loadImageIcon("images/questionmark.gif", "Question Mark");
			hoverIcon = loadImageIcon("images/questionmark_hover.gif", "Question Mark");
			resourcesLoaded = true;
		}
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
	private ImageIcon loadImageIcon(String path, String description) {
		URL url = getClass().getResource(path);
		if (url != null) {
			return new ImageIcon(url, description);
		} else {
			System.err.println("No such file exists: " + path);
			return null;
		}
	}	
}
