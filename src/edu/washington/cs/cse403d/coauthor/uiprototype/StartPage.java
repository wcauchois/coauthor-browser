package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.net.URL;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.JTextPane;


public class StartPage extends BrowserPage {
 	private JButton authorSearchButton;
	private JButton articleSearchButton;
	private JButton graphSearchButton;
	
	private static String authorSearchHelp = "Author search help";
	private static String articleSearchHelp = "Article search help";
	private static String graphSearchHelp = "Graph search help";
	
	private void initialize() {
		this.setLayout(new GridBagLayout());
		this.setName("Main");
		
		authorSearchButton = new JButton();
		authorSearchButton.setText("Search By Author");
		
		articleSearchButton = new JButton();
		articleSearchButton.setText("Search By Article");
		
		graphSearchButton = new JButton();
		graphSearchButton.setText("Search By Graph");
	}
	
	private void position() {
		//This will be the place for the logo.
		//Will add a logo picture in place of the Label
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 100, 0);
		add(new JLabel("Placeholder for Logo"), c);
		
		//Insert question marks
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 4;
		c.insets = new Insets(0, 5, 0, 0);
		//add(questionMark1, c);
		//this is where the JButton created with capAction is inserted.
		//Enable add(questionMark1, c) to display JLabel instead, if you want to.
		//add(button, c);
		add(new HelpMarker(authorSearchHelp), c);
		
	    c.gridx = 1;
	    c.gridy = 3;
	    add(new HelpMarker(articleSearchHelp), c);
	    
	    c.gridx = 1;
	    c.gridy = 2;
	    add(new HelpMarker(graphSearchHelp), c);
	    
	    //Button 1
		GridBagConstraints c0 = new GridBagConstraints();
		c0.fill = GridBagConstraints.HORIZONTAL;
		c0.weightx = 0.5;
		c0.gridx = 0;
		c0.gridy = 1;
		c0.insets = new Insets(10, 0, 10, 0);
		add(authorSearchButton, c0);
		
		//Button 2
		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.HORIZONTAL;
		c1.gridx = 0;
		c1.gridy = 2;
		c1.insets = new Insets(10, 0, 10, 0);  //top, left, bottom, right
		add(articleSearchButton, c1);
		
		//Button 3
		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.gridx = 0;
		c2.gridy = 3;
		c2.insets = new Insets(10, 0, 10, 0);
		add(graphSearchButton, c2);
	
		
		
		
	}

	public String getTitle() {
		return "Start";
	}
	
	//constructor
	public StartPage() {
		initialize();
		position();
		//createFrame();
	}
		
	
	private class captionAction extends AbstractAction{
        
		public captionAction(Icon icon, String caption){
            // puts out the caption
            putValue(SHORT_DESCRIPTION, caption);
            
            // puts out the icon
            putValue(LARGE_ICON_KEY, icon);
        }

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Currently No action is assigned since just putting
			// the cursor over the icon does the job.
			
		}
	}	
}