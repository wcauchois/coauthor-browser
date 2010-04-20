package edu.washington.cs.cse403d.coauthor.uiprototype;

import javax.swing.JFrame;

public class BrowserFrame extends JFrame implements Browser {
	private BrowserHistory history;
	public BrowserFrame(BrowserPage initialPage) {
		history = new BrowserHistory(initialPage);
		updateContent();
	}
	private void updateContent() {
		setContentPane(history.getCurrent());
		validate();
	}
	public void go(BrowserPage page) {
		history.push(page);
		updateContent();
	}
	public boolean canGoForward() {
		return history.hasNext();
	}
	public boolean canGoBack() {
		return history.hasPrevious();
	}
	public void goForward() {
		history.forward();
		updateContent();
	}
	public void goBack() { 
		history.back();
		updateContent();
	}
}