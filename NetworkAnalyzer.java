import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class NetworkAnalyzer extends JPanel {
	public NetworkAnalyzer() {
		super(new GridLayout(1, 1));

		JTabbedPane tabbedPane = new JTabbedPane();

		JComponent panel1 = makePanel(1);
		tabbedPane.addTab("Summary", panel1);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = makePanel(2);
		tabbedPane.addTab("Per User", panel2);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent panel3 = makePanel(3);
		tabbedPane.addTab("Settings", panel3);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		/*JComponent panel4 = makeTextPanel("Panel #4 (has a preferred size of 410 x 50).");
		panel4.setPreferredSize(new Dimension(410, 50));
		tabbedPane.addTab("Tab 4", panel4);
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);*/

		//Add the tabbed pane to this panel.
		add(tabbedPane);

		//The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}
    
	protected JComponent makePanel(int type) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel("Xavier~ I will add the designated contents later. Testing: "+type);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.setPreferredSize(new Dimension(410, 50));
		panel.add(filler);
		return panel;
	}

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
   /* private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("NetworkAnalyzer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add content to the window.
        frame.add(new NetworkAnalyzer(), BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		createAndShowGUI();
            }
        });
    }*/
}