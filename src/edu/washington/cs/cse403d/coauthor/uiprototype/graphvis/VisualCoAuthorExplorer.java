package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

	import java.util.ArrayList;
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
		databaseInit();
		List<String> initialCoAuthorList = getCoAuthorList(authorName);
		constructorHelper(authorName, initialCoAuthorList);
	}
	
	public VisualCoAuthorExplorer(String authorName, List<String> initialCoAuthorList){
		databaseInit();
		constructorHelper(authorName, initialCoAuthorList);
	}
	
	public void constructorHelper(String authorName, List<String> initialCoAuthorList){
		graphInit(authorName, initialCoAuthorList);
		visualizationInit(coAuthors);
		displayInit(this.colorLayoutVis);	
		
	}
	
	/**
	 * initializes the coAuthors Graph around authorName
	 * 
	 * only used in constructor
	 * @param authorName
	 * @param initialCoAuthorList
	 */
	private void graphInit(String authorName, List<String> initialCoAuthorList){
		
		String centerNode = authorName; // CHANGE THIS VARIABLE TO CHANGE THE SEARCH/VISUALIZATION RESULTS
	//	List<String> initialCoAuthorList = getTestCoauthors();
		System.out.println(initialCoAuthorList);
		Table nodes = new Table();
		nodes.addColumn("name", String.class);
		
		// table of edges; source and target refer to row index in the table of
		// nodes
		Table edges = new Table();

		edges.addColumn("source", int.class);
		edges.addColumn("target", int.class);
		
		// Add Searched-for author to table of Nodes
		nodes.addRow();
		nodes.setString(0,"name",centerNode); 

	  	// add all co-Authors to table
	  	// for each co-Author, add an edge linking him/her to the first
	  	// entry in the Authors table
		// populate the both the author and edges table
		for(int i = 0; i < initialCoAuthorList.size(); i++){
	  		nodes.addRow();
	  		edges.addRow();
	  		nodes.setString(i+1, "name", initialCoAuthorList.get(i));
	  		edges.setInt(i, "source", 0);
	  		edges.setInt(i, "target", i+1);
	  	}

		this.coAuthors = new Graph(nodes,edges,false);
	}



	public List<String> getTestCoauthors(){
		List<String> co = new ArrayList<String>();
		co.add("Jeff Prouty");
		co.add("Sergey Alekhnovich");
		co.add("Miles Sackler");
		co.add("Bill Cauchois");
		co.add("Kevin Bang");
		return co;
	}
}