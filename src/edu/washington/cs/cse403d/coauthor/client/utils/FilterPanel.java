package edu.washington.cs.cse403d.coauthor.client.utils;	

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.ResourceManager;

/**
 * Created a filter bar below the coauthor/publication list in
 * the result screen.
 * 
 * @author Kevin Bang
 *
 */
public class FilterPanel extends JPanel {
	private static final long serialVersionUID = -9090832682261141465L;
	private FilterField filterField;
	private JPanel filterPanel;
	private String theAuthor;
	private JList list;
	private String author2 = null;
	private static final String[] CSS_RULES = new String[] {
		"body { background-color: #eeeeff; width: 250px }",
		"body { font-family: Dialog; font-size: 12 }",
		"ul { margin-left: 15px }"
	};
	
	/**
	 * Constructor
	 * 
	 * @param flag determines whether the filter is for coauthor or publications 
	 * @param defaultListModel listModel of the list to be filtered
	 * @param dataList list of data containing all the items in original list
	 * @param list original JList to be filtered
	 */
	public FilterPanel(JList list, String author) {
		this.list = list;
		this.theAuthor = author;
		createFilterPanel();
		add(filterPanel);
	}
	
	public FilterPanel(JList list, String author1, String author2)
	{
		this.list = list;
		this.theAuthor = author1;
		this.author2 = author2;
		createFilterPanel();
		add(filterPanel);
	}

	/**
	 * Create the filter panel
	 */
	private void createFilterPanel() {
		filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
				
		filterField = new FilterField(list, theAuthor);
		if(theAuthor != null)
			filterField.setPreferredSize(new Dimension(300, 0));
		else
			filterField.setPreferredSize(new Dimension(340, 0));
		filterPanel.add(filterField);
		
		filterPanel.add(Box.createHorizontalStrut(5));
		filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
		filterPanel.add(Box.createHorizontalStrut(5));
		
		if(theAuthor != null) {
			filterPanel.add(new VisualizeButton(Services.getResourceManager().loadStrings("strings.xml").get(
			"Visualizer.help")));
			filterPanel.add(Box.createHorizontalStrut(5));
			filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
			filterPanel.add(Box.createHorizontalStrut(5));
		}
		
		if(theAuthor != null)
			filterPanel.add(new HelpMarker(Services.getResourceManager().
				loadStrings("strings.xml").get("CoauthorFilter.help")));
		else
			filterPanel.add(new HelpMarker(Services.getResourceManager().
				loadStrings("strings.xml").get("PublicationFilter.help")));
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	}
	
	private class VisualizeButton extends JLabel {
		private static final long serialVersionUID = 8024005710286098259L;
		private ImageIcon visualizeButton, hoverButton;
		final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
		final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		private PopupFactory popupFactory;
		private Popup popup = null;
		private ToolTip toolTip;
		ResourceManager resourceManager;
		
		public VisualizeButton(String tooltip) {
			popupFactory = PopupFactory.getSharedInstance();
			resourceManager = Services.getResourceManager();
			toolTip = new ToolTip(tooltip);
			visualizeButton = resourceManager.loadImageIcon("VisualizeButton.png");
			hoverButton = resourceManager.loadImageIcon("VisualizeButtonHover.png");
			setIcon(visualizeButton);
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (author2 == null)
						Services.getGraphVizManager().showGraphFor(theAuthor);
					else
						try {
							Services.getGraphVizManager().showGraphFor(theAuthor, author2);
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
				public void mouseEntered(MouseEvent evt) {
					setCursor(handCursor);
					setIcon(hoverButton);
					Point loc = VisualizeButton.this.getLocationOnScreen();
					popup = popupFactory.getPopup(VisualizeButton.this, toolTip,
								loc.x + VisualizeButton.this.getWidth(), loc.y);
					popup.show();
				}
				@Override
				public void mouseExited(MouseEvent evt) {
					setCursor(defaultCursor);
					setIcon(visualizeButton);
					popup.hide();
					popup = null;
				}
				
			});
		}		
	}
	
	public JList getList() {
		return filterField.getList();
	}
	
	private class ToolTip extends JPanel {
		private static final long serialVersionUID = -1589166890467253186L;

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
}