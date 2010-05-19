package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

	import java.util.List;
	import prefuse.data.Graph;
	import prefuse.data.Table;

/**
 * 
 * @author Sergey
 *
 */
public class VisualCoAuthorExplorer extends VisualExplorer {

	public String curSelected;

	
	/**
	 * Creates a new initial visualization with the authorName node in the center
	 * of all its children.
	 * @param authorName 
	 */
	public VisualCoAuthorExplorer(String authorName){
		
		graphInit(authorName);
		visualizationInit(coAuthors);
		displayInit(this.colorLayoutVis);
		curSelected = authorName;
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
		
		System.out.println("Number of Authors returned: " + coAu.size());
		// table of edges; source and target refer to row index in the table of
		// nodes
		Table e = new Table();
//			System.out.println(nodes);
		e.addColumn("source", int.class);
		e.addColumn("target", int.class);
		
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

}


