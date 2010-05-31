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
        frame.setContentPane(demo(vx.getVisualization(), "name"));
		frame.add(vx.getDisplay());
		frame.pack();           // layout components in window
		frame.setVisible(true); // show the window
		vx.updateVis();

	}
	
    public static JPanel demo(Visualization vis, final String label) {        
        // create a new radial tree view
    //    Visualization vis = gview.getVisualization();
        
        // create a search panel for the tree map
/*        SearchQueryBinding sq = new SearchQueryBinding(
             (Table)vis.getGroup("graph"), label,
             (SearchTupleSet)vis.getGroup(Visualization.SEARCH_ITEMS));
        JSearchPanel search = sq.createSearchPanel();
        search.setShowResultCount(true);
        search.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
        search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
     */   
        final JFastLabel title = new JFastLabel("                 ");
        title.setPreferredSize(new Dimension(350, 20));
        title.setVerticalAlignment(SwingConstants.BOTTOM);
        title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
        title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
        
      /*  gview.addControlListener(new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent e) {
                if ( item.canGetString(label) )
                    title.setText(item.getString(label));
            }
            public void itemExited(VisualItem item, MouseEvent e) {
                title.setText(null);
            }
        });*/
        
        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalStrut(10));
        box.add(title);
        box.add(Box.createHorizontalGlue());
    //    box.add(search);
        box.add(Box.createHorizontalStrut(3));
        
        JPanel panel = new JPanel(new BorderLayout());
    //    panel.add(gview, BorderLayout.CENTER);
        panel.add(box, BorderLayout.SOUTH);
        
        Color BACKGROUND = Color.WHITE;
        Color FOREGROUND = Color.DARK_GRAY;
        UILib.setColor(panel, BACKGROUND, FOREGROUND);
        
        return panel;
    }
}
