package edu.washington.cs.cse403d.coauthor.client.searchui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.washington.cs.cse403d.coauthor.client.Services;
import edu.washington.cs.cse403d.coauthor.client.utils.HelpMarker;

public class HelpPane extends JPanel {
	private static final long serialVersionUID = -4062401297366198670L;

	public HelpPane() {
		JPanel panel1 = new JPanel();
		panel1.add(new HelpMarker("This would be the additional information."));
		panel1.add(new JLabel("Hover your mouse over this icon for additional information.          "));
		JPanel panel2 = new JPanel();
		panel2.add(new JLabel(Services.getResourceManager().loadImageIcon("HistoryBack.png")));
		panel2.add(new JLabel(Services.getResourceManager().loadImageIcon("HistoryForward.png")));
		panel2.add(new JLabel("Click these icons to go to the previous/next page.                   "));
		JPanel panel4 = new JPanel();
		panel4.add(new JLabel(Services.getResourceManager().loadImageIcon("VisualizeButton.png")));
		panel4.add(new JLabel("Click on this icon to see a visual graph of your search.                 "));
		JPanel panel5 = new JPanel();
		panel5.add(new JLabel(Services.getResourceManager().loadImageIcon("plus.png")));
		panel5.add(new JLabel(Services.getResourceManager().loadImageIcon("minus.png")));
		panel5.add(new JLabel("Click these icons to add/remove an author to your search."));

		add(panel1);
		add(panel2);
		add(panel4);
		add(panel5);
	}
}