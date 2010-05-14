package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

import javax.swing.JFrame;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;


import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.util.TableIterator;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class PrefuseVisualization {
	
	public static final String HOSTNAME = "attu2.cs.washington.edu";

	public static void main(String[] args) {
	
		// setup of database connections
	/*	CoauthorDataServiceInterface c;
		try {
			c = (CoauthorDataServiceInterface) Naming.lookup("rmi://" + HOSTNAME + "/"
					+ CoauthorDataServiceInterface.SERVICE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
    	
		String centerNode = "Tom Anderson"; // CHANGE THIS VARIABLE TO CHANGE THE SEARCH/VISUALIZATION RESULTS
		List<String> coAu = null;
		try{
			coAu = c.getCoauthors(centerNode);
		} catch (RemoteException e) {
			e.printStackTrace();
		}*/
		
		String centerNode = "Jeff Prouty"; // CHANGE THIS VARIABLE TO CHANGE THE SEARCH/VISUALIZATION RESULTS
		List<String> coAu = new ArrayList<String>();
		//coAu.add(centerNode);
		coAu.add("Sergey Alekhnovich");
		coAu.add("Miles Sackler");
		coAu.add("Bill Cauchois");
		coAu.add("Kevin Bang");
		coAu.add("unicorns");
		Table nodes = new Table();
		nodes.addColumn("name", String.class);

		// table of edges; source and target refer to row index in the table of
		// nodes
		Table e = new Table();
//		System.out.println(nodes);
		e.addColumn("source", int.class);
		e.addColumn("target", int.class);
		
//		System.out.println(e.getSchema());
	  	
		// Add Searched-for author to table of Nodes
		nodes.addRow();
		nodes.setString(0,"name",centerNode); 

		
		//	  	System.out.println(nodes);

	  	//		System.out.println(nodes.getSchema());
//	  	System.out.println(nodes.getString(0, "name"));
//	  	System.out.println(crow);
		
	  	// add all co-Authors to table
	  	// for each co-Author, add an edge linking him/her to the first
	  	// entry in the Authors table
	  	
		// populate the both the author and edges table
		for(int i = 1; i < coAu.size() + 1; i++){
	//  		System.out.println(coAu.get(i-1));
	  		nodes.addRow();
	  		e.addRow();
	  		nodes.setString(i, "name", coAu.get(i-1));
	  		e.setInt(i-1, "source", 0);
	  		e.setInt(i-1, "target", i);
	  	}
	  	
	  	
	/*  	TableIterator itr = e.iterator();
		itr.next();
		
		while(itr.hasNext()){
			System.out.println(itr.get("source") + " -> " + itr.get("target"));
			itr.next();
		} // debugging purposes
	*/	
// -- 1. load the data ------------------------------------------------
	       Graph coAuthors = new Graph(nodes,e,false); 
	       
	   
	        // -- 2. the visualization --------------------------------------------
	        
	        // add the graph to the visualization as the data group "graph"
	        // nodes and edges are accessible as "graph.nodes" and "graph.edges"
	        Visualization vis = new Visualization();
	        VisualGraph vg  = (VisualGraph) vis.add("graph", coAuthors);
	        vis.setInteractive("graph.edges", null, true);
	        
	        // -- 3. the renderers and renderer factory ---------------------------
	        
	        // draw the "name" label for NodeItems
	        LabelRenderer r = new LabelRenderer("name");
	   //     EdgeRenderer e = new EdgeRenderer(Constants.EDGE_TYPE_LINE);
	        r.setRoundedCorner(8, 8); // round the corners
	        
	        
	        // create a new default renderer factory
	        // return our name label renderer as the default for all non-EdgeItems
	        // includes straight line edges for EdgeItems by default
	        vis.setRendererFactory(new DefaultRendererFactory(r));
	        
	        
	        // -- 4. the processing actions ---------------------------------------
	        
	        // create our nominal color palette
	        // pink for females, baby blue for males
	        int[] palette = new int[] {
	            ColorLib.rgb(255,180,180), ColorLib.rgb(190,190,255)
	        };
	        // map nominal data values to colors using our provided palette
	        ColorAction fill = new ColorAction("graph.nodes",
	        		 VisualItem.FILLCOLOR, ColorLib.rgb(190, 215, 206));
	        // use black for node text
	        ColorAction text = new ColorAction("graph.nodes",
	                VisualItem.TEXTCOLOR, ColorLib.gray(0));
	        // use light grey for edges
	        ColorAction edges = new ColorAction("graph.edges",
	                VisualItem.STROKECOLOR, ColorLib.gray(200));
	        
	        // create an action list containing all color assignments
	        ActionList color = new ActionList();
	        color.add(fill);
	        color.add(text);
	        color.add(edges);
	        
	        // create an action list with an animated layout
	        ActionList layout = new ActionList(Activity.INFINITY);
	        layout.add(new ForceDirectedLayout("graph"));
	        layout.add(new RepaintAction());
	        
	        // add the actions to the visualization
	        vis.putAction("color", color);
	        vis.putAction("layout", layout);
	        
	        
	        // -- 5. the display and interactive controls -------------------------
	        
	        Display d = new Display(vis);
	        d.setSize(500, 500); // set display size
	        // drag individual items around
	        d.addControlListener(new DragControl());
	        // pan with left-click drag on background
	        d.addControlListener(new PanControl()); 
	        // zoom with right-click drag
	       d.addControlListener(new ZoomControl());
	       // zoom with scroll wheel
	       d.addControlListener(new WheelZoomControl());
	       // right click to zoom to full frame view of graph
	       d.addControlListener(new ZoomToFitControl());

	        // -- 6. launch the visualization -------------------------------------
	        NodeItem focus = (NodeItem) vg.getNode(0);
	        PrefuseLib.setX(focus, null, 1000);
	        PrefuseLib.setY(focus, null, 1000);
	        // create a new window to hold the visualization
	       JFrame frame = new JFrame("prefuse example");
	       frame.setBounds(800, 100, 1, 1);
	            // ensure application exits when window is closed
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.add(d);
	        frame.pack();           // layout components in window
	        frame.setVisible(true); // show the window
	        // assign the colors
	        vis.run("color");
	        // start up the animated layout
	        vis.run("layout");
	        
	        // Demonstrates dynamically adding nodes to the graph
	        // scanner for user input
	        Scanner read = new Scanner(System.in);
	        System.out.println("Would you like to visualize more nodes?");
	        String ans = read.next();
	        
	        if(ans.equals("yes")){
	        	// for loop for great justice
				for(int i = 0; i <40 ; i++){
					// code to add the actual nodes
					int aN = coAuthors.addNodeRow();
					Node b = coAuthors.getNode(aN);
					b.set("name", "Mr. Notkin");
					coAuthors.addEdge(i%5, aN);
				}
			}
	        vis.run("color");
	        vis.run("layout");
		        
	    }
}
