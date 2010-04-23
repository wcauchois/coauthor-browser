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
 	private JButton authorS;
	private JButton articleS;
	private JButton graphV;
	private JButton llsr; //load last search result
	private JLabel questionMark1;
	private JLabel questionMark2;
	private JLabel questionMark3;
	private JLabel questionMark4;
	
	//Test variable for displaying caption!
	private String caption = "This is the help text displayed";
	
	// returns ImageIcon object
	private ImageIcon createImageIcon(String path, String description) {
		URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("No such file exists: " + path);
			return null;
		}
	}	
	
	//just initializes variables and such
	private void initialize() {
		this.setLayout(new GridBagLayout());
		this.setName("Main");
		
		authorS = new JButton();
		authorS.setText("Search By Author");
		
		articleS = new JButton();
		articleS.setText("Search By Article");
		
		graphV = new JButton();
		graphV.setText("Search By Graph");
		
		llsr = new JButton();
		llsr.setText("Load Last Search Result");
		
		// will have to change these to buttons
		ImageIcon icon = createImageIcon("images/questionmark.gif", "Question Mark");
		questionMark1 = new JLabel(icon);
		questionMark2 = new JLabel(icon);
		questionMark3 = new JLabel(icon);
		questionMark4 = new JLabel(icon);		
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
		
		/* This is the last question mark button
		 * capAction takes the image to display, and the caption to display
		 * You can find caption at the variable declarations at the top
		 */
		Icon icon = createImageIcon("images/questionmark.gif", "Question Mark");
		captionAction capAction = new captionAction(icon, caption);
		JButton button = new JButton(capAction);
		HelpButton hb = new HelpButton(caption);
		
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
		add(new HelpButton("Hello world"), c);
		
	    c.gridx = 1;
	    c.gridy = 3;
	    add(questionMark2, c);
	    
	    c.gridx = 1;
	    c.gridy = 2;
	    add(questionMark3, c);
	    
	    c.gridx = 1;
	    c.gridy = 1;
	    add(questionMark4, c);
		
	    //Button 1
		GridBagConstraints c0 = new GridBagConstraints();
		c0.fill = GridBagConstraints.HORIZONTAL;
		c0.weightx = 0.5;
		c0.gridx = 0;
		c0.gridy = 1;
		c0.insets = new Insets(10, 0, 10, 0);
		add(authorS, c0);
		
		//Button 2
		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.HORIZONTAL;
		c1.gridx = 0;
		c1.gridy = 2;
		c1.insets = new Insets(10, 0, 10, 0);  //top, left, bottom, right
		add(articleS, c1);
		
		//Button 3
		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.gridx = 0;
		c2.gridy = 3;
		c2.insets = new Insets(10, 0, 10, 0);
		add(graphV, c2);
		
		//Button 4
		GridBagConstraints c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.weightx = 0.5;
		c3.gridx = 0;
		c3.gridy = 4;
		c3.insets = new Insets(10, 0, 10, 0);
		add(llsr, c3);		
		
		
		
	}

	public String getTitle() {
		return "Start";
	}
	
	//constructor
	public StartPage() {
		initialize();
		position();
		createFrame();
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
	
	//Internal frame tester
	protected void createFrame() {
        MyInternalFrame frame = new MyInternalFrame("Test"); //Frame title
        frame.setVisible(true); 
        this.add(frame);  //add internal frame to container
        frame.add(new JLabel("Test"));  //frame content
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
	
}