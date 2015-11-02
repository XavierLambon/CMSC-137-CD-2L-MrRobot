import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

//for the SQLite
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/*
 * The server as a GUI
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 1L;
	// the stop and start buttons
	private JButton stopStart;

	// JTextArea for the chat room and the events
	private JTextArea chat, event;
	// The port number
	private JTextField tPortNumber;
	// my server
	private Server server;

	//for Managing Accounts
	private JButton accounts;
	private boolean isManage;
	private JPanel cardlayout;
	private CardLayout c1;

	//for the Network Analyzer
	private JButton analyze;
	private boolean isAnalyze;

	private JTextField username, password;
	private JButton createAccount;

	private String path;
	
	
	// server constructor that receive the port to listen to for connection as parameter
	ServerGUI(int port) {
		super("Chat Server");
		server = null;
		// in the NorthPanel the PortNumber the Start and Stop buttons
		JPanel north = new JPanel();
		north.add(new JLabel("Port number: "));
		tPortNumber = new JTextField("  " + port);
		north.add(tPortNumber);
		// to stop or start the server, we start with "Start"
		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);

		// handles the button for managing accounts
		accounts = new JButton("Manage Accounts");
		north.add(accounts);

		accounts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(!isManage){
					accounts.setText("Go Back");
					isManage = true;
					analyze.hide();
					c1.show(cardlayout, "2");
				}else{
					accounts.setText("Manage Accounts");
					isManage = false;
					analyze.show();
					c1.show(cardlayout, "1");
				}
			}
		});

		//handles the network analyzer
		analyze = new JButton("Network Analyzer");
		north.add(analyze);
		analyze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(!isAnalyze){
					analyze.setText("Go Back");
					isAnalyze = true;
					accounts.hide();
					c1.show(cardlayout, "3");
				}else{
					analyze.setText("Network Analyzer");
					isAnalyze = false;
					accounts.show();
					c1.show(cardlayout, "1");
				}
			}
		});


		//initially hides this button, and shows again once the server is successfully started
		accounts.hide();
		analyze.hide();
		
		// enables the panel switch between the manage account and the center panel (which contains the event and chat room)
		cardlayout = new JPanel();
		c1 = new CardLayout();
		cardlayout.setLayout(c1);

		// the event and chat room
		JPanel center = new JPanel(new GridLayout(2,1));
		chat = new JTextArea(80,80);
		chat.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(event));	
		
		//for the manage account interface (to CJ: need your help in improving the layout)
		JPanel accountPanel = new JPanel();
		accountPanel.add(new JLabel("Username: "));
		username = new JTextField(15);
		accountPanel.add(username, BorderLayout.NORTH);
		accountPanel.add(new JLabel("password: "));
		password = new JTextField(15);
		accountPanel.add(password);
		createAccount = new JButton("Create Account");
		accountPanel.add(createAccount);

		createAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				//Handles the creation of an account
				Connection c = null;
				Statement stmt = null;
				try{
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:"+path);	//jdbc:sqlite:absoluteDBpath
					c.setAutoCommit(true);

					stmt = c.createStatement();

					ResultSet rs = stmt.executeQuery("SELECT * FROM ACCOUNT WHERE name = '"+username.getText().trim()+"';");
					String name = "";
					while(rs.next()){
						//getInt, getString, getFloat~
						name = rs.getString("name");

					}
					if(name.equals("")){	//no results found
						stmt.executeUpdate("INSERT INTO ACCOUNT (name, password) VALUES ('"+username.getText().trim()+"', '"+password.getText().trim()+"')");
						JOptionPane.showMessageDialog(null, "Account successfully added!");
					}else{
						JOptionPane.showMessageDialog(null, "Account name already exists!");
					}
					c.close();
					
				}catch(Exception e2){
					System.err.println(e2.getClass().getName() + ": " + e2.getMessage());
					System.exit(0);
				}
			}
		});

		//creates a new panel for network analyzer (i'll handle the layout on this one -Xavier)
		NetworkAnalyzer analyzePanel = new NetworkAnalyzer();

		cardlayout.add(center, "1");
		cardlayout.add(accountPanel, "2");
		cardlayout.add(analyzePanel, "3");

		add(cardlayout);

		c1.show(cardlayout, "1");

		isManage = false;
		isAnalyze = false;

		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(550, 600);
		setVisible(true);
	}		

	// append message to the two JTextArea
	// position at the end
	void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 1);
		
	}
	
	// start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		// if running we have to stop
		if(server != null) {
			server.stop();
			server = null;
			tPortNumber.setEditable(true);
			stopStart.setText("Start");

			accounts.setText("Manage Accounts");
			c1.show(cardlayout, "1");
			isManage = false;
			isAnalyze = false;

			accounts.hide();
			analyze.hide();
			return;
		}
		//checks first if the location of the database file is already set
		File f = new File("databaseLocation.txt");
		JFileChooser openFile;
		if(!f.exists()){
			JOptionPane.showMessageDialog(null, "Database location not defined. Please set location.");
			openFile = new JFileChooser();
            if(openFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            	//writes the location from the text file
            	try{
	            	FileWriter fw = new FileWriter("databaseLocation.txt");
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(openFile.getSelectedFile().getAbsolutePath());
					bw.close();
				}catch(IOException e1){
					e1.printStackTrace();
				}
            }
            
		}
		
		path = "";
		try (BufferedReader br = new BufferedReader(new FileReader("databaseLocation.txt"))){
			path = br.readLine();
			File f1 = new File(path);

			//checks if the contents of the string stored in the text file is valid (does not check if it's a .sqlite file though. will implement in the future)
			if(!f1.exists()){
				JOptionPane.showMessageDialog(null, "Database location is incorrect/corrupted. Please reset location.");
				openFile = new JFileChooser();
		        if(openFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
		        	//rewrites the location from the text file
		        	try{
		            	FileWriter fw = new FileWriter("databaseLocation.txt");
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(openFile.getSelectedFile().getAbsolutePath());
						bw.close();
					}catch(IOException e1){
						e1.printStackTrace();
					}
		        }
			}
			path = path.replace("\\", "\\\\");

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// OK start the server	
		int port;
		try {
			port = Integer.parseInt(tPortNumber.getText().trim());
		}
		catch(Exception er) {
			appendEvent("Invalid port number");
			return;
		}
		// ceate a new Server
		server = new Server(port, this, path);
		// and start it as a thread
		new ServerRunning().start();
		stopStart.setText("Stop");
		accounts.show();
		analyze.show();
		tPortNumber.setEditable(false);
		
      	
	}
	
	// entry point to start the Server
	public static void main(String[] arg) {
		// start server default port 1500
		new ServerGUI(1500);
	}

	/*
	 * If the user click the X button to close the application
	 * I need to close the connection with the server to free the port
	 */
	public void windowClosing(WindowEvent e) {
		// if my Server exist
		if(server != null) {
			try {
				server.stop();			// ask the server to close the conection
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		// dispose the frame
		dispose();
		System.exit(0);
	}
	// I can ignore the other WindowListener method
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

	/*
	 * A thread to run the Server
	 */
	class ServerRunning extends Thread {
		public void run() {

			// should execute until if fails
			server.start();
			// the server failed
			stopStart.setText("Start");
			tPortNumber.setEditable(true);
			appendEvent("Server crashed\n");
			server = null;
		}
	}

}
