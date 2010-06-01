package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
//		VisualCoAuthorExplorer vx = new VisualCoAuthorExplorer(s);
		VisualChainExplorer vx = new VisualChainExplorer("Jessica Miller", "Ross Tate");
		
/*		List<String> authors = new ArrayList<String>();
		authors.add("Marty Stepp");
		authors.add("David Notkin");
		authors.add("Paul Anderson");*/
		//authors.add()
		//VisualMultipleAuthorExplorer vx = new VisualMultipleAuthorExplorer(authors);
		
		JFrame frame = new JFrame("prefuse example");
		frame.setBounds(800, 100, 1, 1);
	    // ensure application exits when window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(demo(vx, "name"));
		frame.add(vx.getDisplay());
		frame.pack();           // layout components in window
		frame.setVisible(true); // show the window
		vx.updateVis();

	}
	
    public static JPanel demo(VisualExplorer vx, String label){
    	
    	
    	JPanel panel = new JPanel(new BorderLayout());
    	
		final JFastLabel title = new JFastLabel("       Fish          ");
		title.setPreferredSize(new Dimension(350, 20));
		title.setVerticalAlignment(SwingConstants.BOTTOM);
		title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
		title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
        
		JButton butt1 = new JButton("Blah");
		
		JButton butt2 = new JButton("poop");
        
		Box box = new Box(BoxLayout.X_AXIS);
        box.add(title);
        box.add(butt1);
        box.add(butt2);
        
        box.add(Box.createHorizontalStrut(10));
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalStrut(3));
        panel.add(box, BorderLayout.SOUTH);
        
        Color BACKGROUND = Color.WHITE;
        Color FOREGROUND = Color.DARK_GRAY;
        UILib.setColor(panel, BACKGROUND, FOREGROUND);
    	
    	
    	return panel;
    }
}
