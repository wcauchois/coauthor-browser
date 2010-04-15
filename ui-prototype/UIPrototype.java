// i know these imports are bad style :(
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UIPrototype implements ActionListener {
    private JFrame mainFrame;
    public UIPrototype(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    public void actionPerformed(ActionEvent evt) {
        if(evt.getActionCommand().equals("Quit")) {
            // XXX: is this the right way to exit the app?
            mainFrame.dispose();
        }
    }
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu, submenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        menuItem = new JMenuItem("Quit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(menu);

        ButtonGroup group = new ButtonGroup();
        rbMenuItem = new JRadioButtonMenuItem("Authors");
        rbMenuItem.setSelected(true);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Articles");
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Graph");
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        return menuBar;
    }
    public Container createContentPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        final JPanel left = new JPanel(),
                     right = new JPanel();
        split.setLeftComponent(left);
        split.setRightComponent(right);
    
        left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
        final JTextField filterBar = new JTextField("Filter");
        String[] authors = {"Notkin, David",
                            "Murphey, Gail C",
                            "Aldrich, Jonathan",
                            "Chambers, Craig",
                            "VanHilst, Michael"};
        final JList authorsList = new JList(authors);
        filterBar.setForeground(Color.LIGHT_GRAY);
        filterBar.setEditable(false);
        left.add(filterBar);
        left.add(authorsList);
        left.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent evt) {
                filterBar.setMaximumSize(new Dimension(
                    left.getWidth(), filterBar.getPreferredSize().height));
                authorsList.setMaximumSize(left.getSize());
            }
            public void componentHidden(ComponentEvent evt) { }
            public void componentShown(ComponentEvent evt) { }
            public void componentMoved(ComponentEvent evt) { }
        });
        // this is just to fire the componentResized event
        left.setSize(left.getPreferredSize());

        pane.add(split);
        return pane;
    }
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("UI Prototype");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UIPrototype proto = new UIPrototype(frame);
        frame.setJMenuBar(proto.createMenuBar());
        frame.setContentPane(proto.createContentPane());

        frame.setVisible(true);
    }
    public static void main(String[] args) {
        try {
            // i like my system look and feel!
            UIManager.setLookAndFeel(
              UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            // not really the end of the world...
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() { createAndShowGUI(); }
        });
    }
}
