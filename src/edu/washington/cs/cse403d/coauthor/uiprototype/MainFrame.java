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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

public class MainFrame extends BrowserFrame {
	
	public JMenuBar createMenuBar() {
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
		//need to add actionListener
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
		
		menuItem = new JMenuItem("Quit");
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
		
		menu = new JMenu("Go");
		menu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Back");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(canGoBack())
					goBack();
				else
					JOptionPane.showMessageDialog(null, "You can't go back");
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Forward");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(canGoForward())
					goForward();
				else
					JOptionPane.showMessageDialog(null, "You can't go forward");
			}
		});
		menu.add(menuItem);
		
		//Build the third menu, Tools
		menu = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Preferences");
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
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				createFrame();
			}
		});
		menu.add(menuItem);
		
		
		return menuBar;
	}
		
	public MainFrame() {
		super(new StartPage());
		setTitle("Co-author Browser");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(createMenuBar());
	}
	
	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setSize(480, 500);
		frame.setVisible(true);
	}
	
	//creating internal frame
	protected void createFrame() {
        MyInternalFrame frame = new MyInternalFrame("Test"); //Frame title
        frame.setVisible(true); 
        this.add(frame);  //add internal frame to container
        frame.add(new JLabel("Test"));  //frame content
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
}
