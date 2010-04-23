package edu.washington.cs.cse403d.coauthor.uiprototype;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;


public class MyInternalFrame extends JInternalFrame {

    public MyInternalFrame(String frameTitle) {
        super(frameTitle, 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable

        
        setSize(10, 10);
        setLocation(10, 10);
    }
    
    //for testing purpose: puts Jlabel within the internal pane's content pane
    public void addJLabel(JLabel label){
    	rootPane.add(label);
    }
}