package edu.washington.cs.cse403d.coauthor.client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.Naming;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.washington.cs.cse403d.coauthor.client.browser.BrowserFrame;
import edu.washington.cs.cse403d.coauthor.client.searchui.SearchPage;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;

/**
 * The entry point for the application -- main() initializes and shows an instance
 * of this class. MainFrame encapsulates things like initializing the base services,
 * and creating a menu.
 * @see Services
 * @author William Cauchois
 *
 */
public class MainFrame extends BrowserFrame {
	
	public JMenuBar createMenuBar() {
		// TODO(wcauchois): the menu bar is vastly out of sync with the features we've been implementing.
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		final MainFrame mainFrame = this;
		
		menuBar = new JMenuBar();

		/************** File **************/
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("New Search for Author");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				MainFrame.this.go(new SearchPage(SearchPage.AUTHOR_SEARCH));
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("New Search for Article");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				MainFrame.this.go(new SearchPage(SearchPage.ARTICLE_SEARCH));
			}
		});
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Quit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(1);
			}
		});
		menu.add(menuItem);
		
		/************** Tools **************/
		menu = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Preferences");
		menu.add(menuItem);
		
		/*************** Help **************/
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("User Manual");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("About");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JOptionPane.showMessageDialog(mainFrame, 
						"Author Network\nConstant Time Algorithms, Inc.",
						"About",
						JOptionPane.PLAIN_MESSAGE);
			}
		});
		menu.add(menuItem);
		
		return menuBar;
	}
	
	 private static final String HOSTNAME = "attu2.cs.washington.edu";
	public MainFrame() {
		super(new SearchPage());
		setTitle("Author Network");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(createMenuBar());
	}
	
	public static void main(String[] args) {
		try {
			CoauthorDataServiceInterface cdsi = (CoauthorDataServiceInterface)Naming.lookup(
					"rmi://" + HOSTNAME + "/" + CoauthorDataServiceInterface.SERVICE_NAME);
			if(false) { // Disable prototype cached CDSI
				Services.provideCoauthorDataServiceInterface(
						new CachedCoauthorData(cdsi));
			} else {
				Services.provideCoauthorDataServiceInterface(cdsi);
			}
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null,
				"Couldn't connect to data service (" + e.getMessage() + ")");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		Services.provideResourceManager(new ResourceManager("/"));
		MainFrame mainFrame = new MainFrame();
		
		//restrict resizing
		mainFrame.setMinimumSize(new Dimension(480, 600));
		mainFrame.setVisible(true);
	}
}
