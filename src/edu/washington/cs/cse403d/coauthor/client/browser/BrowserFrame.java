package edu.washington.cs.cse403d.coauthor.client.browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.washington.cs.cse403d.coauthor.client.Services;

/**
 * A frame that displays one BrowserPage at a time, and includes controls for
 * going forwards and backwards in history (like a web browser). Instantiating
 * an instance of this class will provide an instance of a Browser to the
 * BrowserService, so that subsequent calls to BrowserService.getBrowser() will
 * return the most recently instantiated BrowserFrame.
 * 
 * @see Services
 * @author William Cauchois
 */
public class BrowserFrame extends JFrame implements Browser {
	private static final long serialVersionUID = 2084399509406597681L;

	protected BrowserHistory history;
	private JPanel pagePane = new JPanel();
	private JPanel navPane = new JPanel();
	private JPanel loadingPanel;

	private abstract class NavButton extends JButton implements ActionListener {
		private static final long serialVersionUID = 8896732421593552612L;

		public NavButton(String iconImage) {
			setIcon(Services.getResourceManager().loadImageIcon(iconImage));
			setPreferredSize(new Dimension(30, 30));
			addActionListener(this);
		}
	}

	private class BackButton extends NavButton {
		private static final long serialVersionUID = -6991005901303570281L;

		public BackButton() {
			super("HistoryBack.png");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			goBack();
		}

		public void update() {
			setEnabled(canGoBack());
		}
	}

	private class ForwardButton extends NavButton {
		private static final long serialVersionUID = 2586986138123539508L;

		public ForwardButton() {
			super("HistoryForward.png");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			goForward();
		}

		public void update() {
			setEnabled(canGoForward());
		}
	}

	private ForwardButton forwardButton = new ForwardButton();
	private BackButton backButton = new BackButton();

	public BrowserFrame(BrowserPage initialPage) {
		history = new BrowserHistory(initialPage);
		navPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		navPane.add(backButton);
		navPane.add(forwardButton);
		navPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(navPane, BorderLayout.NORTH);
		getContentPane().add(pagePane, BorderLayout.CENTER);
		Services.provideBrowser(this);
		
		loadingPanel = new JPanel();
		loadingPanel.setLayout(new BoxLayout(loadingPanel, BoxLayout.Y_AXIS));

		JLabel icon = new JLabel(Services.getResourceManager().loadImageIcon("Loading.gif"));
		icon.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel text = new JLabel("Loading...");
		text.setAlignmentX(Component.CENTER_ALIGNMENT);

		loadingPanel.add(icon);
		loadingPanel.add(text);
		
		go(initialPage);
	}
	
	private void renderPage() {
		pagePane.removeAll();
		pagePane.add(history.getCurrent());
		backButton.update();
		forwardButton.update();
		validate();
		pagePane.setSize(this.getSize());
		pagePane.repaint();
	}
	
	private void renderLoadingScreen() {
		pagePane.removeAll();
		pagePane.add(loadingPanel);
		pagePane.repaint();
	}
	
	protected JPanel getNavPane() {
		return navPane;
	}

	public void go(final BrowserPage page) {
		history.getCurrent().onExit(page);
		if(!page.isLoaded()) {
			renderLoadingScreen();
			new Thread() {
				public void run() {
					try {
						page.load();
						page.onEnter(history.push(page));
					} catch(PageLoadError e) {
						if(!e.getRedirect().isLoaded())
							throw new RuntimeException("invalid redirect page");
						e.getRedirect().onEnter(history.push(e.getRedirect()));
					}
					renderPage();
				}
			}.start();
		} else {
			page.onEnter(history.push(page));
			renderPage();
		}
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
		renderPage();
	}

	public void goBack() {
		BrowserPage old = history.getCurrent();
		old.onExit(history.back());
		history.getCurrent().onEnter(old);
		renderPage();
	}
}