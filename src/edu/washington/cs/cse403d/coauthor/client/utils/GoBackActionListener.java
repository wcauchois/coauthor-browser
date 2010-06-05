package edu.washington.cs.cse403d.coauthor.client.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.washington.cs.cse403d.coauthor.client.Services;

/**
 * Invokes goBack() on the current Browser instance when an action is performed.
 * @author William Cauchois
 */
public class GoBackActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent evt) {
		Services.getBrowser().goBack();
	}

}
