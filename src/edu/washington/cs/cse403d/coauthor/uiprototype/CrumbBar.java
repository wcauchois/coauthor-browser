package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A widget that presents a series of buttons to the user representing pages
 * that are previous in the history AND upwards in the hierarchy. The hierarchy
 * is implied by the current BrowserPage's getCrumbs() method.
 * @see BrowserPage#getCrumbs()
 * @author William Cauchois
 */
public class CrumbBar extends JPanel {
	private static class HistoryAction extends AbstractAction {
		private BrowserPage page;
		@Override public void actionPerformed(ActionEvent evt) {
			BrowserService.getBrowser().go(page);
		}
		public HistoryAction(BrowserPage page) {
			this.page = page;
			putValue(NAME, page.getTitle() + " >");
		}
	}
	/**
	 * Update the set of buttons in this CrumbBar.
	 * @param history a BrowserHistory object from which to derive the crumbs.
	 */
	@SuppressWarnings("unchecked")
	public void update(BrowserHistory history) {
		List<BrowserPage> crumbs = new ArrayList<BrowserPage>();
		Iterator<BrowserPage> iter = history.backwardsIterator();
		crumbs.add(iter.next());
		Class[] classes = history.getCurrent().getCrumbs();
		if(classes != null) {
			int pos = 0;
			while(iter.hasNext() && pos < classes.length) {
				BrowserPage page = iter.next();
				if(page.getClass().equals(classes[pos])) {
					crumbs.add(page);
					pos++;
				}
			}
		}
		removeAll();
		for(int i = crumbs.size() - 1; i >= 0; i--)
			add(new JButton(new HistoryAction(crumbs.get(i))));	
		validate();
	}
}
