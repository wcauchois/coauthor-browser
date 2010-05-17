package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

import javax.swing.JFrame;

import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;


import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.util.TableIterator;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class PrefuseVisualization {
	
	public static final String HOSTNAME = "attu2.cs.washington.edu";

	

	
	public static void main(String[] args) {
		String s = "Marty Stepp";
		VisualCoAuthorExplorer vx = new VisualCoAuthorExplorer(s);
		// create a new window to hold the visualization
		JFrame frame = new JFrame("prefuse example");
		frame.setBounds(800, 100, 1, 1);
	    // ensure application exits when window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(vx.getDisplay());
		frame.pack();           // layout components in window
		frame.setVisible(true); // show the window

		vx.updateVis();
		
		String cur = "";
		while(true)
		{
			if (!cur.equals(s))
			{
				System.out.println("here");
				frame.remove(vx.getDisplay());
				vx = new VisualCoAuthorExplorer(s);
				// create a new window to hold the visualization
				//frame = new JFrame("prefuse example");
				frame.setBounds(800, 100, 1, 1);
			    // ensure application exits when window is closed
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				frame.add(vx.getDisplay());
				frame.pack();           // layout components in window
				frame.setVisible(true); // show the window
				vx.updateVis();
				cur = s;
			}
			cur = s;
			s = vx.name;
		}
			
    }
	

}
