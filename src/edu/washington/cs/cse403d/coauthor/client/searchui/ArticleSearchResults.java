package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.Fonts;
import edu.washington.cs.cse403d.coauthor.client.utils.LineWrappedLabel;
import edu.washington.cs.cse403d.coauthor.client.utils.StringUtils;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

public class ArticleSearchResults extends BrowserPage {
	private static final long serialVersionUID = -5261731609522734709L;

	private String search;

	private static final int RESULTS_WIDTH = 450, RESULTS_HEIGHT = 450;

	private class PubRenderer extends JPanel implements ListCellRenderer {
		private static final long serialVersionUID = 5729873339180000514L;
		
		private Font titleFont, authorsFont;

		public PubRenderer() {
			Font dialogFont = Fonts.getDialogFont();
			titleFont = dialogFont.deriveFont(Font.BOLD, 14);
			authorsFont = dialogFont.deriveFont(Font.ITALIC);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			Publication pub = (Publication) value;
			removeAll();
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			LineWrappedLabel title = new LineWrappedLabel(pub.getTitle());
			title.setFont(titleFont);
			add(title);

			JPanel authorsWrapper = new JPanel();
			authorsWrapper.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel authors = new JLabel(StringUtils.join(", ", pub.getAuthors()));
			authors.setFont(authorsFont);
			authorsWrapper.setBackground(null);
			authorsWrapper.add(authors);
			add(authorsWrapper);

			setPreferredSize(new Dimension(RESULTS_WIDTH + 10, authors.getPreferredSize().height
					+ title.getActualHeight(RESULTS_WIDTH) + 15));
			setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
			return this;
		}
	}

	private JList results = new JList();

	public ArticleSearchResults(String theQuery) {
		search = theQuery;
	}

	public String getTitle() {
		return search.trim();
	}

	@Override
	protected void load() {
		List<Publication> pubs = null;
		CoauthorDataServiceInterface c = Services.getCoauthorDataServiceInterface();
		try {
			pubs = c.getPublications(search);
		} catch (RemoteException r) {
			// XXX
		}

		setLayout(new BorderLayout());
		results.setCellRenderer(new PubRenderer());
		results.setListData(pubs.toArray());
		JScrollPane scroller = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setPreferredSize(new Dimension(RESULTS_WIDTH, RESULTS_HEIGHT));
		add(scroller, BorderLayout.CENTER);
	}
}