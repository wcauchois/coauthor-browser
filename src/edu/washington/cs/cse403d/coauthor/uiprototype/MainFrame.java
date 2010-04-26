package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class MainFrame extends BrowserFrame {
	
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		final MainFrame mainFrame = this;
		
		menuBar = new JMenuBar();

		/************** File **************/
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Start A New Search");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Save Current Search");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Load Saved Search");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menuItem = new JMenuItem("Load Last Search");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Quit");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(1);
			}
		});
		menu.add(menuItem);
		

		/************** Search **************/
		menu = new JMenu("Search");
		menu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Author Search");
		//need to add actionListner
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Services.getBrowser().go(new SearchPage());				
			}
		});
		
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Article Search");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Graph Search");
		menu.add(menuItem);
		
		/************** Tools **************/
		menu = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Preferences");
		menu.add(menuItem);
		
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("User Manual");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("About");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JOptionPane.showMessageDialog(mainFrame, 
						"Co-Author Browser v.0.9\n"
						+ "by CTA Inc.",
						"About",
						JOptionPane.PLAIN_MESSAGE);
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
		Services.provideResourceManager(new ResourceManager("/"));
		MainFrame mainFrame = new MainFrame();
		mainFrame.setSize(480, 600);
		mainFrame.setVisible(true);
	}
}
