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
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		menuItem = new JMenuItem("Quit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(1);
			}
		});
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

		return menuBar;
	}
	public MainFrame() {
		super(new StartPage());
		BrowserService.provideBrowser(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Coauthor Browser");
		setJMenuBar(createMenuBar());
	}
	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch(Exception e) { }
		MainFrame frame = new MainFrame();
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}