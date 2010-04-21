package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BrowserFrame extends JFrame implements Browser {
	private BrowserHistory history;
	private JPanel pagePane = new JPanel();
	private JPanel navPane = new JPanel();
	private CrumbBar crumbBar = new CrumbBar();
	private class BackAction extends AbstractAction {
		public BackAction() {
			putValue(NAME, "Back");
			putValue("enabled", false);
		}
		@Override public void actionPerformed(ActionEvent evt) {
			goBack();
		}
		public void update() {
			putValue("enabled", canGoBack());
		}
	}
	private class ForwardAction extends AbstractAction {
		public ForwardAction() {
			putValue(NAME, "Forward");
			putValue("enabled", false);
		}
		@Override public void actionPerformed(ActionEvent evt) {
			goForward();
		}
		public void update() {
			putValue("enabled", canGoForward());
		}
	}
	private ForwardAction forwardAction = new ForwardAction();
	private BackAction backAction = new BackAction(); // backdoor action hehe
	public BrowserFrame(BrowserPage initialPage) {
		history = new BrowserHistory(initialPage);
		navPane.add(new JButton(backAction));
		navPane.add(new JButton(forwardAction));
		navPane.add(new JLabel("Crumbs: "));
		navPane.add(crumbBar);
		navPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(navPane, BorderLayout.NORTH);
		getContentPane().add(pagePane, BorderLayout.CENTER);
		update();
	}
	private void update() {
		pagePane.removeAll();
		pagePane.add(history.getCurrent());
		crumbBar.update(history);
		backAction.update();
		forwardAction.update();
		validate();
		pagePane.repaint();
	}
	public boolean go(BrowserPage page, boolean useHistory) {
		if(useHistory) {
			if(history.contains(page)) {
				history.setCurrent(page);
				update();
				return true;
			}
		}
		go(page);
		return false;
	}
	public void go(BrowserPage page) {
		history.push(page);
		update();
	}
	public boolean canGoForward() {
		return history.hasNext();
	}
	public boolean canGoBack() {
		return history.hasPrevious();
	}
	public void goForward() {
		history.forward();
		update();
	}
	public void goBack() { 
		history.back();
		update();
	}
}