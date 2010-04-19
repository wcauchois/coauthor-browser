// This class requires BreezySwing, which can be found at:
// <http://faculty.cs.wwu.edu/martin/software%20packages/BreezyGUI/legal_stuff.htm>

package edu.washington.cse403d.uiprototype;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import BreezySwing.*;

public class UIPrototypeMiles extends GBFrame implements ActionListener
{
	JLabel authorLabel = addLabel ("Author Name:",1,1,1,1);
	JTextField authorName = addTextField ("Enter Author's Name Here",1,2,1,1);
	
	JButton authorSearch = addButton("Search For Other Authors", 2,1,2,1);
	
	JLabel coAuthorLabel1 = addLabel ("Co-Author 1: ",3,1,1,1);
	JTextField coAuthor1 = addTextField ("Enter First Co-Author",3,2,1,1);
	
	JLabel coAuthorLabel2 = addLabel ("Co-Author 2: ",4,1,1,1);
	JTextField coAuthor2 = addTextField ("Enter First Co-Author",4,2,1,1);
	
	JLabel coAuthorLabel3 = addLabel ("Co-Author 3: ",5,1,1,1);
	JTextField coAuthor3 = addTextField ("Enter First Co-Author",5,2,1,1);
	
	private ArrayList<JLabel> labels = new ArrayList<JLabel>();
	private ArrayList<JTextField> fields = new ArrayList<JTextField>();
	private int curH = 8;
	
	JButton coAuthor = addButton ("Add Co-Author", 6,1,1,1);
	JButton search = addButton ("Search", 6,2,1,1);
	JButton advancedSearch = addButton ("Advanced Search", 7,1,2,1);

	public void buttonClicked (JButton buttonObj)
	{
		if (buttonObj == authorSearch)
		{
			
		}
		else if (buttonObj == coAuthor)
		{
			JLabel tempLabel = addLabel("Co-Author " + (curH-4) + ":", curH,1,1,1);
			JTextField tempField = addTextField ("Enter Next Co-Author",curH,2,1,1);
			curH++;
			labels.add(tempLabel);
			fields.add(tempField);
		}
		else if (buttonObj == search)
		{
			
		}
		else if (buttonObj == advancedSearch)
		{
			
		}
	}
	
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu, submenu;
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
    
	public static void main(String[] args)
	{
		UIPrototypeMiles ui = new UIPrototypeMiles();
        ui.setJMenuBar(ui.createMenuBar());
		ui.setSize(300,400);
		ui.setVisible(true);
	}

	public void actionPerformed(ActionEvent evt)
	{
		 if(evt.getActionCommand().equals("Quit")) {
			 System.exit(1);
	     }
	}
}
