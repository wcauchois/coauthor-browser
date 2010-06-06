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
import edu.washington.cs.cse403d.coauthor.client.utils.GoBackActionListener;

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

		public void enableIfAvailable() {
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

		public void enableIfAvailable() {
			setEnabled(canGoForward());
		}
	}

	private class LoadPage extends Thread {
		private final BrowserPage page;
		private boolean isCancelled;

		public LoadPage(BrowserPage page) {
			this.page = page;
			this.isCancelled = false;
		}

		/**
		 * Cancels the currently loading page such that it will not be used when
		 * this thread's execution is complete
		 */
		public void cancelRequest() {
			this.isCancelled = true;
		}

		public void run() {
			try {
				page.load();
				if (!isCancelled) {
					page.onEnter(history.push(page));
				}
			} catch (PageLoadError e) {
				if (!e.getRedirect().isLoaded()) {
					throw new RuntimeException("invalid redirect page");
				}
				e.getRedirect().onEnter(history.push(e.getRedirect()));
			} catch (Exception e) {
				if (!isCancelled) {
					MessagePage message = new MessagePage(MessagePage.ERROR,
							"Error",
							"The operation could not be completed because an error was encountered ("
									+ e.getMessage() + ").", MessagePage.OK);
					message.addActionListener(new GoBackActionListener());
					message.onEnter(history.push(message));
					e.printStackTrace(System.err);
				}
			}
			if (!isCancelled) {
				renderPage();
				loadingThread = null;
			} else {
				System.out
						.println("Page was cancelled. Didn't do anything with load of page: "
								+ page.getTitle());
			}
		}
	}

	private ForwardButton forwardButton = new ForwardButton();
	private BackButton backButton = new BackButton();

	protected BrowserHistory history;
	private JPanel pagePane = new JPanel();
	private JPanel navPane = new JPanel();
	private JPanel loadingPanel;

	private LoadPage loadingThread;

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

		JLabel icon = new JLabel(Services.getResourceManager().loadImageIcon(
				"Loading.gif"));
		icon.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel text = new JLabel("Loading...");
		text.setAlignmentX(Component.CENTER_ALIGNMENT);

		loadingPanel.add(icon);
		loadingPanel.add(text);
		this.setIconImage(Services.getResourceManager().loadImageIcon(
				"notkin.png").getImage());
		go(initialPage);
	}

	private void renderPage() {
		backButton.enableIfAvailable();
		forwardButton.enableIfAvailable();loadingThread = null;

		pagePane.removeAll();
		pagePane.add(history.getCurrent());
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

	/**
	 * Cancels a loading page if one exists.
	 * 
	 * @return true if a loading page was canceled
	 */
	private boolean cancelLoadingPageIfExists() {
		if (loadingThread != null) {
			// Currently loading a page. Modify the background thread so it
			// doesn't navigate to it on completion
			loadingThread.cancelRequest();
			loadingThread = null;
			return true;
		}
		return false;
	}

	public void go(final BrowserPage page) {
		cancelLoadingPageIfExists();

		history.getCurrent().onExit(page);
		if (!page.isLoaded()) {
			renderLoadingScreen();
			loadingThread = new LoadPage(page);
			loadingThread.start();
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
		if (!cancelLoadingPageIfExists()) {
			BrowserPage old = history.getCurrent();
			old.onExit(history.forward());
			history.getCurrent().onEnter(old);
		}
		renderPage();
	}

	public void goBack() {
		if (!cancelLoadingPageIfExists()) {
			BrowserPage old = history.getCurrent();
			old.onExit(history.back());
			history.getCurrent().onEnter(old);
		}
		renderPage();
	}
}