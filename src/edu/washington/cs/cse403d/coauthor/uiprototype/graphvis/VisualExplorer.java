package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.CircleLayout;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.Layout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.BalloonTreeLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;
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
		Node addTo = findAuthor(authorName);
		
		if(addTo == null){
			addTo = this.coAuthors.addNode();
			addTo.setString("name", authorName);
			addTo.setInt("visited", 1);
		}
		
		// look for returned authors already in the graph; remove all authors already in
		// the graph from the search results
		Iterator authItr = this.coAuthors.nodes();
		while(authItr.hasNext()){
			Node current = (Node) authItr.next();
			if(moreAuthors.contains(current.get("name"))){ // the graph already contains a node for one of the returned coauthors 
				this.coAuthors.addEdge(addTo, current);	// add an edge between the clicked-on node and the coauthor node it has in the graph
				moreAuthors.remove(current.get("name"));
			}
		}
		
		// add nodes for all query-returned co-authors that were not originally in the graph
		Iterator coAuItr = moreAuthors.iterator();
		while(coAuItr.hasNext()){
			String current = (String) coAuItr.next();
			Node added = this.coAuthors.addNode();
			added.set("name", current);
			this.coAuthors.addEdge(addTo, added);
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
		if(nodeCt < 100){
			for (int i = 0; i < nodeCt; i++){
				Node current = (Node) graphItr.next();
				this.addCoAuthors((String) current.get("name"));
			}
			this.updateVis();
		}else {
			System.out.println("Too many nodes in graph to add co-authors to all!");
		}
	}
	
	/**
	 * removes the passed parameter node from the current graph
	 * @param toBeRemoved
	 */
	protected void removeNode(int toBeRemoved){
		if(this.coAuthors.getNode(toBeRemoved).getChildCount() == 0){
			this.coAuthors.removeNode(toBeRemoved);
		}else{
			System.out.println("This node has children; it cannot be removed!");
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
	protected void visualizationInit(){
		  	
        // add the graph to the visualization as the data group "graph"
        // nodes and edges are accessible as "graph.nodes" and "graph.edges"
		
		Visualization vis = new Visualization();
	    VisualGraph vg  = (VisualGraph) vis.add("graph", this.coAuthors);
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
	
	    
 // -- set up the actions ----------------------------------------------
        
        int maxhops = 4, hops = 4;
        
        ActionList animate = setupAnimate("fdl");
       

        ActionList draw = new ActionList();
        draw.add(new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.green(1)));
        draw.add(new ColorAction("graph.nodes", VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
        draw.add(new ColorAction("graph.edges", VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200)));
        draw.add(new RepaintAction());
        
        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        vis.putAction("draw", draw);
        vis.putAction("layout", animate);
        vis.runAfter("draw", "layout");
	    
/*	    NodeItem focus = (NodeItem) vg.getNode(0);
    	PrefuseLib.setX(focus, null, 700);
 	    PrefuseLib.setY(focus, null, 700);*/
	    
	    this.colorLayoutVis = vis;
	}
	
	protected ActionList setupAnimate(String layout){
		int[] palette = new int[] {
                ColorLib.rgb(190,190,255),ColorLib.rgb(255,180,180)
            };
            // map nominal data values to colors using our provided palette
        DataColorAction fill = new DataColorAction("graph.nodes", "visited",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        fill.add("_fixed", ColorLib.rgb(255,0,255));
        fill.add("_highlight", ColorLib.rgb(255,100,100));
       
        // default behavior returns a force-directed layout
        Layout fdl = new ForceDirectedLayout("graph");
        ForceSimulator fsim = ((ForceDirectedLayout) fdl).getForceSimulator();
        fsim.getForces()[0].setParameter(0, -1.2f);
        
        if(layout.equals("radial")){
        	fdl = new RadialTreeLayout("graph");
        }
        
        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(fdl);
        animate.add(fill);
        animate.add(new RepaintAction());
        
        return animate;
	}
	
	public void switchToRadialLayout(){
	 //	Action curLayout = this.colorLayoutVis.getAction("layout");
	 	this.colorLayoutVis.removeAction("layout");
	 	
	 	ActionList animate = setupAnimate("radial");
		this.colorLayoutVis.putAction("layout", animate);
		this.updateVis();
	}
	
	public void switchToFDL(){
	 	this.colorLayoutVis.removeAction("layout");
        
	 	ActionList animate = this.setupAnimate("fdl");
		this.colorLayoutVis.putAction("layout", animate);
		this.updateVis();
	}
	
	/**
	 * initializes the display 
	 * 
	 * only used in constructor
	 * @param initialVis
	 */
	protected void displayInit(){
		  
        // -- 5. the display and interactive controls -------------------------
        
        Display d = new Display(this.colorLayoutVis);
        // control to detect when a node has been clicked on and perform the appropriate actions
        
        Control nodeClicked = new ControlAdapter(){
        	public void itemClicked(VisualItem item, MouseEvent e ){
        		String clickedOn = item.getString("name");
        		Node ckOn = coAuthors.getNode(item.getRow());
        		ckOn.set("visited", 1);
        		System.out.println(clickedOn + " visited status is: " + ckOn.getInt("visited"));
        		addCoAuthors(item.getString("name"));
      
           	}
        	public void keyTyped(java.awt.event.KeyEvent e){
        		if(e.getKeyChar() == '1'){
        			addCoAuthorsToAllNodes();
        		}else if(e.getKeyChar() == '2'){
        			trimOneDegree();
        		} else if(e.getKeyChar() == '4'){
        			switchToRadialLayout();
        		} else if(e.getKeyChar() == '5'){
        			switchToFDL();
        		}
        	}
        	public void itemKeyTyped(VisualItem item, java.awt.event.KeyEvent e){
        		if(e.getKeyChar() == '2'){
        			removeCoAuthors(item.getString("name"));
        		}else if(e.getKeyChar() == '3'){
        			removeNode(item.getRow());
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
        d.panTo(new Point(0,0));
	}
}
