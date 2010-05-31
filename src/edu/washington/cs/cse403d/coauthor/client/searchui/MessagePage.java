package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import edu.washington.cs.cse403d.coauthor.client.ResourceManager;
import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.Fonts;

/**
 * A page analogous to a JOptionPane in conventional Swing applications. This page
 * will display a message, accompanied by some descriptive text and optionally an
 * icon (currently three types are supported: error, warning, and info) and/or some
 * buttons (some combination of "OK", "Cancel", and "Go back"). 
 * @author William Cauchois
 */
public class MessagePage extends BrowserPage {
	private String title;
	@Override
	public String getTitle() {
		return title;
	}
	
	// This just sets several properties of JTextArea to make it suitable for our
	// needs (as a read-only word-wrapping text container).
	private class DetailsText extends JTextArea {
		public DetailsText(String theText) {
			setText(theText);
			setLineWrap(true);
			setWrapStyleWord(true);
			setEditable(false);
			setBorder(null);
			setBackground(null);
		}
	}
	
	// Enclose a component in a JPanel with a FlowLayout with the specified
	// alignment.
	private Component encloseInFlowLayout(Component target, int align) {
		JPanel container = new JPanel();
		container.setBackground(null);
		container.setLayout(new FlowLayout(align));
		container.add(target);
		return container;
	}
	
	// Enumeration values for the type of the MessagePane.
	public static final int
			ERROR = 1, // A red X indicating an error has occurred
			INFO = 2, // An "i" denoting informational content
			WARNING = 3; // A yellow triangle indicating a warning
	// Bit-flags for the buttons that may be used with a MessagePane.
	public static final int
			GO_BACK = 1 << 0,
			OK = 1 << 1,
			CANCEL = 1 << 2;
	
	/**
	 * Construct a MessagePane with the specified title and descriptive text,
	 * no icon or button.
	 * @see #MessagePage(int, String, String, int)
	 */
	public MessagePage(String title, String details) {
		this(0, title, details, 0);
	}
	/**
	 * Construct a MessagePane with the specified title and descriptive text,
	 * and the icon associated with the type parameter (which must be one of
	 * ERROR, INFO, WARNING, or 0).
	 * @see #MessagePage(int, String, String, int)
	 */
	public MessagePage(int type, String title, String details) {
		this(type, title, details, 0);
	}
	/**
	 * Construct a MessagePane with the specified title, descriptive text,
	 * icon associated with the type parameter (which must be one of ERROR,
	 * INFO, WARNING, or 0), and one or more buttons. Use an ActionListener to
	 * determine when one of the buttons has been pressed.
	 * @see #addActionListener(ActionListener)
	 * @param type one of ERROR, INFO, WARNING, or 0.
	 * @param buttons some combination of GO_BACK, OK, or CANCEL bitwise OR'd together. 
	 */
	public MessagePage(int type, String title, String details, int buttons) {
		this.title = title;
		
		ImageIcon icon = null;
		ResourceManager resMan = Services.getResourceManager();
		if(type == ERROR) 			icon = resMan.loadImageIcon("error.png");
		else if(type == INFO) 		icon = resMan.loadImageIcon("info.png");
		else if(type == WARNING) 	icon = resMan.loadImageIcon("warning.png");
		
		setLayout(new BorderLayout());
		JPanel messagePane = new JPanel();
		messagePane.setLayout(new BoxLayout(messagePane, BoxLayout.Y_AXIS));
		messagePane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)));
		messagePane.setBackground(Color.WHITE);
		messagePane.setPreferredSize(new Dimension(400, 200));
		add(Box.createVerticalStrut(75), BorderLayout.NORTH);
		add(messagePane, BorderLayout.CENTER);
		
		JLabel titleLabel = new JLabel(title, icon, SwingConstants.LEFT);
		titleLabel.setFont(Fonts.getHeaderFont());
		messagePane.add(encloseInFlowLayout(titleLabel, FlowLayout.LEFT));
		
		JTextArea detailsText = new DetailsText(details);
		messagePane.add(detailsText);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(null);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		List<JButton> buttonList = new ArrayList<JButton>();
		if((buttons & GO_BACK) == GO_BACK) 	buttonList.add(new JButton("Go back"));
		if((buttons & CANCEL) == CANCEL) 	buttonList.add(new JButton("Cancel"));
		if((buttons & OK) == OK) 			buttonList.add(new JButton("OK"));
		for(JButton button : buttonList) {
			button.addActionListener(dispatcherListener);
			buttonsPanel.add(button);
		}
		if(buttonList.size() > 0)
			messagePane.add(buttonsPanel);
		
	}
	private class DispatcherListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			for(ActionListener l : actionListeners) {
				l.actionPerformed(evt);
			}
		}
		
	}
	private DispatcherListener dispatcherListener = new DispatcherListener();;
	private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	/**
	 * Add an ActionListener to this MessagePane that will be notified when any
	 * one of the buttons is pressed. This is equivalent to adding l to each
	 * JButton individually (i.e. the ActionEvent is passed directly).
	 * @param l the action listener.
	 */
	public void addActionListener(ActionListener l) {
		actionListeners.add(l);
	}
}
