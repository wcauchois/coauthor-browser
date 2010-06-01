package edu.washington.cs.cse403d.coauthor.client.graphviz;

import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.washington.cs.cse403d.coauthor.uiprototype.graphvis.VisualChainExplorer;
import edu.washington.cs.cse403d.coauthor.uiprototype.graphvis.VisualCoAuthorExplorer;
import edu.washington.cs.cse403d.coauthor.uiprototype.graphvis.VisualExplorer;

public class GraphVizFrame extends JFrame {
	VisualExplorer visExp;
	
	
	public GraphVizFrame(String startingAuthor) {
		getContentPane().add(new JLabel(" Co-Authors Graph for " + startingAuthor));
		this.visExp = new VisualCoAuthorExplorer(startingAuthor);
		
		this.visExp.updateVis();
		this.add(visExp.getDisplay());
		
	}
	
	public GraphVizFrame(String startingAuthor, String endingAuthor) throws RemoteException{
		getContentPane().add(new JLabel("Co-Author chain from " + startingAuthor + "to " + endingAuthor));
		this.visExp = new VisualChainExplorer(startingAuthor,endingAuthor);

		this.visExp.updateVis();
		
		this.add(visExp.getDisplay());
		
		
	}
}