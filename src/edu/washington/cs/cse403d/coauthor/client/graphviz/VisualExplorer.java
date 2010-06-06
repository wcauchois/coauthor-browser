package edu.washington.cs.cse403d.coauthor.client.graphviz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolTip;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.PolarLocationAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.FontAction;
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
import prefuse.activity.SlowInSlowOutPacer;
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
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.Force;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

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
	protected ActionList radialAnimateActionsArrangement;
	protected ActionList radialAnimateActionsSpacing;
	protected ForceDirectedLayout fdlLayout;
	protected ForceDirectedLayout spacingLayout;
	protected PrefixSearchTupleSet searchTupleSet;
	
	private boolean isInFDL;
	protected ActionList highlightControl;
	protected ActionList fdlAnimateActions;
	
	private String draw = "draw";
	
	private final int ADD_COAUTHORS_CAP = 100;
	private final int MAX_CONCURRENT_THREADS = 20;

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
	 * protected method to add the co-authors of the passed authorName and visualize them
	 * figure out how to coherently add the names to the graph
	 * @param authorName
	 */
	protected void addCoAuthors(String authorName){
	
		List<String> moreAuthors = getCoAuthorList(authorName);        
		synchronized (this.colorLayoutVis){
			
		Node addTo = findAuthor(authorName);
		System.out.println("VISITED STATUS: " + addTo.getInt("visited"));
		//addTo.setInt("visited", 1);
		
		// look for returned authors already in the graph; remove all authors already in
		// the graph from the search results
		System.out.println(moreAuthors);
		Iterator authItr = this.coAuthors.nodes();
		while(authItr.hasNext()){
			Node current = (Node) authItr.next();
			if(moreAuthors.contains(current.get("name"))){ // the graph already contains a node for one of the returned coauthors 
				if(this.coAuthors.getEdge(addTo, current) ==  null) {
					this.coAuthors.addEdge(addTo, current);	// add an edge between the clicked-on node and the coauthor node it has in the graph
				}
				moreAuthors.remove(current.get("name"));
			}
		}
		
		// add nodes for all query-returned co-authors that were not originally in the graph
			Iterator coAuItr = moreAuthors.iterator();
			while(coAuItr.hasNext()){
				String current = (String) coAuItr.next();
				Node added = this.coAuthors.addNode();
				added.set("name", current);
				added.set("visited",0);
				this.searchTupleSet.index(this.colorLayoutVis.getVisualItem("graph.nodes", added),"name");
				this.coAuthors.addEdge(addTo, added);
			}
			addTo.setInt("visited", 1);
	}
//		this.colorLayoutVis.removeAction("highlight");
//		this.colorLayoutVis.putAction("highlight",this.highlightControl);
		this.updateVis();
	}
	

	

	/**
	* adds coauthor nodes to all nodes currently in the graph
	*/
	public boolean addCoAuthorsToAllNodes(){
		System.out.println("Adding coauthors to all nodes in graph");
		Iterator graphItr = this.coAuthors.nodes();
	
		int nodeCt = this.coAuthors.getNodeCount();
	
		ExecutorService pool = Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS);
	
		if(nodeCt < ADD_COAUTHORS_CAP){
			for (int i = 0; i < nodeCt; i++){
				Node current = (Node) graphItr.next();
				final String nodeName = (String) current.get("name");
				pool.execute(new Runnable() {
					@Override
					public void run() {
						addCoAuthors(nodeName);
					}
				});
			}
			if(this.isInFDL){
				this.switchToFDL();
			}else{
				this.switchToRadialLayout();
			}
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * removes the passed parameter node from the current graph
	 * @param toBeRemoved
	 */
	protected void removeNode(int toBeRemoved){
		synchronized(this.colorLayoutVis){
		if(this.coAuthors.getNode(toBeRemoved).getChildCount() == 0){
			this.coAuthors.removeNode(toBeRemoved);
		}else{
			System.out.println("This node has children; it cannot be removed!");
		}
		this.updateVis();
	}
	}
	
	/**
	 * removes all children at the node with the passed authorName, assumes that 
	 * input is a valid node in the graph
	 * @param authorName
	 */
	public void removeCoAuthors(String authorName){
		synchronized(this.colorLayoutVis){
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

	}}

	/**
	 * removes all nodes that have degree 0 from the graph
	 */
	public void trimOneDegree(){
	synchronized (this.colorLayoutVis){
		
		Iterator nodeItr = this.coAuthors.nodes();
		List<Integer> toBeDeleted = new ArrayList<Integer>();
		while(nodeItr.hasNext()){
			Node currentNode = (Node) nodeItr.next();
			System.out.println("This node will be deleted: " + currentNode.getString("name"));
			if (currentNode.getDegree() == 1){
				System.out.println("Degree of node is: " + currentNode.getDegree());
				System.out.println("The name at this node is " + this.coAuthors.getNode(currentNode.getRow()).getString("name"));
				System.out.println("The row of current node is " + currentNode.getRow());
				toBeDeleted.add(currentNode.getRow());
			}	
		}
		
		for(int i =0; i<toBeDeleted.size();i++){
			Node deleted = this.coAuthors.getNode(toBeDeleted.get(i));
			System.out.println("@@@The name at node " + toBeDeleted.get(i) + " is " + deleted.getString("name"));
			this.coAuthors.removeNode(deleted);
		}
		
	}
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
		
		this.colorLayoutVis = new Visualization();
	    VisualGraph vg  = (VisualGraph) this.colorLayoutVis.add("graph", this.coAuthors);
	    this.colorLayoutVis.setInteractive("graph.edges", null, false);
	    
	    // -- 3. the renderers and renderer factory ---------------------------
	    
	    // draw the "name" label for NodeItems
	    LabelRenderer labels = new LabelRenderer("name");
	    labels.setRoundedCorner(8, 8); // round the corners
	    EdgeRenderer edges = new EdgeRenderer();
	    
	    DefaultRendererFactory initialRf = new DefaultRendererFactory(labels);
	    initialRf.add(new InGroupPredicate("graph.edges"), edges);
	    
	    this.colorLayoutVis.setRendererFactory(initialRf);

        initFdlAnimation();
        initRadialAnimation();

        ActionList draw = new ActionList();
        draw.add(new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.green(1)));
        draw.add(new ColorAction("graph.nodes", VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
        draw.add(new ColorAction("graph.edges", VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200)));
        draw.add(new RepaintAction());
        
		int[] palette = new int[] {
                ColorLib.rgb(190,190,255),ColorLib.rgb(255,180,180)
            };
            // map nominal data values to colors using our provided palette
        DataColorAction fill = new DataColorAction("graph.nodes", "visited",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        fill.add("_fixed", ColorLib.rgb(255,0,255));
        fill.add("ingroup('_search_')", ColorLib.rgb(100,240,75));
    
        fill.add("_highlight", ColorLib.rgb(255,100,100));
        
        this.highlightControl = new ActionList(Activity.INFINITY);
        highlightControl.add(new RepaintAction());
        highlightControl.add(fill);

        
        searchTupleSet = new PrefixSearchTupleSet();
        searchTupleSet.setDelimiterString(", ");
        this.colorLayoutVis.addFocusGroup(Visualization.SEARCH_ITEMS, searchTupleSet);
        searchTupleSet.addTupleSetListener(new TupleSetListener(){
        	@Override
				public void tupleSetChanged(TupleSet arg0, Tuple[] arg1,
						Tuple[] arg2) {
					System.out.println("Tupule Set has changed!");
					colorLayoutVis.cancel("highlight");
					colorLayoutVis.run("highlight");
					colorLayoutVis.repaint();
        		}
        	});
        
        this.colorLayoutVis.putAction("draw", draw);
        this.colorLayoutVis.putAction("highlight", highlightControl);
        this.colorLayoutVis.runAfter(this.draw,"highlight");
        this.switchToFDL();

	}
	
		protected void initFdlAnimation(){

        // default behavior returns a force-directed layout
        fdlLayout = new ForceDirectedLayout("graph");
        ForceSimulator fsim = ((ForceDirectedLayout) fdlLayout).getForceSimulator();
        fsim.getForces()[0].setParameter(0, -8f);
        fsim.getForces()[0].setParameter(1, 200);
        
        fsim.getForces()[1].setParameter(0, .02f);
        fsim.getForces()[2].setParameter(1, 100);
        
        Force[] forces = fsim.getForces();
                     
        ActionList fdlAnimate = new ActionList(10000);
        fdlAnimate.setPacingFunction(new SlowInSlowOutPacer());
        fdlAnimate.add(fdlLayout);

        this.fdlAnimateActions = fdlAnimate;
	}
	
	protected void initRadialAnimation(){
 
        Layout radialLayout = new RadialTreeLayout("graph");
       
        ActionList arrangement = new ActionList(100);
        arrangement.add(radialLayout);
        
        
        /*spacingLayout = new ForceDirectedLayout("graph");
        ForceSimulator fsim = ((ForceDirectedLayout) spacingLayout).getForceSimulator();
        fsim.getForces()[0].setParameter(0, -.1f);
        fsim.getForces()[0].setParameter(1, 20);
        
        fsim.getForces()[1].setParameter(0, .001f);
        fsim.getForces()[2].setParameter(0, 0.0000000001f);
        fsim.getForces()[2].setParameter(1, 5);
        
        Force[] forces = fsim.getForces();
        spacingLayout.setPacingFunction((new SlowInSlowOutPacer()));
        */
        ActionList spacing = new ActionList(500);
        spacing.add(this.spacingLayout);
        
        this.radialAnimateActionsArrangement = arrangement;
       // this.radialAnimateActionsSpacing = spacing;
        
	}

	
	/**
	 * use this to update the graph display when there are any changes to the 
	 * underlying data structures, i.e. adding nodes to the table
	 */
	public void updateVis(){
		
			
		colorLayoutVis.run(draw);
		if (!this.isInFDL){
			this.colorLayoutVis.run("arrange");
		//	this.colorLayoutVis.run("spacing");
		}else{
			this.colorLayoutVis.run("ForceLayout");
		}
		
		colorLayoutVis.setInteractive("graph.edges", null, false);
		
	}
	
	
	public void switchToRadialLayout(){
	 
	 	this.colorLayoutVis.removeAction("ForceLayout");
	 	
	
		
	 	this.colorLayoutVis.putAction("arrange", this.radialAnimateActionsArrangement);
//		this.colorLayoutVis.putAction("spacing", this.radialAnimateActionsSpacing);
//	 	this.colorLayoutVis.alwaysRunAfter("arrange","spacing");
        this.colorLayoutVis.runAfter(draw, "arrange");
	 	this.updateVis();
	 	this.isInFDL = false;
	}
	
	public void switchToFDL(){
	 	this.colorLayoutVis.removeAction("spacing");
        this.colorLayoutVis.removeAction("arrange");
        
//	 	ActionList animate = this.setupAnimate("fdl");
		this.colorLayoutVis.putAction("ForceLayout", this.fdlAnimateActions);
        this.colorLayoutVis.runAfter(draw, "ForceLayout");
		this.updateVis();
		this.isInFDL = true;
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
        
        JToolTip tip = d.createToolTip();
        
        this.dispCtrls = d;
        d.panTo(new Point(0,0));
	}
	
	public JSplitPane forceTweaking(){
	    ForceSimulator fsim = ((ForceDirectedLayout)this.fdlAnimateActions.get(0)).getForceSimulator();
        JForcePanel fpanel = new JForcePanel(fsim);


        
        Box cf = new Box(BoxLayout.Y_AXIS);
      //  cf.add(slider);
        cf.setBorder(BorderFactory.createTitledBorder("Connectivity Filter"));
        fpanel.add(cf);

 
        
        fpanel.add(Box.createVerticalGlue());
        
        JSplitPane split = new JSplitPane();
        split.setLeftComponent(this.dispCtrls);
        split.setRightComponent(fpanel);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(false);
        split.setDividerLocation(700);
        
        return split;
	}
}
