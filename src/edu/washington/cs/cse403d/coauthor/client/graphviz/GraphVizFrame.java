package edu.washington.cs.cse403d.coauthor.client.graphviz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import prefuse.controls.ControlAdapter;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;

import edu.washington.cs.cse403d.coauthor.uiprototype.graphvis.VisualChainExplorer;
import edu.washington.cs.cse403d.coauthor.uiprototype.graphvis.VisualCoAuthorExplorer;
import edu.washington.cs.cse403d.coauthor.uiprototype.graphvis.VisualExplorer;

public class GraphVizFrame extends JFrame implements ActionListener{
	VisualExplorer visExp;
	
	
	public GraphVizFrame(String startingAuthor) {
		getContentPane().add(new JLabel(" Co-Authors Graph for " + startingAuthor));
		this.visExp = new VisualCoAuthorExplorer(startingAuthor);
		
		this.setContentPane(this.setupButtons(visExp));
		this.visExp.updateVis();
		this.add(visExp.getDisplay());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public GraphVizFrame(String startingAuthor, String endingAuthor) throws RemoteException{
		getContentPane().add(new JLabel("Co-Author chain from " + startingAuthor + "to " + endingAuthor));
		this.visExp = new VisualChainExplorer(startingAuthor,endingAuthor);
		
		this.setContentPane(this.setupButtons(visExp));
		this.visExp.updateVis();
		this.add(visExp.getDisplay());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}
	
    public JPanel setupButtons(VisualExplorer vx){
    	
    	
    	JPanel panel = new JPanel(new BorderLayout());
    	
    	final String label = "name";
		final JFastLabel title = new JFastLabel("       Fish          ");
		title.setPreferredSize(new Dimension(150, 20));
		title.setVerticalAlignment(SwingConstants.BOTTOM);
		title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
		title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
        
		
		vx.getDisplay().addControlListener(new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent e) {
                if ( item.canGetString(label) )
                    title.setText(item.getString(label));
            }
            public void itemExited(VisualItem item, MouseEvent e) {
                title.setText(null);
            }
        });
		
		
		JButton oneDg = new JButton("Add One Degree to All");
		oneDg.setToolTipText("Displays the co-authors of all nodes " +
				" in the graph.");
		oneDg.setActionCommand("onedg");
		oneDg.addActionListener(this);
		
		JButton trim = new JButton("Trim 1 Degree");
        trim.setActionCommand("trim");
        trim.setToolTipText("Remove all authors that have only " +
        		"one edge connecting them to the graph.");
        
		trim.addActionListener(this);
		
		JButton radial = new JButton("Radial View");
		radial.setActionCommand("rad");
		radial.addActionListener(this);
		radial.setToolTipText("Switch to a radial view of the co-author graph.");
		
		
		JButton force = new JButton("Dynamic View");
		force.setActionCommand("force");
		force.addActionListener(this);
		
		
		
		Box box = new Box(BoxLayout.X_AXIS);
        box.add(title);
        box.add(oneDg);
        box.add(trim);
        box.add(radial);
        box.add(force);
        
        box.add(Box.createHorizontalStrut(10));
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalStrut(3));
        panel.add(box, BorderLayout.SOUTH);
        
        Color BACKGROUND = Color.LIGHT_GRAY;
        Color FOREGROUND = Color.BLACK;
        UILib.setColor(panel, BACKGROUND, FOREGROUND);
    	
    	
    	return panel;
    }


	public void actionPerformed(ActionEvent e) {
		if("onedg".equals(e.getActionCommand())){
			boolean canAdd = visExp.addCoAuthorsToAllNodes();
			if(!canAdd){
				JOptionPane.showMessageDialog(this, "There are too many nodes in " +
						"the graph to add coauthors to all.","Warning!",JOptionPane.WARNING_MESSAGE);
			}
		}else if("rad".equals(e.getActionCommand())){
			visExp.switchToRadialLayout();
		}else if("trim".equals(e.getActionCommand())){
			visExp.trimOneDegree();
		}else if("force".equals(e.getActionCommand())){
			visExp.switchToFDL();
		}
		
	}
}