package edu.washington.cs.cse403d.coauthor.client.graphviz;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class GraphVizFrame extends JFrame {
	public GraphVizFrame(String startingAuthor) {
		getContentPane().add(new JLabel("Graph for " + startingAuthor));
	}
}