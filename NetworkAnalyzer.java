import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.Timer;

public class NetworkAnalyzer extends JPanel implements ActionListener{
	Timer timer;
	JComponent panel1;
	JTabbedPane tabbedPane;
	ArrayList<Integer> points, points2;
	DrawGraph dgraph, dgraph2, dgraph3;

	public NetworkAnalyzer() {
		super(new GridLayout(1, 1));

		tabbedPane = new JTabbedPane();


		panel1 = makePanel(1);
		tabbedPane.addTab("Summary", panel1);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		points = getRandomPoints(30, 20);
		points2 = getRandomPoints(30, 20);
		dgraph = new DrawGraph(points, points2, Color.blue, Color.red);
		panel1.add(dgraph); 

		//points = getRandomPoints(30, 20);
		dgraph2 = new DrawGraph(points, Color.blue);
		panel1.add(dgraph2); 


		//points = getRandomPoints(30, 20);
		dgraph3 = new DrawGraph(points2, Color.red);
		panel1.add(dgraph3); 


		timer = new Timer(2000, this);
		timer.setInitialDelay(1000);
		timer.start(); 



		
		JComponent panel2 = makePanel(2);
		tabbedPane.addTab("Per User", panel2);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);


		String[] columnNames = {"No.",
                        "Time",
                        "Source",
                        "Destination",
                        "Protocol", 
                        "Info"};

        Object[][] data = {
			{"Kathy", "Smith",
			"Snowboarding", new Integer(5), new Boolean(false), "some info"},
			{"John", "Doe",
			"Rowing", new Integer(3), new Boolean(true), "some info"},
			{"Sue", "Black",
			"Knitting", new Integer(2), new Boolean(false), "some info"},
			{"Jane", "White",
			"Speed reading", new Integer(20), new Boolean(true), "some info"},
			{"Joe", "Brown",
			"Pool", new Integer(10), new Boolean(false), "some info"}
			};

		JTable table = new JTable(data, columnNames);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		panel2.add(scrollPane);




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
		//JLabel filler = new JLabel("Xavier~ I will add the designated contents later. Testing: "+type);
		//filler.setHorizontalAlignment(JLabel.CENTER);
		if(type == 1)
			panel.setLayout(new GridLayout(3, 1));
		else
			panel.setLayout(new GridLayout(1, 1));
		panel.setPreferredSize(new Dimension(410, 50));
		//panel.add(filler);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
	    //If still loading, can't animate.
	    
	    if(e.getSource() == timer){

		    Random random = new Random();
		    points.remove(0);
		    points.add(random.nextInt(20));
		    points2.remove(0);
		    points2.add(random.nextInt(20));

	    	panel1.remove(dgraph);
	    	panel1.remove(dgraph2);
	    	panel1.remove(dgraph3);



			dgraph = new DrawGraph(points, points2, Color.blue, Color.red);
			dgraph2 = new DrawGraph(points, Color.blue);
			dgraph3 = new DrawGraph(points2, Color.red);
			panel1.add(dgraph);
			panel1.add(dgraph2);
			panel1.add(dgraph3);
			panel1.revalidate();
			panel1.repaint();

	    }
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


    public ArrayList<Integer> getRandomPoints(int number, int max){
    	ArrayList<Integer> points = new ArrayList<Integer>();
	    Random random = new Random();
	    for (int i = 0; i < number ; i++) {
	       points.add(random.nextInt(max));
	    }
	    return points;
    }
}