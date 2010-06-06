package edu.washington.cs.cse403d.coauthor.client.graphviz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.SearchTupleSet;

import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;

public class GraphVisTestClass {
	
	public static final String HOSTNAME = "attu2.cs.washington.edu";
	public static void main(String[] args) throws RemoteException{
		
		CoauthorDataServiceInterface backend;
		try {
			backend = (CoauthorDataServiceInterface) Naming.lookup("rmi://" + HOSTNAME + "/"
					+ CoauthorDataServiceInterface.SERVICE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		List<PathLink> chain= backend.getOneShortestPathBetweenAuthors("Jessica Miller", "Ross Tate", true); // Jessica Miller->Stephen G. Kobourov-> Michael Stepp-> Ross Tate
		System.out.println(chain);
	//	String s = "Jessica Miller";
	//	VisualCoAuthorExplorer vx = new VisualCoAuthorExplorer("David Notkin");
		//VisualChainExplorer vx = new VisualChainExplorer("Jessica Miller", "David Notkin");
		
/*		List<String> authors = new ArrayList<String>();
		authors.add("Marty Stepp");
		authors.add("David Notkin");
		authors.add("Paul Anderson");*/
		//authors.add()
		//VisualMultipleAuthorExplorer vx = new VisualMultipleAuthorExplorer(authors);
		
/*		JFrame frame = new JFrame("prefuse example");
		frame.setBounds(800, 100, 1, 1);
	    // ensure application exits when window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(demo(vx));
		frame.add(vx.forceTweaking());
		frame.pack();           // layout components in window
		frame.setVisible(true); // show the window
		vx.updateVis();
*/
		//GraphVizFrame fr = new GraphVizFrame("Jessica Miller", "Marty Stepp"); 
		GraphVizFrame fr = new GraphVizFrame("David Notkin");
		fr.pack();
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);
	}
	
    public static JPanel demo(VisualExplorer vx){
    	
    	
    	JPanel panel = new JPanel(new BorderLayout());
    	
    	final String label = "name";
		final JFastLabel title = new JFastLabel("       Fish          ");
		title.setPreferredSize(new Dimension(150, 20));
		title.setVerticalAlignment(SwingConstants.BOTTOM);
		title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
		title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
        
		JButton oneDg = new JButton("Add One Degree");
		oneDg.setToolTipText("Press this button to expand the co-authors of all nodes " +
				" in the graph.");
	//	oneDg.addActionListener((ActionListener) panel);
		
		vx.getDisplay().addControlListener(new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent e) {
                if ( item.canGetString(label) )
                    title.setText(item.getString(label));
            }
            public void itemExited(VisualItem item, MouseEvent e) {
                title.setText(null);
            }
        });
		
		oneDg.setActionCommand("onedg");
		
		JButton trim = new JButton("Trim");
        
		JButton radial = new JButton("Radial View");
        
		JButton force = new JButton("Dynamic View");
		
		
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
    
    public void actionPerformed(ActionEvent e){
    	if("onedg".equals(e.getActionCommand())){
    		System.out.println("HAHAA I am master of buttons");
    	}
    }
}
