package edu.washington.cs.cse403d.coauthor.uiprototype;

// Try Ctrl+Shift+O in windows; Apple+alt+O (or shift) on mac -Jeff
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Program implements ActionListener {
	private JFrame mainFrame;
	public Program(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	public void actionPerformed(ActionEvent evt) {
		if(evt.getActionCommand().equals("Quit")) {
			// XXX: is this the right way to exit the app?
			mainFrame.dispose();
		}
	}
	private static class Author {
		public Author(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}
		public String firstName, lastName;
	}
	private static class AuthorsListModel extends AbstractListModel {
		public ArrayList<Author> data = new ArrayList<Author>();
		public void add(Author author) { data.add(author); }
		public Object getElementAt(int i) { return data.get(i); }
		public int getSize() { return data.size(); }
	}
	private static class AuthorCellRenderer extends JLabel
											implements ListCellRenderer {
		public Component getListCellRendererComponent(
		  JList list, Object value, int index,
		  boolean isSelected, boolean cellHasFocus) {
			Author author = (Author)value;
			setText(author.lastName + ", " + author.firstName);
			if(isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}
	public JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		menuItem = new JMenuItem("Quit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menuBar.add(menu);

		ButtonGroup group = new ButtonGroup();
		rbMenuItem = new JRadioButtonMenuItem("Authors");
		rbMenuItem.setSelected(true);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Articles");
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Graph");
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		return menuBar;
	}
	public Container createContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final JPanel left = new JPanel(), right = new JPanel();
		split.setLeftComponent(left);
		split.setRightComponent(right);
	
		left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
		final JTextField filterBar = new JTextField("Filter");
		/*String[] authors = {"Notkin, David",
							"Murphey, Gail C",
							"Aldrich, Jonathan",
							"Chambers, Craig",
							"VanHilst, Michael"};*/
		AuthorsListModel authors = new AuthorsListModel();
		authors.add(new Author("David", "Notkin"));
		authors.add(new Author("Gail C", "Murphey"));
		authors.add(new Author("Jonathan", "Aldrich"));
		authors.add(new Author("Craig", "Chambers"));
		authors.add(new Author("Michael", "VanHilst"));
		final JList authorsList = new JList(authors);
		authorsList.setCellRenderer(new AuthorCellRenderer());
		filterBar.setForeground(Color.LIGHT_GRAY);
		filterBar.setEditable(false);
		left.add(filterBar);
		left.add(authorsList);
		left.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent evt) {
				filterBar.setMaximumSize(new Dimension(
					left.getWidth(), filterBar.getPreferredSize().height));
				authorsList.setMaximumSize(left.getSize());
			}
			public void componentHidden(ComponentEvent evt) { }
			public void componentShown(ComponentEvent evt) { }
			public void componentMoved(ComponentEvent evt) { }
		});
		// this is just to fire the componentResized event
		left.setSize(left.getPreferredSize());

		final JLabel authorName = new JLabel("David Notkin");
		authorName.setFont(authorName.getFont().deriveFont(Font.BOLD, 24));
		right.add(authorName);

		authorsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				Author author = (Author)authorsList.getSelectedValue();
				authorName.setText(author.firstName + " " + author.lastName);
			}
		});

		pane.add(split);
		return pane;
	}
	public static void createAndShowGUI() {
		JFrame frame = new JFrame("UI Prototype");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Program proto = new Program(frame);
		frame.setJMenuBar(proto.createMenuBar());
		frame.setContentPane(proto.createContentPane());

		frame.setSize(500, 300);
		frame.setVisible(true);
	}
	public static void main(String[] args) {
		try {
			// i like my system look and feel!
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			// not really the end of the world...
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { createAndShowGUI(); }
		});
	}
}
