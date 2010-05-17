package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

	import javax.swing.JFrame;

	import java.awt.event.MouseEvent;
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
	import prefuse.controls.Control;
	import prefuse.controls.ControlAdapter;
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

/**
 * 
 * @author Sergey
 *
 */
public class VisualCoAuthorExplorer {

	
	public static final String HOSTNAME = "attu2.cs.washington.edu";
	private Graph coAuthors;
	private Visualization colorLayoutVis;
	private Display dispCtrls;
	public String name;
	

	
	/**
	 * Creates a new initial visualization with the authorName node in the center
	 * of all its children.
	 * @param authorName 
	 */
	public VisualCoAuthorExplorer(String authorName){
		

		graphInit(authorName);
		visualizationInit(coAuthors);
		displayInit(this.colorLayoutVis);
		name = authorName;
    }
	
	
	/**
	 * @return the underlying graph data structure
	 */
	public Graph getGraph(){
		return this.coAuthors;
		
	}
	
	/**
	 * @return the Visualization that sets all the colors and node layouts
	 */
	public Visualization getVisualization(){
		return this.colorLayoutVis;
	}
	
	/**
	 * Display is added into a JFrame/other gui object
	 * @return the display for this graph visualization
	 */
	public Display getDisplay(){
		return this.dispCtrls;
	}
	
	
	/**
	 * use this to update the graph display when there are any changes to the 
	 * underlying data structures, i.e. adding nodes to the table
	 */
	public void updateVis(){
		colorLayoutVis.run("color");
		colorLayoutVis.run("layout");

	}
	
	
	/**
	 * private method to add the co-authors of the passed authorName and visualize them
	 * @todo currently accesses the server and gets a list of co-author names; still need to 
	 * figure out how to coherently add the names to the graph
	 * @param authorName
	 */
	private void addCoAuthors(String authorName){
		
		List<String> moreAuthors = getCoAuthorList(authorName);        
		
		System.out.println(moreAuthors);
	}
	
	
	/**
	 * sends a request to the backend for a list of co-authors of the passed authorName
	 * @param authorName 
	 * @return a list of co-author names for the given author
	 */
	private List<String> getCoAuthorList(String authorName){
		// setup of database connections
		CoauthorDataServiceInterface c;
		try {
			c = (CoauthorDataServiceInterface) Naming.lookup("rmi://" + HOSTNAME + "/"
					+ CoauthorDataServiceInterface.SERVICE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	
		String centerNode = authorName; // CHANGE THIS VARIABLE TO CHANGE THE SEARCH/VISUALIZATION RESULTS
		List<String> coAu = null;
		try{
			coAu = c.getCoauthors(centerNode);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return coAu;
	}
	
	
	/**
	 * initializes the coAuthors Graph around authorName
	 * 
	 * only used in constructor
	 * @param authorName
	 */
	private void graphInit(String authorName){
		
		String centerNode = authorName; // CHANGE THIS VARIABLE TO CHANGE THE SEARCH/VISUALIZATION RESULTS
		List<String> coAu = getCoAuthorList(authorName);
		Table nodes = new Table();
		nodes.addColumn("name", String.class);

		// table of edges; source and target refer to row index in the table of
		// nodes
		Table e = new Table();
//			System.out.println(nodes);
		e.addColumn("source", int.class);
		e.addColumn("target", int.class);
		
//			System.out.println(e.getSchema());
	  	
		// Add Searched-for author to table of Nodes
		nodes.addRow();
		nodes.setString(0,"name",centerNode); 

	  	// add all co-Authors to table
	  	// for each co-Author, add an edge linking him/her to the first
	  	// entry in the Authors table
		// populate the both the author and edges table
		for(int i = 1; i < coAu.size() + 1; i++){
	  		nodes.addRow();
	  		e.addRow();
	  		nodes.setString(i, "name", coAu.get(i-1));
	  		e.setInt(i-1, "source", 0);
	  		e.setInt(i-1, "target", i);
	  	}

		this.coAuthors = new Graph(nodes,e,false);
	}
	
	/**
	 * initializes the visualization parameters
	 * 
	 * only used in constructor
	 * @param initialGraph
	 */
	private void visualizationInit(Graph initialGraph){
		  	
        // add the graph to the visualization as the data group "graph"
        // nodes and edges are accessible as "graph.nodes" and "graph.edges"
		
		Visualization vis = new Visualization();
	    VisualGraph vg  = (VisualGraph) vis.add("graph", initialGraph);
	    vis.setInteractive("graph.edges", null, true);
	    
	    // -- 3. the renderers and renderer factory ---------------------------
	    
	    // draw the "name" label for NodeItems
	    LabelRenderer r = new LabelRenderer("name");
	    r.setRoundedCorner(8, 8); // round the corners
	    	    
	    // create a new default renderer factory
	    // return our name label renderer as the default for all non-EdgeItems
	    // includes straight line edges for EdgeItems by default
	    vis.setRendererFactory(new DefaultRendererFactory(r));
	      
	    // -- 4. the processing actions ---------------------------------------
	    
	    
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
	    
	    NodeItem focus = (NodeItem) vg.getNode(0);
	    PrefuseLib.setX(focus, null, 700);
	    PrefuseLib.setY(focus, null, 700);
	    
	    this.colorLayoutVis = vis;

	}
	
	
	/**
	 * initializes the display 
	 * 
	 * only used in constructor
	 * @param initialVis
	 */
	private void displayInit(Visualization initialVis){
		  
        // -- 5. the display and interactive controls -------------------------
        
        Display d = new Display(initialVis);
        // control to detect when a node has been clicked on and perform the appropriate actions
        
        Control nodeClicked = new ControlAdapter(){
        	public void itemClicked(VisualItem item, MouseEvent e ){
        		String clickedOn = item.getString("name");
        		name = clickedOn;
        		System.out.println(clickedOn);
        		addCoAuthors(clickedOn);
           	}
        };
        
        d.setSize(500, 500); 
        d.addControlListener(new DragControl());
        d.addControlListener(new PanControl()); 
        d.addControlListener(new ZoomControl());
        d.addControlListener(new WheelZoomControl());
        d.addControlListener(new ZoomToFitControl());
        d.addControlListener(nodeClicked);
        
        this.dispCtrls = d;

	}
}


