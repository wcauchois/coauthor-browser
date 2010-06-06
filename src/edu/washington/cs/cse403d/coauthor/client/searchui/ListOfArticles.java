package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.Fonts;
import edu.washington.cs.cse403d.coauthor.client.utils.LineWrappedLabel;
import edu.washington.cs.cse403d.coauthor.client.utils.ListPopupMouseListener;
import edu.washington.cs.cse403d.coauthor.client.utils.StringUtils;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

public class ListOfArticles extends JList {
	private int preferredWidth;
	private Font titleFont, authorsFont;
	
	public ListOfArticles(List<Publication> pubs, int preferredWidth) {
		setListData(pubs.toArray());
		setCellRenderer(new PubRenderer());
		this.preferredWidth = preferredWidth;

		Font dialogFont = Fonts.getDialogFont();
		titleFont = dialogFont.deriveFont(Font.BOLD);
		authorsFont = dialogFont.deriveFont(Font.ITALIC);
		
		addMouseListener(new ListPopupMouseListener(buildContextMenu()));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if(evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
					goToSelectedArticle();
			}
		});
	}
	
	public void setTitleFont(Font font) {
		titleFont = font;
		repaint();
	}
	
	private class PubRenderer extends JPanel implements ListCellRenderer {
		private static final long serialVersionUID = 5729873339180000514L;
		
		public PubRenderer() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
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

			setPreferredSize(new Dimension(preferredWidth + 10, authors.getPreferredSize().height
					+ title.getActualHeight(preferredWidth) + 15));
			setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
			
			return this;
		}
	}
	
	private void goToSelectedArticle() {
		Services.getBrowser().go(new ArticleResult(
				getSelectedValue().toString()));
	}
	
	protected JPopupMenu buildContextMenu() {
		final JPopupMenu menu = new JPopupMenu();
		
		JMenuItem menuItem;
		menuItem = new JMenuItem("Search for this publication");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				goToSelectedArticle();
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Copy to clipboard");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				StringUtils.copyToClipboard(getSelectedValue());
			}
		});
		menu.add(menuItem);
		
		return menu;
	}
}