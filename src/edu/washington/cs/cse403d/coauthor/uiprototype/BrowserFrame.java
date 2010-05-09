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

/**
 * A frame that displays one BrowserPage at a time, and includes controls for
 * going forwards and backwards in history (like a web browser). Instantiating
 * an instance of this class will provide an instance of a Browser to the
 * BrowserService, so that subsequent calls to BrowserService.getBrowser() will
 * return the most recently instantiated BrowserFrame.
 * @see Services
 * @author William Cauchois
 */
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
		Services.provideBrowser(this);
		initialPage.onEnter(null);
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
	public void go(BrowserPage page) {
		history.getCurrent().onExit(page);
		page.onEnter(history.push(page));
		update();
	}
	public boolean canGoForward() {
		return history.hasNext();
	}
	public boolean canGoBack() {
		return history.hasPrevious();
	}
	public void goForward() {
		BrowserPage old = history.getCurrent();
		old.onExit(history.forward());
		history.getCurrent().onEnter(old);
		update();
	}
	public void goBack() {
		BrowserPage old = history.getCurrent();
		old.onExit(history.back());
		history.getCurrent().onEnter(old);
		update();
	}
}