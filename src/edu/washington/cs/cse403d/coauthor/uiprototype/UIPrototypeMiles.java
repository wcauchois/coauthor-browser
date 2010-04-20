package edu.washington.cs.cse403d.coauthor.uiprototype;

// This class requires BreezySwing, which can be found at:
// <http://faculty.cs.wwu.edu/martin/software%20packages/BreezyGUI/legal_stuff.htm>

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.swing.*;

import edu.washington.cs.cse403d.coauthor.dataservice.CoauthorDataServiceInterface;
import BreezySwing.GBFrame;

public class UIPrototypeMiles extends GBFrame implements ActionListener
{
	JLabel authorLabel = addLabel("Author Name:", 1, 1, 1, 1);
	JTextField authorName = addTextField("Enter Author's Name Here", 1, 2, 1, 1);

	JButton authorSearch = addButton("Search For Other Authors", 2, 1, 2, 1);

	JLabel coAuthorLabel1 = addLabel("Co-Author 1: ", 3, 1, 1, 1);
	JTextField coAuthor1 = addTextField("Enter Co-Author", 3, 2, 1, 1);

	JLabel coAuthorLabel2 = addLabel("Co-Author 2: ", 4, 1, 1, 1);
	JTextField coAuthor2 = addTextField("Enter Next Co-Author", 4, 2, 1, 1);

	JLabel coAuthorLabel3 = addLabel("Co-Author 3: ", 5, 1, 1, 1);
	JTextField coAuthor3 = addTextField("Enter Next Co-Author", 5, 2, 1, 1);

	private ArrayList<JLabel> labels = new ArrayList<JLabel>();
	private ArrayList<JTextField> fields = new ArrayList<JTextField>();
	private int curH = 8;

	JButton advancedSearch = addButton("Advanced Search", 6, 1, 1, 1);
	JButton search = addButton("Search", 6, 2, 1, 1);
	JButton coAuthor = addButton("Add Co-Author", 7, 1, 2, 1);

	private static CoauthorDataServiceInterface c;
	public static final String HOSTNAME = "attu2.cs.washington.edu";

	public void buttonClicked(JButton buttonObj)
	{
		if (buttonObj == authorSearch)
		{ // runs the method which returns names similar to the one given by the user
			if (!authorName.getText().equals("Enter Author's Name Here"))
			{
				try
				{
					String[] name = authorName.getText().split(" +");
					String s = "";
					for (int i = 0; i < name.length; i++)
						s += "+" + name[i] + " ";
					s = s.substring(0, s.length() - 1);
					System.out.println(c.getAuthors(s));
				} catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
		}
		else if (buttonObj == coAuthor)
		{ // adds a new co-author text field to the GUI
			JLabel tempLabel = addLabel("Co-Author " + (curH - 4) + ":", curH,
					1, 1, 1);
			JTextField tempField = addTextField("Enter Next Co-Author", curH,
					2, 1, 1);
			curH++;
			labels.add(tempLabel);
			fields.add(tempField);
			validate();
		}
		else if (buttonObj == search)
		{ // runs the method which returns the names of all co-authors of the given author
			if (!authorName.getText().equals("Enter Author's Name Here"))
			{
				try
				{
					System.out.println(c.getCoauthors(authorName.getText()));
				} catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
		}
		else if (buttonObj == advancedSearch)
		{ // runs the method which returns a list of all of the works of all of the provided authors and/or co-authors
			boolean done = false;
			ArrayList<String> authors = new ArrayList<String>();
			if (!authorName.getText().equals("Enter Author's Name Here"))
				authors.add(authorName.getText());
			if (!coAuthor1.getText().equals("Enter Co-Author"))
			{
				authors.add(coAuthor1.getText());
			}
			else
				done = true;
			if (!done && !coAuthor2.getText().equals("Enter Next Co-Author"))
			{
				authors.add(coAuthor2.getText());
			}
			else
				done = true;
			if (!done && !coAuthor3.getText().equals("Enter Next Co-Author"))
			{
				authors.add(coAuthor3.getText());
			}
			else
				done = true;

			for (int i = 0; i < fields.size() && !done; i++)
			{
				if (!fields.get(i).getText().equals("Enter Next Co-Author"))
				{
					authors.add(fields.get(i).getText());
				}
				else
					done = true;
			}

			String[] names = new String[authors.size()];
			for (int i = 0; i < authors.size(); i++)
				names[i] = authors.get(i);
			if (authors.size() > 0)
			{
				try
				{
					System.out.println(c.getPublicationsForAnyAuthor(names));
				} catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu menu, submenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		menuItem = new JMenuItem("New");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Quit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
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

	public static void main(String[] args)
	{
		UIPrototypeMiles ui = new UIPrototypeMiles();
		ui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ui.setJMenuBar(ui.createMenuBar());
		ui.setSize(350, 400);
		ui.setVisible(true);

		try
		{
			c = (CoauthorDataServiceInterface) Naming.lookup("rmi://"
					+ HOSTNAME + "/"
					+ CoauthorDataServiceInterface.SERVICE_NAME);
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("Quit"))
		{
			System.exit(1);
		}
		else if (evt.getActionCommand().equals("New"))
		{
			main(null);
		}
	}
}
