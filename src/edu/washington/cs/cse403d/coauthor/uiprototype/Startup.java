package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

public class Startup extends BrowserFrame {
	
	public JMenuBar initMenu() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		
		//create the menu bar
		menuBar = new JMenuBar();
		
		//Build the first menu, File
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Start A New Search");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		//need to add actinoLister		
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Save Current Search");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		//need to add actionListner
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Load Saved Search");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		//need to add actionListner
		menu.add(menuItem);
		menuItem = new JMenuItem("Load Last Search");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		//need to add actionListner
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Close");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(1);
			}
		});
		menu.add(menuItem);
		
		
		//Build the second menu, Search
		menu = new JMenu("Search");
		menu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Author Search");
		//need to add actionListner
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Article Search");
		//need to add actionListner
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Graph Search");
		//need to add actionListner
		menu.add(menuItem);
		
		
		
		//Build the third menu, Tools
		menu = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Preference");
		//need to add actionListner
		menu.add(menuItem);
		
		
		
		//Build the fourth menu, Help
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("User Manual");
		//need to add actionListner
		menu.add(menuItem);
		
		menuItem = new JMenuItem("About");
		//need to add actionListner
		menu.add(menuItem);
		
		
		return menuBar;
	}
		
	public Startup() {
		super(new MainPage());
		setTitle("Co-author Browser");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(initMenu());
	}
	
	public static void main(String[] args) {
		Startup frame = new Startup();
		frame.setSize(480, 500);
		frame.setVisible(true);
	}
}
