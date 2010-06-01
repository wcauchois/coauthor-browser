package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

import java.rmi.RemoteException;
import java.util.List;

import prefuse.data.Graph;
import prefuse.data.Table;
import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;

public class VisualChainExplorer extends VisualExplorer{
	
	protected List<PathLink> authorChain;
	
	
	
	/**
	 * generates a new visualization of the co-authorship chain between the two passed authors
	 * @param authorStart
	 * @param authorEnd
	 * @throws RemoteException
	 */
	public VisualChainExplorer(String authorStart, String authorEnd) throws RemoteException{
		this.databaseInit();
		this.authorChain= backend.getOneShortestPathBetweenAuthors(authorStart, authorEnd, false); // Jessica Miller->Stephen G. Kobourov-> Michael Stepp-> Ross Tate
		this.constructorHelper(authorStart, authorEnd, authorChain);
	}
	
	/**
	 * constructor for cached results
	 * @param authorStart
	 * @param authorEnd
	 * @param authorChain
	 * @throws RemoteException
	 */
	public VisualChainExplorer(String authorStart, String authorEnd, List<PathLink> authorChain) throws RemoteException{
		this.databaseInit();
		this.constructorHelper(authorStart, authorEnd, authorChain);
	}
	
	/**
	 * @param authorStart
	 * @param authorEnd
	 * @param authorChain
	 * @throws RemoteException
	 */
	public void constructorHelper(String authorStart, String authorEnd, List<PathLink> authorChain) throws RemoteException{
		this.authorChain = authorChain;
		this.graphInit(authorStart, authorEnd);
		this.visualizationInit();
		this.displayInit();

	}
	
	/**
	 * initializes the chain graph betwen the two passed co-authors
	 * @param authorStart
	 * @param authorEnd
	 * @throws RemoteException
	 */
	protected void graphInit(String authorStart, String authorEnd) throws RemoteException{
		System.out.println(authorStart + " " + authorEnd);
		//System.out.println(authorChain);
		Table nodes = new Table();
		nodes.addColumn("name", String.class);
		nodes.addColumn("visited", int.class, 0);
		
		// table of edges; source and target refer to row index in the table of
		// nodes
		Table edges = new Table(); // Jessica Miller->Stephen G. Kobourov-> Michael Stepp-> Ross Tate

		edges.addColumn("source", int.class);
		edges.addColumn("target", int.class);
		
		// Add Searched-for author to table of Nodes
		nodes.addRow();
		nodes.setString(0, "name", authorStart);
		nodes.setInt(0, "visited", 1);

		int k;
		for(k = 1; k < authorChain.size(); k++){
			System.out.println(authorChain.get(k-1).getAuthorBName() + "->" + authorChain.get(k).getAuthorBName());
			nodes.addRow();
			edges.addRow();
			nodes.setString(k, "name", authorChain.get(k-1).getAuthorBName());
			nodes.setInt(k, "visited", 0);
			edges.setInt(k-1, "source", k-1);
	  		edges.setInt(k-1, "target", k);
		}
		
		// end of result fencepost
		nodes.addRow();
		edges.addRow();
		nodes.setString(k, "name", authorChain.get(k-1).getAuthorBName());
		nodes.setInt(k, "visited", 1);
		edges.setInt(k-1, "source", k-1);
  		edges.setInt(k-1, "target", k);

  		
		this.coAuthors = new Graph(nodes,edges,false);
		
	
	}
}
