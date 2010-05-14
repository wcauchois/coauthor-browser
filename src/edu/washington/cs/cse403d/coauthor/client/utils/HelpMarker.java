package edu.washington.cs.cse403d.coauthor.client.utils;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import edu.washington.cs.cse403d.coauthor.client.ResourceManager;
import edu.washington.cs.cse403d.coauthor.client.Services;

/**
 * This component displays a question mark which, when the user hovers over it,
 * displays some explanatory text (presumably about a nearby component). 
 * @author William Cauchois
 */
public class HelpMarker extends JLabel {
	private ImageIcon icon, hoverIcon;
	private PopupFactory popupFactory;
	private Popup popup = null;
	private ToolTip toolTip;
	private static final String[] CSS_RULES = new String[] {
		"body { background-color: #eeeeff; width: 250px }",
		"body { font-family: Dialog; font-size: 12 }",
		"ul { margin-left: 15px }"
	};
	private class ToolTip extends JPanel {
		public ToolTip(String htmlContent) {
			JTextPane text = new JTextPane();
			HTMLEditorKit kit = new HTMLEditorKit();
			text.setEditorKit(kit);
			HTMLDocument doc = (HTMLDocument)text.getDocument();
			for(String rule : CSS_RULES)
				kit.getStyleSheet().addRule(rule);
			try {
				doc.setInnerHTML(doc.getRootElements()[0],
						"<body>" + htmlContent + "</body>");
			} catch(Throwable t) {
				System.err.println("HelpMarker.ToolTip: Failed to parse HTML");
				return;
			}
			add(text);
			text.setBorder(null); // There's an annoying white border by default
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
	}
	/**
	 * Initializes the help marker with some text to be displayed when the user
	 * hovers over the question mark.
	 * @param tooltip the tooltip, with optional HTML formatting.
	 */
	public HelpMarker(String tooltip) {
		popupFactory = PopupFactory.getSharedInstance();
		ResourceManager resourceManager = Services.getResourceManager();
		icon = resourceManager.loadImageIcon("questionmark.gif");
		hoverIcon = resourceManager.loadImageIcon("questionmark_hover.gif");
		toolTip = new ToolTip(tooltip);
		
		setIcon(icon);
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				setIcon(hoverIcon);
				Point loc = HelpMarker.this.getLocationOnScreen();
				popup = popupFactory.getPopup(HelpMarker.this, toolTip,
							loc.x + HelpMarker.this.getWidth(), loc.y);
				popup.show();
			}
			public void mouseExited(MouseEvent evt) {
				setIcon(icon);
				popup.hide();
				popup = null;
			}
		});
	}
}