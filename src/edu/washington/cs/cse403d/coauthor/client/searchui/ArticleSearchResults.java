package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import java.util.Hashtable;
import java.util.List;


import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.browser.MessagePage;
import edu.washington.cs.cse403d.coauthor.client.browser.PageLoadError;
import edu.washington.cs.cse403d.coauthor.client.utils.GoBackActionListener;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

public class ArticleSearchResults extends BrowserPage {
	private static final long serialVersionUID = -5261731609522734709L;

	private String search;
	private List<Publication> pubData;
	private JList results;

	private static final int RESULTS_WIDTH = 450, RESULTS_HEIGHT = 410;

	public ArticleSearchResults(String theQuery) {
		search = theQuery;
	}

	public String getTitle() {
		return search.trim();
	}

	@Override
	protected void load() throws Exception {
		CoauthorDataServiceInterface c = Services.getCoauthorDataServiceInterface();
		pubData = c.getPublications(search);
		
		if(pubData.size() == 0) {
			MessagePage message = new MessagePage(MessagePage.WARNING,
					"No results",
					"Your search for \"" + search + "\" did not yield any results.",
					MessagePage.GO_BACK);
			message.addActionListener(new GoBackActionListener());
			throw new PageLoadError(message);
		}
		
		results = new ListOfArticles(pubData, RESULTS_WIDTH);
		
		setLayout(new BorderLayout());
		
		JScrollPane scroller = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setPreferredSize(new Dimension(RESULTS_WIDTH, RESULTS_HEIGHT));
		add(scroller, BorderLayout.CENTER);
		
		int minimumYear = Integer.MAX_VALUE, maximumYear = 0;
		for(Publication pub : pubData) {
			if(pub.getYear() < minimumYear)
				minimumYear = pub.getYear();
			if(pub.getYear() > maximumYear)
				maximumYear = pub.getYear();
		}
		add(new YearFilterPanel(minimumYear, maximumYear), BorderLayout.SOUTH);
		
		setLoaded();
	}
	private class YearFilterPanel extends JPanel {
		private static final long serialVersionUID = 6846853590031989030L;
		private void initYearSlider(JSlider slider, int minimum, int maximum) {
			slider.setMinimum(minimum);
			slider.setMaximum(maximum);
			Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
			labels.put(minimum, new JLabel("" + minimum));
			labels.put(maximum, new JLabel("" + maximum));
			slider.setLabelTable(labels);
			slider.setPaintLabels(true);
		}
		private void update() {
			label.setText("Showing articles published between " + startYear.getValue() + " and " + endYear.getValue());
			List<Publication> filteredPubs = new ArrayList<Publication>();
			for(Publication pub : pubData) {
				if(pub.getYear() >= startYear.getValue() && pub.getYear() <= endYear.getValue())
					filteredPubs.add(pub);
			}
			results.setListData(filteredPubs.toArray());
		}
		private JSlider startYear = new JSlider();
		private JSlider endYear = new JSlider();
		private JLabel label = new JLabel();
		public YearFilterPanel(int minimumYear, int maximumYear) {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			initYearSlider(startYear, minimumYear, maximumYear);
			initYearSlider(endYear, minimumYear, maximumYear);
			
			startYear.setValue(minimumYear);
			startYear.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					if(startYear.getValue() > endYear.getValue())
						endYear.setValue(startYear.getValue());
					update();
				}
			});
			
			endYear.setValue(maximumYear);
			endYear.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					if(endYear.getValue() < startYear.getValue())
						startYear.setValue(endYear.getValue());
					update();
				}
			});

			add(Box.createVerticalStrut(15));
			label.setFont(label.getFont().deriveFont(Font.ITALIC));
			add(label);
			
			add(startYear);
			add(endYear);
			
			update();
		}
	}
}