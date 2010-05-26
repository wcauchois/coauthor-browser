package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

/**
 * @author Sergey
 * abstract parent class for the various visualization objects; contains
 * methods common to all visualizations (initializing display/visualization,
 * adding co-authors to a single node, querying the backend for co-authors of
 * a single node, and redrawing the visualization
 */
public abstract class VisualExplorer {

	public static final String HOSTNAME = "attu2.cs.washington.edu";
	protected Graph coAuthors;
	protected Visualization colorLayoutVis;
	protected Display dispCtrls;
	protected CoauthorDataServiceInterface backend;
	
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
	 * Sets up a connection to the backend 
	 */
	public void databaseInit(){
	
		try {
			backend = (CoauthorDataServiceInterface) Naming.lookup("rmi://" + HOSTNAME + "/"
					+ CoauthorDataServiceInterface.SERVICE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	
	
	/**
	 * sends a request to the backend for a list of co-authors of the passed authorName
	 * @param authorName 
	 * @return a list of co-author names for the given author
	 */
	protected List<String> getCoAuthorList(String authorName){
		String centerNode = authorName; 
		List<String> coAu = null;
		try{
			coAu = backend.getCoauthors(centerNode);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return coAu;
	}
	
	
	/**
	 * protected method to add the co-authors of the passed authorName and visualize them
	 * figure out how to coherently add the names to the graph
	 * @param authorName
	 */
	protected void addCoAuthors(String authorName){
		List<String> moreAuthors = getCoAuthorList(authorName);        
		Node clickedOn = findAuthor(authorName);
		
		// look for returned authors already in the graph; remove all authors already in
		// the graph from the search results
		Iterator authItr = this.coAuthors.nodes();
		while(authItr.hasNext()){
			Node current = (Node) authItr.next();
			if(moreAuthors.contains(current.get("name"))){ // the graph already contains a node for one of the returned coauthors 
				this.coAuthors.addEdge(clickedOn, current);	// add an edge between the clicked-on node and the coauthor node it has in the graph
				moreAuthors.remove(current.get("name"));
			}
		}
		
		// add nodes for all query-returned co-authors that were not originally in the graph
		Iterator coAuItr = moreAuthors.iterator();
		while(coAuItr.hasNext()){
			String current = (String) coAuItr.next();
			Node added = this.coAuthors.addNode();
			added.set("name", current);
			this.coAuthors.addEdge(clickedOn, added);
		}
		this.updateVis();
	}
	
	/**
	 * used to find the Node in the prefuse graph that has authorName in it's name
	 * @param authorName 
	 * @return 
	 */
	protected Node findAuthor(String authorName){
		Iterator authItr = this.coAuthors.nodes();
		Node searchedFor = null;
		while(authItr.hasNext()){
			Node current = (Node) authItr.next();
			if(current.get("name").equals(authorName)){
				searchedFor = current;
				System.out.println("Author node found! ID is: " + searchedFor.getRow());
			}
		}
		return searchedFor;
	}
	
	/**
	 * adds coauthor nodes to all nodes currently in the graph
	 */
	public void addCoAuthorsToAllNodes(){
		System.out.println("Adding coauthors to all nodes in graph");
		Iterator graphItr = this.coAuthors.nodes();
		
		int nodeCt = this.coAuthors.getNodeCount();
		
		for (int i = 0; i < nodeCt; i++){
			Node current = (Node) graphItr.next();
			this.addCoAuthors((String) current.get("name"));
		}
		this.updateVis();
	}
	
	
	/**
	 * removes all children at the node with the passed authorName, assumes that 
	 * input is a valid node in the graph
	 * @param authorName
	 */
	public void removeCoAuthors(String authorName){
		
		Node removeCoAuthorsFrom = findAuthor(authorName);
	
		Iterator childItr = removeCoAuthorsFrom.children();
		while(childItr.hasNext()){
			Node currentNode = (Node) childItr.next();
			System.out.println("This node will be deleted: " + currentNode.getString("name"));
			if (currentNode.getChildCount() > 0)
				this.removeCoAuthors(currentNode.getString("name"));
			this.coAuthors.removeNode(currentNode);

		}
		System.out.println("You are now removing the co-authors of " + authorName);

	}

	/**
	 * removes all nodes that have degree 0 from the graph
	 */
	public void trimOneDegree(){
		
		Iterator nodeItr = this.coAuthors.nodes();
		List<Integer> toBeDeleted = new ArrayList<Integer>();
		while(nodeItr.hasNext()){
			Node currentNode = (Node) nodeItr.next();
			System.out.println("This node will be deleted: " + currentNode.getString("name"));
			if (currentNode.getDegree() == 1){
				System.out.println("Degree of node is: " + currentNode.getDegree());
				System.out.println("The name at this node is " + this.coAuthors.getNode(currentNode.getRow()).getString("name"));
				toBeDeleted.add(currentNode.getRow());
			}	
		}
		
		for(int i =0; i<toBeDeleted.size();i++){
			this.coAuthors.removeNode(toBeDeleted.get(i));
		}
		
	}
	
	/**
	 * use this to update the graph display when there are any changes to the 
	 * underlying data structures, i.e. adding nodes to the table
	 */
	public void updateVis(){
		colorLayoutVis.run("layout");
		colorLayoutVis.run("draw");
	}
	

	/**
	 * initializes the visualization parameters
	 * 
	 * only used in constructor
	 * @param initialGraph
	 */
	protected void visualizationInit(Graph initialGraph){
		  	
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
	 
	    
	    /*
	//    ColorAction fill = new ColorAction("graph.nodes",
	 //   		 VisualItem.FILLCOLOR, ColorLib.rgb(190, 215, 206));
	    // use black for node text
	    ColorAction text = new ColorAction("graph.nodes",
	            VisualItem.TEXTCOLOR, ColorLib.gray(0));
	    // use light grey for edges
	    ColorAction edges = new ColorAction("graph.edges",
	            VisualItem.STROKECOLOR, ColorLib.gray(200));
	    
	  
	    
        ColorAction highlight = new ColorAction("graph.nodes",
        		VisualItem.FILLCOLOR, ColorLib.rgb(190, 215, 206));
        highlight.add("_fixed", ColorLib.rgb(255,0,0));
  //      highlight.add("_highlight", ColorLib.rgb(255,0,255));
	    
	    // create an action list containing all color assignments
	    ActionList draw = new ActionList();
	//    draw.add(fill);
	    draw.add(text);
	    draw.add(edges);
	    draw.add(new ColorAction("graph.edges", VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200)));
	    
	    // create an action list with an animated layout
	    ActionList animate = new ActionList(Activity.INFINITY);
	    animate.add(new ForceDirectedLayout("graph"));
	    animate.add(highlight);
	    
	    animate.add(new RepaintAction());
	    
	    // add the actions to the visualization
	    vis.putAction("draw", draw);
	    vis.putAction("animate", animate);
	    vis.runAfter("draw", "animate");
	    
	    */
	    
 // -- set up the actions ----------------------------------------------
        
        int maxhops = 4, hops = 4;
        ColorAction fill = new ColorAction("graph", 
                VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
        fill.add("_fixed", ColorLib.rgb(255,0,255));
        fill.add("_highlight", ColorLib.rgb(255,100,100));

        ActionList draw = new ActionList();
        draw.add(new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.green(1)));
        draw.add(new ColorAction("graph.nodes", VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
        draw.add(new ColorAction("graph.edges", VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200)));
        draw.add(new RepaintAction());
        
        ForceDirectedLayout fdl = new ForceDirectedLayout("graph");
        ForceSimulator fsim = fdl.getForceSimulator();
        fsim.getForces()[0].setParameter(0, -1.2f);
        
        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(fdl);
        animate.add(fill);
        animate.add(new RepaintAction());
        
        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        vis.putAction("draw", draw);
        vis.putAction("layout", animate);
        vis.runAfter("draw", "layout");
	    
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
	protected void displayInit(Visualization initialVis){
		  
        // -- 5. the display and interactive controls -------------------------
        
        Display d = new Display(initialVis);
        // control to detect when a node has been clicked on and perform the appropriate actions
        
        Control nodeClicked = new ControlAdapter(){
        	public void itemClicked(VisualItem item, MouseEvent e ){
        		String clickedOn = item.getString("name");
        		System.out.println(clickedOn);
        		addCoAuthors(item.getString("name"));
           	}
        	public void keyTyped(java.awt.event.KeyEvent e){
        		if(e.getKeyChar() == '1'){
        			addCoAuthorsToAllNodes();
        		}else if(e.getKeyChar() == '2'){
        			trimOneDegree();
        		}
        	}
        	public void itemKeyTyped(VisualItem item, java.awt.event.KeyEvent e){
        		if(e.getKeyChar() == '2'){
        			removeCoAuthors(item.getString("name"));
        		}
        	}
        };
        
        d.setSize(500, 500); 
        d.addControlListener(new DragControl());
        d.addControlListener(new PanControl()); 
        d.addControlListener(new ZoomControl(1));
        d.addControlListener(new WheelZoomControl());
        d.addControlListener(new ZoomToFitControl());
        d.addControlListener(new NeighborHighlightControl());
        d.addControlListener(new FocusControl(2));
        d.addControlListener(nodeClicked);
        
        this.dispCtrls = d;

	}
}
