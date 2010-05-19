package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import prefuse.data.Graph;
import prefuse.data.Table;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

public class VisualChainExplorer extends VisualExplorer{
	
	protected List<PathLink> authorChain;
	
	public VisualChainExplorer(String authorStart, String authorEnd) throws RemoteException{
		this.databaseInit();
		this.graphInit(authorStart, authorEnd);
		this.visualizationInit(coAuthors);
		this.displayInit(colorLayoutVis);

	}
		
	protected void graphInit(String authorStart, String authorEnd) throws RemoteException{
		System.out.println(authorStart + " " + authorEnd);
		this.authorChain= backend.getOneShortestPathBetweenAuthors(authorStart, authorEnd, false); // Jessica Miller->Stephen G. Kobourov-> Michael Stepp-> Ross Tate
		//System.out.println(authorChain);
		Table nodes = new Table();
		nodes.addColumn("name", String.class);
		
		// table of edges; source and target refer to row index in the table of
		// nodes
		Table edges = new Table(); // Jessica Miller->Stephen G. Kobourov-> Michael Stepp-> Ross Tate

		edges.addColumn("source", int.class);
		edges.addColumn("target", int.class);
		
		// Add Searched-for author to table of Nodes
		nodes.addRow();
		nodes.setString(0, "name", authorStart);
		int k;
		for(k = 1; k < authorChain.size(); k++){
			System.out.println(authorChain.get(k-1).getAuthorBName() + "->" + authorChain.get(k).getAuthorBName());
			nodes.addRow();
			edges.addRow();
			nodes.setString(k, "name", authorChain.get(k-1).getAuthorBName());
	  		edges.setInt(k-1, "source", k-1);
	  		edges.setInt(k-1, "target", k);
		}
		
		// end of result fencepost
		nodes.addRow();
		edges.addRow();
		nodes.setString(k, "name", authorChain.get(k-1).getAuthorBName());
  		edges.setInt(k-1, "source", k-1);
  		edges.setInt(k-1, "target", k);

	
		this.coAuthors = new Graph(nodes,edges,false);
		
	
	}
}
