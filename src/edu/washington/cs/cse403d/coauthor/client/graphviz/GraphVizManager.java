package edu.washington.cs.cse403d.coauthor.client.graphviz;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.washington.cs.cse403d.coauthor.uiprototype.graphvis.PrefuseVisualization;

public class GraphVizManager {
	private GraphVizFrame theFrame = null;
	private static final long serialVersionUID = 8486284382808952443L;
	private JTextField field = new JTextField();
	private JButton search = new JButton("Search");
	private PrefuseVisualization pv;
	
	public GraphVizManager() {
	}
	public void showGraphFor(String startingAuthor) {
		// XXX: Right now, if the frame is already shown, just hide it. It would
		// 		be better to navigate the current frame to the new startingAuthor
		if(theFrame != null)
			hideGraph();
		theFrame = new GraphVizFrame(startingAuthor);
		theFrame.setMinimumSize(new Dimension(500, 500));
		
		pv = new PrefuseVisualization(startingAuthor);
		field.setPreferredSize(theFrame.getPreferredSize());
		
		theFrame.add(pv.getD());
		theFrame.pack();
		theFrame.setVisible(true);
	}
	
	
	public void showGraphFor(String startingAuthor, String s) {
		// XXX: Right now, if the frame is already shown, just hide it. It would
		// 		be better to navigate the current frame to the new startingAuthor
		if(theFrame != null)
			hideGraph();
		theFrame = new GraphVizFrame(startingAuthor);
		theFrame.setMinimumSize(new Dimension(500, 500));
		
		pv = new PrefuseVisualization(startingAuthor,s);
		field.setPreferredSize(theFrame.getPreferredSize());
		
		theFrame.add(pv.getD());
		theFrame.pack();
		theFrame.setVisible(true);
	}
	public void hideGraph() {
		theFrame.setVisible(false);
		theFrame.dispose();
		theFrame = null;
	}
}