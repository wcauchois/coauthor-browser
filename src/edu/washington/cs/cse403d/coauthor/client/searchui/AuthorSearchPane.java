package edu.washington.cs.cse403d.coauthor.client.searchui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.washington.cs.cse403d.coauthor.client.ResourceManager;
import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.browser.BrowserPage;
import edu.washington.cs.cse403d.coauthor.client.utils.HelpMarker;
import edu.washington.cs.cse403d.coauthor.client.utils.QuerySuggestionsField;

/**
 * This panel permits the user to enter one or more author names, as for
 * a search on those authors.
 * @see SearchPage
 * @author William Cauchois
 *
 */
public class AuthorSearchPane extends JPanel {
	private class AuthorField extends QuerySuggestionsField {
		private final Color invalidMarkColor = Color.RED;
		private boolean markedInvalid = false;
		public void markInvalid() {
			markedInvalid = true;
			setBackground(invalidMarkColor);
		}
		@Override
		protected void onSubmit() {
			AuthorSearchPane.this.onSubmit();
		}
		public AuthorField() {
			addFocusListener(new FocusAdapter() {
				@Override public void focusGained(FocusEvent evt) {
					if(markedInvalid) {
						// Restore the default background color
						setBackground(SystemColor.text);
					}
				}
			});
		}
		@Override
		protected List<String> issueQuery(String part) throws Exception {
			return Services.getCoauthorDataServiceInterface().getAuthors(part);
		}
	}
	private JButton submit = new JButton("Search");
	private List<AuthorField> authorFields = new LinkedList<AuthorField>();
	private ExecutorService executor;
	private class AuthorsPane extends JPanel {
		private class FocusPolicy extends FocusTraversalPolicy {
			private Component getComponentPlusN(Component comp, int n) {
				int i = authorFields.indexOf(comp);
				if(i < 0) return authorFields.get(0);
				else {
					i = (i + n) % authorFields.size();
					if(i < 0) i += authorFields.size();
					return authorFields.get(i);
				}
			}
			@Override
			public Component getComponentAfter(Container cycleRoot, Component comp) {
				return getComponentPlusN(comp, 1);
			}
			@Override
			public Component getComponentBefore(Container cycleRoot, Component comp) {
				return getComponentPlusN(comp, -1);
			}
			@Override
			public Component getDefaultComponent(Container cycleRoot) {
				return authorFields.get(0);
			}
			@Override
			public Component getFirstComponent(Container cycleRoot) {
				return authorFields.get(0);
			}
			@Override
			public Component getLastComponent(Container arg0) {
				return authorFields.get(authorFields.size() - 1);
			}
		}
		private GroupLayout layout;
		private GroupLayout.SequentialGroup hGroup, vGroup;
		private GroupLayout.ParallelGroup[] columns = new GroupLayout.ParallelGroup[N_COLUMNS];
		private ImageIcon plusIcon, minusIcon;
		private List<JComponent[]> rows = new LinkedList<JComponent[]>();
		private static final int N_COLUMNS = 4;
		public AuthorsPane() {
			ResourceManager resourceManager = Services.getResourceManager();
			plusIcon = resourceManager.loadImageIcon("plus.png");
			minusIcon = resourceManager.loadImageIcon("minus.png");
			
			setLayout(layout = new GroupLayout(this));
			setFocusTraversalPolicy(new FocusPolicy());
			setFocusCycleRoot(true);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			hGroup = layout.createSequentialGroup();
			for(int i = 0; i < N_COLUMNS; i++) {
				columns[i] = layout.createParallelGroup();
				hGroup.addGroup(columns[i]);
			}
			layout.setHorizontalGroup(hGroup);
			
			vGroup = layout.createSequentialGroup();
			layout.setVerticalGroup(vGroup);
			
			addFirstRow();
		}
		private void addFirstRow() {
			AuthorField field = new AuthorField();
			authorFields.add(field);
			JButton minusButton = new MinusButton();
			minusButton.setEnabled(false);
			addRow(new JLabel("Author:"), field, minusButton, new PlusButton());
			validate();
		}
		private void refresh() {
			getParent().validate();
			repaint();
		}
		// Encapsulates a tiny bit of behavior shared between PlusButton and MinusButton.
		private class PlusMinusButton extends JButton {
			private final Dimension size = new Dimension(27, 27);
			public PlusMinusButton(ImageIcon theIcon) {
				super(theIcon);
				setBorderPainted(false);
				setContentAreaFilled(false);
				setMaximumSize(size);
				setMinimumSize(size);
			}
		}
		private class MinusButton extends PlusMinusButton implements ActionListener {
			private JComponent[] otherCells;
			public MinusButton(JComponent... otherCells) {
				super(minusIcon);
				this.otherCells = otherCells.clone();
				addActionListener(this);
			}
			public void actionPerformed(ActionEvent evt) {
				for(JComponent cell : otherCells) {
					if(cell instanceof AuthorField)
						authorFields.remove(cell);
					AuthorsPane.this.remove(cell);
				}
				AuthorsPane.this.remove(this);
				refresh();
			}
		}
		private class PlusButton extends PlusMinusButton implements ActionListener {
			public PlusButton() {
				super(plusIcon);
				addActionListener(this);
			}
			public void actionPerformed(ActionEvent evt) {
				AuthorField field = new AuthorField();
				JLabel label = new JLabel("Co-author:");
				JButton plusButton = new PlusButton();
				JButton minusButton = new MinusButton(field, label, plusButton);
				authorFields.add(field);
				addRow(label, field, minusButton, plusButton);
				refresh();
			}
		}
		private void addRow(JComponent... cells) {
			GroupLayout.ParallelGroup row = layout.createParallelGroup(
					GroupLayout.Alignment.BASELINE);
			int i = 0;
			for(JComponent cell : cells) {
				columns[i].addComponent(cell);
				row.addComponent(cell);
				i++;
			}
			vGroup.addGroup(row);
			rows.add(cells.clone());
		}
	}
	private AuthorsPane authorsPane = new AuthorsPane();
	private static ExecutorService createExecutor() {
		return Executors.newSingleThreadExecutor();
	}
	public AuthorSearchPane(BrowserPage parent) {
		executor = createExecutor();
		setLayout(new BorderLayout());
		
		JPanel headerPane = new JPanel();
		headerPane.add(new JLabel("Please enter your search terms below."));
		headerPane.add(new HelpMarker(
				Services.getResourceManager().
				loadStrings("strings.xml").get("AuthorSearchPane.help")));
		add(headerPane, BorderLayout.NORTH);
		
		JScrollPane scrollAuthorsPane = new JScrollPane(authorsPane);
		add(scrollAuthorsPane, BorderLayout.CENTER);
		
		JPanel submitPane = new JPanel();
		submitPane.add(submit);
		submitPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(submitPane, BorderLayout.SOUTH);
		submit.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent evt) {
				onSubmit();
			}
		});
		
		parent.addNavListener(new BrowserPage.NavListener() {
			public void onEnter(BrowserPage previous) {
				if(executor == null)
					executor = createExecutor();
			}
			public void onExit(BrowserPage next) {
				for(AuthorField f : authorFields)
					f.hideSuggestions();
				executor.shutdownNow();
				executor = null;
			}
		});
	}
	protected void onSubmit() {
		List<String> authors = getAuthors();
		if(authors != null)
			Services.getBrowser().go(new AuthorSearchResults(getAuthors()));
	}
	public List<String> getAuthors() {
		List<String> authors = new ArrayList<String>();
		boolean haveInvalidFields = false;
		for(AuthorField field : authorFields) {
			if(field.getText().trim().length() == 0) {
				field.markInvalid();
				haveInvalidFields = true;
			} else if(!haveInvalidFields)
				authors.add(field.getText());
		}
		if(haveInvalidFields)
			return null;
		else
			return authors;
	}
}