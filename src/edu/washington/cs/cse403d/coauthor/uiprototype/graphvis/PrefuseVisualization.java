package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

import java.rmi.RemoteException;

import javax.swing.JFrame;

import prefuse.Display;

public class PrefuseVisualization {
	
	public static final String HOSTNAME = "attu2.cs.washington.edu";
	private JFrame frame;
	private Display d;
	
	public PrefuseVisualization()
	{
		String s = "Jessica Miller";
//		String cur = "";
		VisualCoAuthorExplorer vx = new VisualCoAuthorExplorer(s);
	//	frame = new JFrame("prefuse example");
		
	//	frame.setBounds(800, 100, 1, 1);
	//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	//	frame.add(vx.getDisplay());
	//	frame.pack();           // layout components in window
	//	frame.setVisible(true); // show the window
		vx.updateVis();
//		cur = s;
//		cur = s;
		s = vx.curSelected;
		d = vx.getDisplay();
	}
	
	public PrefuseVisualization(String name)
	{
		String s = name;
//		String cur = "";
		VisualCoAuthorExplorer vx = new VisualCoAuthorExplorer(s);
	//	frame = new JFrame("prefuse example");
		
	//	frame.setBounds(800, 100, 1, 1);
	//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	//	frame.add(vx.getDisplay());
	//	frame.pack();           // layout components in window
	//	frame.setVisible(true); // show the window
		vx.updateVis();
//		cur = s;
//		cur = s;
		s = vx.curSelected;
		d = vx.getDisplay();
	}
	
	public PrefuseVisualization(String name1, String name2)
	{
//		String cur = "";
		VisualChainExplorer vx = null;
		try {
			vx = new VisualChainExplorer(name1,name2);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	frame = new JFrame("prefuse example");
		
	//	frame.setBounds(800, 100, 1, 1);
	//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	//	frame.add(vx.getDisplay());
	//	frame.pack();           // layout components in window
	//	frame.setVisible(true); // show the window
		vx.updateVis();
//		cur = s;
//		cur = s;
		d = vx.getDisplay();
	}
	
	public JFrame getFrame()
	{
		return frame;
	}
	
	public Display getD()
	{
		return d;
	}
	/*
	public static void main(String[] args) {
		String s = "Jessica Miller";
		String cur = "";
		VisualCoAuthorExplorer vx = new VisualCoAuthorExplorer(s);
		// create a new window to hold the visualization
		frame = new JFrame("prefuse example");
		
//		while(true)
//		{
//			if (!cur.equals(s))
//			{
			/*	frame.remove(vx.getDisplay());
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
//			}
			cur = s;
			s = vx.curSelected;
//		}
			
    } 
	*/
	

}
