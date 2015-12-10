import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

//for the SQLite
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


import javax.swing.Timer;
import java.awt.event.*;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server implements ActionListener{
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port, udpPort;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;

	//for the database path (dynamic)
	private static String path;
	
	private static DatagramSocket serverUDP;

	private static javax.swing.Timer lobbyTimer;
	private static int cdTime;

	//for the lobby
	private static ArrayList<Players> lobby;
	private InetAddress serverIP;
	

	//for the SQLite
	private static Connection c = null;
	private static Statement stmt = null;
	private String sql = null;

	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port) {
		this(port, null);
	}
	
	public Server(int port, ServerGUI sg) {
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
		lobby = new ArrayList<Players>();
		lobbyTimer = new javax.swing.Timer(1000, this);
	}

	public Server(int port, int udpPort, ServerGUI sg, String path) {
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		this.udpPort = udpPort;
		//the path for the database
		this.path = path;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
		lobby = new ArrayList<Players>();
		lobbyTimer = new javax.swing.Timer(1000, this);
	}
	
	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);
			// the socket used for the UDP connection
			serverUDP = new DatagramSocket(udpPort);
			serverIP = InetAddress.getLocalHost();
			System.out.println("server port: "+serverIP.getHostAddress());
			UDPThread udp = new UDPThread();	//here~
			udp.start();
			

			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				al.add(t);									// save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				serverUDP.close(); 	//closes the UDP connection
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
    /*
     * For the GUI to stop the server
     */
	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}
	/*
	 *  to broadcast a message to all Clients
	 */
	private synchronized void broadcast(String message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		// display message on console or GUI
		if(sg == null)
			System.out.print(messageLf);
		else
			sg.appendRoom(messageLf);     // append in the room window
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(messageLf)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}

	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	/*
	 *  To run as a console application just open a console window and: 
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
		
	}
	
	static class Players{
		InetAddress ipAdd;
		int port;
		String name;

		Players(String name, DatagramPacket packet){
			ipAdd = packet.getAddress();
			port = packet.getPort();
			this.name = name;
		}

		public String getPlayerIP(){
			return ipAdd.getHostAddress();
		}

		public String getPlayerName(){
			return name;
		}

		public int getPlayerPort(){
			return port;
		}
	}

	//A thread for the UDP. CURRENTLY STUCK HERE...
	public static class UDPThread extends Thread{
		
		
		UDPThread(){
			//yay~
		}
		
		public void run() {
			while(true){
				try{
					//AGAIN, CURRENTLY HERE. FREE THE RECEIVE AND SENDDATA
					byte[] receiveData = new byte[1024];
					byte[] sendData = new byte[1024];
					DatagramPacket sendPacket, receivePacket;
					Arrays.fill(receiveData, (byte)0);
					receivePacket = new DatagramPacket(receiveData, receiveData.length);
					serverUDP.receive(receivePacket);

					String msgFromClient = new String(receivePacket.getData()).trim();	//i may need to trim the excess null chars at the end
					String udpString[];
					udpString = msgFromClient.split("~");

					//handles the UDP request from the client
					String uname = udpString[1];
					if(udpString[0].equals("mapdata")){
						String parameter;
						if(udpString[2].equals("none")){
							parameter = "";
						}else{
							parameter = udpString[2];
						}
						try{
							Class.forName("org.sqlite.JDBC");
							c = DriverManager.getConnection("jdbc:sqlite:"+path);	//jdbc:sqlite:absoluteDBpath
							c.setAutoCommit(true);

							stmt = c.createStatement();
							String statement = "UPDATE ACCOUNT SET base_config = '"+parameter+"' WHERE name = '"+uname+"'";
							stmt.executeUpdate(statement);

						}catch(Exception e){
							System.err.println(e.getClass().getName() + ": " + e.getMessage());
							System.exit(0);
						}
					}else if(udpString[0].equals("getBase")){	//handles the fetching of the user's base config to the client
						
						try{
							Class.forName("org.sqlite.JDBC");
							c = DriverManager.getConnection("jdbc:sqlite:"+path);	//jdbc:sqlite:absoluteDBpath
							c.setAutoCommit(true);

							stmt = c.createStatement();
							ResultSet rs = stmt.executeQuery("SELECT * FROM ACCOUNT WHERE name = '"+uname+"';");

							String baseConfig = "";
							while(rs.next()){
								baseConfig = rs.getString("base_config");
							}
							//send here
							Arrays.fill(sendData, (byte)0);
							sendData = baseConfig.getBytes();
							InetAddress IPAddress = receivePacket.getAddress();	//for sending the 
							int port2 = receivePacket.getPort();
							sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port2);
							try{
								serverUDP.send(sendPacket);
							}catch(Exception ec) {
								System.out.println("Error creating a UDP packet:" + ec);
							}
							
						}catch(Exception e){
							System.err.println(e.getClass().getName() + ": " + e.getMessage());
							System.exit(0);
						}
					}else if(udpString[0].equals("joinlobby")){
						//handles the case when a player joins a lobby
						Players newPlayer = new Players(udpString[1], receivePacket);
						lobby.add(newPlayer);
						if(lobby.size() == 1){
							cdTime = 10;
							lobbyTimer.start();
						}


						System.out.println();
						System.out.println("List of current players in the lobby:");
						for(Players x : lobby){
							System.out.println("name: "+x.getPlayerName());
							System.out.println("ip: "+x.getPlayerIP());
							System.out.println("port: "+x.getPlayerPort());
							System.out.println("===================");
						}
					}else if(udpString[0].equals("leave")){
						//if a user decides to stop his/her wait in the lobby
						Iterator<Players> it = lobby.iterator();
						while (it.hasNext()) {
							Players user = it.next();
							if(user.getPlayerName().equals(udpString[1])){
								it.remove();
							}
						}
						if(lobby.size() == 0){
							lobbyTimer.stop();

						}
					}
					
					//System.out.println("Client IP: "+receivePacket.getAddress());
					//System.out.println("Client Port: "+receivePacket.getPort());
					//System.out.println("Client socketAddress: "+receivePacket.getSocketAddress());
				}catch(IOException e){
					System.out.println("error: "+e);
				}
			}
		}
		
		
	}

	public void actionPerformed(ActionEvent e){
		if(e.getSource() == lobbyTimer){
			cdTime--;
			System.out.println(cdTime);
			if(cdTime == 0){
				lobbyTimer.stop();

				if(lobby.size() > 1){
					System.out.println("Do matching here");

					int size = lobby.size();

					int limit = (size%2 == 1)? (int)Math.floor((size+1)/2): (int)Math.floor(size/2);

					ArrayList<Players> g1 = new ArrayList<Players>();
					ArrayList<Players> g2 = new ArrayList<Players>();

					for(int i=0; i<lobby.size(); i++){
						if(i < limit){
							g1.add(lobby.get(i));
						}
						else{
							g2.add(lobby.get(i));
						}
					}


					String g1Names = "";
					for(Players p : g1){
						g1Names += p.getPlayerName()+",";
					}
					String g2Names = "";
					for(Players p : g2){
						g2Names += p.getPlayerName()+",";
					}

					for(Players p : lobby){
						byte[] sendData = new byte[1024];
						if(g1.contains(p)){
							sendData = g2Names.getBytes();
						}
						else{
							sendData = g1Names.getBytes();
						}

						try{
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(p.getPlayerIP()), p.getPlayerPort());
							serverUDP.send(sendPacket);
						}catch(Exception ec) {
							System.out.println("Error creating a UDP packet:" + ec);
						}
					}
				}
				else{
					//Arrays.fill(sendData, (byte)0);
					try{
						byte[] sendData = "false".getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(lobby.get(0).getPlayerIP()), lobby.get(0).getPlayerPort());
						serverUDP.send(sendPacket);
					}catch(Exception ec) {
						System.out.println("Error creating a UDP packet:" + ec);
					}

					lobby.remove(0);
				}
			}
		}
	}


	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username, password;
		// the only type of message a will receive
		ChatMessage cm;
		// the date I connect
		String date;

		// the client ip address
		String ipAdress;

		// Constructore
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			/* Creating both Data Stream */
			//CURRENTLY HERE
			ipAdress = socket.getRemoteSocketAddress().toString();
			System.out.println("ClientIP: "+ipAdress);
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username


				//HERE~, THE SERVER CHECKS IF THE CLIENT CREDENTIALS ARE CORRECT
				//username = (String) sInput.readObject();
				Object clientInput[] = (Object[])sInput.readObject();
				username = (String)clientInput[0];
				password = (String)clientInput[1];

				//FOR VALIDATION
				try{
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:"+path);	//jdbc:sqlite:absoluteDBpath
					display("Database connection successful.");
					c.setAutoCommit(true);

					stmt = c.createStatement();

					ResultSet rs = stmt.executeQuery("SELECT * FROM ACCOUNT WHERE name = '"+username+"';");

					String name = "";
					String password = "";
					while(rs.next()){
						//getInt, getString, getFloat~
						name = rs.getString("name");
						password = rs.getString("password");

					}
					if(name.equals("")){	//no results found
						display("Username does not exist.");
						Object input[] = new Object[2];
						input[0] = 1;
						input[1] = new String("");
						sOutput.writeObject(input);
						disconnect();
						c.close();
					}else{
						if(name.equals(username) && password.equals(this.password)){	
							//do some stuffs here
							System.out.println("Logged in successful. Welcome "+name+"!");
							display("Logged in successful. Welcome "+name+"!");
							Object input[] = new Object[2];
							input[0] = 3;
							input[1] = new String("");
							sOutput.writeObject(input);
							
						}else{
							display("Invalid password.");
							Object input[] = new Object[2];
							input[0] = 2;
							input[1] = new String("");
							sOutput.writeObject(input);
							disconnect();
							c.close();
						}
					}
					
				}catch(Exception e){
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
					System.exit(0);
				}
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}

		public void disconnect(){
			try { 
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {} // not much else I can do
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {} // not much else I can do
	        try{
				if(socket != null) socket.close();
			}
			catch(Exception e) {} // not much else I can do
		}

		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the messaage part of the ChatMessage
				String message = cm.getMessage();

				// Switch on the type of message receive
				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
					// scan al the users connected
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				Object input[] = new Object[2];
				input[0] = 3;
				input[1] = msg;
				sOutput.writeObject(input);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}

