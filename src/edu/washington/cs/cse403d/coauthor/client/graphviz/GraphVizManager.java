package edu.washington.cs.cse403d.coauthor.client.graphviz;

import java.awt.Dimension;

public class GraphVizManager {
	private GraphVizFrame theFrame = null;
	public GraphVizManager() {
	}
	public void showGraphFor(String startingAuthor) {
		// XXX: Right now, if the frame is already shown, just hide it. It would
		// 		be better to navigate the current frame to the new startingAuthor
		if(theFrame != null)
			hideGraph();
		theFrame = new GraphVizFrame(startingAuthor);
		theFrame.setMinimumSize(new Dimension(500, 500));
		theFrame.setVisible(true);
	}
	public void hideGraph() {
		theFrame.setVisible(false);
		theFrame.dispose();
		theFrame = null;
	}
}
