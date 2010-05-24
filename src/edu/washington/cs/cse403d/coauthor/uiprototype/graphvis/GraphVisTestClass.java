package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;

import java.rmi.RemoteException;

import javax.swing.JFrame;

public class GraphVisTestClass {
	
	public static final String HOSTNAME = "attu2.cs.washington.edu";
	public static void main(String[] args) throws RemoteException{
		
/*		CoauthorDataServiceInterface backend;
		try {
			backend = (CoauthorDataServiceInterface) Naming.lookup("rmi://" + HOSTNAME + "/"
					+ CoauthorDataServiceInterface.SERVICE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		List<PathLink> chain= backend.getOneShortestPathBetweenAuthors("Jessica Miller", "Ross Tate", true); // Jessica Miller->Stephen G. Kobourov-> Michael Stepp-> Ross Tate
		System.out.println(chain);*/
//		String s = "Jessica Miller";
//		VisualCoAuthorExplorer vx = new VisualCoAuthorExplorer(s);
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

	}
}
