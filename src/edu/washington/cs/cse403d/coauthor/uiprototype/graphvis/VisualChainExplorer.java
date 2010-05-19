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
		this.graphInit(authorEnd, authorEnd);
		this.visualizationInit(coAuthors);
		this.displayInit(colorLayoutVis);

	}
		
	protected void graphInit(String authorStart, String authorEnd) throws RemoteException{
	
		this.authorChain= backend.getOneShortestPathBetweenAuthors("Jessica Miller", "Ross Tate", true); // Jessica Miller->Stephen G. Kobourov-> Michael Stepp-> Ross Tate
		System.out.println(authorChain);
		Table nodes = new Table();
		nodes.addColumn("name", String.class);
		
		// table of edges; source and target refer to row index in the table of
		// nodes
		Table edges = new Table();

		edges.addColumn("source", int.class);
		edges.addColumn("target", int.class);
		
		// Add Searched-for author to table of Nodes
		nodes.addRow();
		nodes.setString(0, "name", authorStart);
		for(int i = 1; i < authorChain.size(); i++){
			System.out.println(authorChain.get(i-1) + "->" + authorChain.get(i));
			nodes.addRow();
			edges.addRow();
			nodes.setString(i, "name", authorChain.get(i).getAuthorAName());
	  		edges.setInt(i-1, "source", i-1);
	  		edges.setInt(i-1, "target", i);
		}
		
		for(int i = 0; i < 2; i++){
			System.out.println(nodes.get(i,"name"));
		}	
		this.coAuthors = new Graph(nodes,edges,false);
		
	
	}
}
