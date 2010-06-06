package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.Fonts;
import edu.washington.cs.cse403d.coauthor.client.utils.HelpMarker;

public class ArticleSearchPane extends JPanel {
	private static final long serialVersionUID = -2380014657227857630L;

	protected JLabel title = new JLabel("Title: ");
	protected JTextField query = new JTextField();
	protected JTextField startYear = new JTextField(5);
	protected JTextField endYear = new JTextField(5);
	protected JButton submit = new JButton("Search");

	public ArticleSearchPane() {
		JPanel topPart = new JPanel();
		topPart.add(new JLabel("Please enter your search terms below"));
		topPart.add(new HelpMarker(Services.getResourceManager().loadStrings("strings.xml").get(
				"ArticleSearchPane.help")));

		final JPanel bottomPart = new JPanel();
		query.setPreferredSize(new Dimension(100, query.getPreferredSize().height));
		query.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER)
					submit.doClick();
			}
		});
		query.setFont(Fonts.getSearchFont());
		query.setPreferredSize(new Dimension(150, 24));
		bottomPart.add(title);
		bottomPart.add(query);
		bottomPart.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (query.getText().length() == 0) {
					JOptionPane.showMessageDialog(bottomPart, "Please enter a query.", "Error!",
							JOptionPane.ERROR_MESSAGE);
				} else if (query.getText().length() < 4) {
					JOptionPane.showMessageDialog(bottomPart, "Query must be at least 4 characters.", "Error!",
							JOptionPane.ERROR_MESSAGE);
				} else if (!isNumber(startYear.getText())) {
					JOptionPane.showMessageDialog(bottomPart, "Starting year must be a number.\n"
							+ "Valid Input Range: 1800 - 2010", "Error!", JOptionPane.ERROR_MESSAGE);
				} else if (!isNumber(startYear.getText())) {
					JOptionPane.showMessageDialog(bottomPart, "Ending year must be a number.\n"
							+ "Valid Input Range: 1800 - 2010", "Error!", JOptionPane.ERROR_MESSAGE);
				} else if (startYear.getText().length() > 0
						&& (Integer.parseInt(startYear.getText()) > 2010 || Integer.parseInt(startYear.getText()) < 1800)) {
					JOptionPane.showMessageDialog(bottomPart, "Incorrect starting year.\n"
							+ "Valid Input Range: 1800 - 2010", "Error!", JOptionPane.ERROR_MESSAGE);
				} else if (endYear.getText().length() > 0
						&& (Integer.parseInt(endYear.getText()) > 2010 || Integer.parseInt(endYear.getText()) < 1800)) {
					JOptionPane.showMessageDialog(bottomPart, "Incorrect ending year.\n"
							+ "Valid Input Range: 1800 - 2010", "Error!", JOptionPane.ERROR_MESSAGE);
				} else if (startYear.getText().length() > 0 && endYear.getText().length() > 0
						&& (Integer.parseInt(startYear.getText()) > Integer.parseInt(endYear.getText()))) {
					JOptionPane.showMessageDialog(bottomPart, "Start year must be smaller than end year.", "Error!",
							JOptionPane.ERROR_MESSAGE);
				} else {
					int start;
					int end;
					if (startYear.getText().length() == 0)
						start = 0;
					else
						start = Integer.parseInt(startYear.getText());
					if (endYear.getText().length() == 0)
						end = 0;
					else
						end = Integer.parseInt(endYear.getText());
					Services.getBrowser().go(new ArticleSearchResults(query.getText(), start, end));
				}
			}
		});

		JPanel yearFilter = new JPanel();
		yearFilter.add(new JLabel("Filter by year: From "));
		yearFilter.add(startYear);
		yearFilter.add(new JLabel(" To "));
		yearFilter.add(endYear);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(topPart);
		add(bottomPart);
		add(yearFilter);
	}

	private boolean isNumber(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < 48 || str.charAt(i) > 57)
				return false;
		}
		return true;
	}

	public String getTitle() {
		return "Search";
	}
}