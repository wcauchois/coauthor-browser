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

		vx.updateVis();
//		cur = s;
//		cur = s;
		d = vx.getDisplay();
	}
	
	public PrefuseVisualization(String name)
	{
		String s = name;
		VisualCoAuthorExplorer vx = new VisualCoAuthorExplorer(s);

		vx.updateVis();
		d = vx.getDisplay();
	}
	
	public PrefuseVisualization(String name1, String name2)
	{
		VisualChainExplorer vx = null;
		try {
			vx = new VisualChainExplorer(name1,name2);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		vx.updateVis();

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
	

}
