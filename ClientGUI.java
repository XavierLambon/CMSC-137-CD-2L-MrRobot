import javax.swing.*;
import javax.imageio.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;


/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {
	
	int mapSize = 40;

	Tile[][] tiles;
	ArrayList<Building> bList;
	ArrayList<JRadioButton> bb;

	private JLabel chatStatus;
	private JTextField chatField;
	private JTextArea chatArea;

	MainPanel mainPanels, mainLogin, mainMenu, mainCM, mainTM;
	SidePanel sidePanels, sideLogin, sideMenu, sideCM, sideTM;

	private JTextField usernameField;
	private JTextField passwordField;

	CButton cmButton, tmButton;
	CButton cmBack;
	CButton tmBack;

	Map mapTM;

	private static final long serialVersionUID = 1L;
	private JTextField tfServer, tfPort;
	private CButton login, logout, whoIsIn;
	private boolean connected;
	private Client client;
	private int defaultPort, defaultUDPPort;
	private String defaultHost;


	private String unameUDP;

	// Constructor connection receiving a socket number
	public ClientGUI(String host, int port, int udpPort) {

		super("Clash of Clans");
		defaultPort = port;
		defaultUDPPort = udpPort;
		defaultHost = host;

		// the server name and the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		// CHAT COMPONENTS
		chatStatus = new JLabel("Please login first", SwingConstants.LEFT);
		chatField = new JTextField(18);
		chatField.setBackground(Color.WHITE);

		JPanel chatControls = new JPanel();
		chatControls.add(chatStatus);
		chatControls.add(chatField);
		chatControls.setBounds(0,0,200,50);

		chatArea = new JTextArea("Welcome to the Chat room\n", 80, 80);
		chatArea.setEditable(false);
		JScrollPane jsp = new JScrollPane(chatArea);
		jsp.setBounds(0,50,200,550);
		
		JPanel chatPanel = new JPanel(null);
		chatPanel.setSize(1000,600);
		chatPanel.add(chatControls);
		chatPanel.add(jsp);


		//LOGIN COMPONENTS
		mainLogin = new MainPanel();
		mainLogin.add(new JLabel("Main Login"));

		usernameField = new JTextField("user", 15);
		passwordField = new JTextField("password", 15);
		login = new CButton("Login");
		login.addActionListener(this);


		sideLogin = new SidePanel();
		sideLogin.add(usernameField);
		sideLogin.add(passwordField);
		sideLogin.add(login);
		

		
		//MAIN MENU COMPONENTS 
		mainMenu = new MainPanel();
		mainMenu.add(new JLabel("Main Menu"));

		sideMenu = new SidePanel();
		cmButton = new CButton("Customize Map");
		tmButton = new CButton("Troop Movement");
		logout = new CButton("Logout");
		cmButton.addActionListener(this);
		tmButton.addActionListener(this);
		logout.addActionListener(this);
		sideMenu.add(cmButton);
		sideMenu.add(tmButton);
		sideMenu.add(logout);


		//CM COMPONENTS
		mainCM = new MainPanel(new GridLayout(mapSize,mapSize));
		tiles = new Tile[mapSize][mapSize];
		int tileSize = mainCM.getWidth()/mapSize;
		for(int i=0; i<mapSize; i++){
			tiles[i] = new Tile[mapSize];
			for(int j=0; j<mapSize; j++){
				tiles[i][j] = new Tile(i, j);
				tiles[i][j].setPreferredSize(new Dimension(tileSize, tileSize));
				tiles[i][j].setSize(tileSize, tileSize);
				tiles[i][j].addActionListener(this);
	
				if( (i+j)%2 == 0 ) 
					tiles[i][j].setBackground(new Color(102, 255, 51));
				else
					tiles[i][j].setBackground(new Color(51, 204, 51));
				
				mainCM.add(tiles[i][j]);
			}
		}


	
		sideCM = new SidePanel(new FlowLayout(FlowLayout.LEFT));
		cmBack = new CButton("Main Menu");
		cmBack.setSize(150,30);
		cmBack.setPreferredSize(new Dimension(150,30));
		cmBack.addActionListener(this);
		sideCM.add(cmBack);


		//TM COMPONENTS
		mainTM = new MainPanel(null);
		mapTM = new Map(600);
		mapTM.setPreferredSize(new Dimension(600,600));
		mapTM.setSize(600,600);
		mapTM.setBounds(0,0,600,600);
		mainTM.add(mapTM);
		

		sideTM = new SidePanel(new FlowLayout(FlowLayout.LEFT));
		tmBack = new CButton("Main Menu");
		tmBack.setSize(150,30);
		tmBack.setPreferredSize(new Dimension(150,30));
		tmBack.addActionListener(this);
		sideTM.add(tmBack);
		

		createBuildings();
		bb = new ArrayList<JRadioButton>();

		ButtonGroup group = new ButtonGroup();

		JRadioButton removeButton = new JRadioButton("Remove Building");
		bb.add(removeButton);
		sideCM.add(removeButton);
		group.add(removeButton);

		for(int i=0; i<bList.size(); i++){
			JRadioButton button = new JRadioButton(bList.get(i).getName()+'-'+bList.get(i).getQuantity());
			bb.add(button);
			sideCM.add(button);
			group.add(button);
		}

		
		mainPanels = new MainPanel(new CardLayout());
		mainPanels.add(mainLogin, "Login");
		mainPanels.add(mainMenu, "Menu");
		mainPanels.add(mainCM, "CM");
		mainPanels.add(mainTM, "TM");

		sidePanels = new SidePanel(new CardLayout());
		sidePanels.add(sideLogin, "Login");
		sidePanels.add(sideMenu, "Menu");
		sidePanels.add(sideCM, "CM");
		sidePanels.add(sideTM, "TM");

		JPanel mainPanel = new JPanel(null);
		mainPanel.setSize(1000,600); 
		mainPanel.add(sidePanels);
		mainPanel.add(mainPanels);
		mainPanel.add(chatPanel);

			
		add(mainPanel, BorderLayout.CENTER);

	
		try {
		    setIconImage(ImageIO.read(new File("images/logo.png")));
		}
		catch (IOException exc) {
		    exc.printStackTrace();
		}

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 600);
		setVisible(true);
		setResizable(false);
		chatField.requestFocus();
	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		chatArea.append(str);
		chatArea.setCaretPosition(chatArea.getText().length() - 1);	
	}
	

	public void createBuildings(){
		bList = new ArrayList<Building>();

		//resource
		bList.add(new Building("Gold Mine", 3, 3, 960, 7));
		bList.add(new Building("Elixir Collector", 3, 3, 960, 7));
		bList.add(new Building("Dark Elixir Drill", 3, 3, 1160, 3));
		bList.add(new Building("Gold Storage", 3, 3, 2100, 4));
		bList.add(new Building("Elixir Storage", 3, 3, 2100, 4));
		bList.add(new Building("Dark Elixir Storage", 3, 3, 3200, 1));
		bList.add(new Building("Builder Hut", 2, 2, 250, 5));

		//army
		bList.add(new Building("Army Camp", 5, 5, 500, 4));
		bList.add(new Building("Barracks", 3, 3, 860, 4));
		bList.add(new Building("Dark Barracks", 3, 3, 900, 2));
		bList.add(new Building("Laboratory", 4, 4, 950, 1));
		bList.add(new Building("Spell Factory", 3, 3, 615, 1));
		bList.add(new Building("Barbarian King Altar", 3, 3, 250, 1));
		bList.add(new Building("Dark Spell Factory", 3, 3, 750, 1));
		bList.add(new Building("Archer Queen Altar", 3, 3, 250, 1));

		//other
		bList.add(new Building("Town Hall", 4, 4, 5500, 1));
		bList.add(new Building("Clan Castle", 3, 3, 3400, 1));

		//defense
		bList.add(new Building("Archer Tower", 3, 3, 1050, 7));
		bList.add(new Building("Cannon", 3, 3, 1260, 6));
		//bList.add(new Building("Air Sweeper", 2, 2, 1000, 2));
		//bList.add(new Building("Cannon", 3, 3, 1260, 6));
		//bList.add(new Building("Cannon", 3, 3, 1260, 6));
	}
		
	/*
	* Button or JTextField clicked
	*/
	public void switchCards(String s){
		System.out.println("Switching to "+s);
		CardLayout cl = (CardLayout)(mainPanels.getLayout());
    	cl.show(mainPanels, s);
    	mainPanels.revalidate();
    	CardLayout cl2 = (CardLayout)(sidePanels.getLayout());
    	cl2.show(sidePanels, s);
    	sidePanels.revalidate();
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();


		if(o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			chatArea.setText("");

			switchCards("Login");
			return;
		}
		

		if(o == cmButton){
			String baseConfig = getBaseConfig();
			System.out.println("base config: "+baseConfig);
			switchCards("CM");

			return;
		}

		if(o == tmButton){
			ArrayList<Building> bArr = new ArrayList<Building>();
			for(int i=0; i<40; i++){
				for(int j=0; j<40; j++){
					if(tiles[i][j].getValue().equals("") || tiles[i][j].getValue().contains("-")){
						continue;
					}
					//weird part here
					bArr.add(new Building(tiles[i][j].getValue(), new Coordinate(j, i)));
				}
			}
			mapTM.setBuildings(bArr);

			switchCards("TM");
			return;
		}

		// if it the who is in button
		if(o == whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			return;
		}

		if(o == cmBack){
			ArrayList<Building> bArr = new ArrayList<Building>();
			for(int i=0; i<40; i++){
				for(int j=0; j<40; j++){
					if(tiles[i][j].getValue().equals("") || tiles[i][j].getValue().contains("-")){
						continue;
					}
					//weird part here
					bArr.add(new Building(tiles[i][j].getValue(), new Coordinate(j, i)));
				}
			}


			String temp = "mapdata~"+unameUDP+"~";
			int tileCount = 40;
			int dim = 600;
			int tileDim = (int)(dim/tileCount);
			int counter = 0;
			for(Building b : bArr){
				int x, y, hp;
				x = b.getPos().getX()/tileDim;
				y = b.getPos().getY()/tileDim;
				hp = b.getHp();
				temp += b.getName()+"-"+x+","+y+"-"+hp+"|";
				counter += 1;
			}
			if(counter > 0){
				temp = temp.substring(0, temp.length()-1);	//removes the last '|'
			}else{
				temp += "none";
			}
			
			client.sendUDP(temp);	//allows saving of the current state of the map into the user's account

			switchCards("Menu");


			return;
		}
		if(o == tmBack){
			switchCards("Menu");
			
			return;
		}

		for(int i=0; i<40; i++){
			for(int j=0; j<40; j++){
				if(o == tiles[i][j]){
					for(int k=0; k<bb.size(); k++){
						if(bb.get(k).isSelected()){
							if(bb.get(k).getText().equals("Remove Building")){
								removeBuilding(i, j);
								return;
							}

							insertBuilding(i, j, k);
							//JOptionPane.showMessageDialog(null, bb.get(k).getText());
							return;
						}
					}


					//JOptionPane.showMessageDialog(null, "i-"+i+" j-"+j);
					return;
				}
			}
		}


		// ok it is coming from the JTextField
		if(connected) {
			// just have to send the message
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, chatField.getText()));				
			chatField.setText("");
			return;
		}
		

		if(o == login) {
			// ok it is a connection request
			String username = usernameField.getText().trim();
			String password = passwordField.getText().trim();
			// empty username ignore it
			if(username.length() == 0)
				return;
			// empty serverAddress ignore it
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;
			// empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
			client = new Client(server, port, username, password, this);
			// test if we can start the Client
			if(!client.start()) 
				return;

			unameUDP = username;

			switchCards("Menu");
			//fetching of the base_config string from the database

			chatField.setText("");
			chatArea.setText("");

		}


	
	}

	public String getBaseConfig(){
		client.sendUDP("getBase~"+unameUDP);
		return client.receiveUDP();
	}

	public boolean getConnectStatus(){
		return connected;
	}

	

	public void removeBuilding(int i, int j){

		if(tiles[i][j].getValue().equals("")){
			JOptionPane.showMessageDialog(null, "There is no building here");
			return;
		}

		if(tiles[i][j].value.contains("-")){
			String tText = tiles[i][j].value;
			i = Integer.parseInt(tText.split("-")[0]);
			j = Integer.parseInt(tText.split("-")[1]);
		}

		int index;
		for(index=0; index<bb.size(); index++){
			if(bb.get(index).getText().split("-")[0].equals(tiles[i][j].value)){
				break;
			}

		}

		String temp = bb.get(index).getText();
		String[] parts = temp.split("-");
		int newQ = Integer.parseInt(parts[1])+1;

		Building tB = bList.get(0);
		for(int b=0; b<bList.size(); b++){
			if( bList.get(b).getName().equals(tiles[i][j].value) ){
				tB = bList.get(b);
				break;
			}
		}

		for(int c=0; c<tB.getQWidth(); c++){
			for(int r=0; r<tB.getQHeight(); r++){

				if( (i+c+j+r)%2 == 0 ) 
					tiles[i+c][j+r].setBackground(new Color(102, 255, 51));
				else
					tiles[i+c][j+r].setBackground(new Color(51, 204, 51));

				tiles[i+c][j+r].setIcon(null);

				tiles[i+c][j+r].value = ""; 
				bb.get(index).setText(parts[0] + "-" + newQ );
			
			}	
		}
	}

	public static BufferedImage resize(BufferedImage image, int width, int height) {
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, width, height, null);
	    g2d.dispose();
	    return bi;
	}

	public void insertBuilding(int i, int j, int index){

		if(!tiles[i][j].getValue().equals("")){
			JOptionPane.showMessageDialog(null, "There is already a building here");
			return;
		}

		String temp = bb.get(index).getText();
		String[] parts = temp.split("-");

		int newQ = Integer.parseInt(parts[1])-1;
		if(newQ < 0)
			return;

		Building tB = bList.get(0);
		for(int b=0; b<bList.size(); b++){
			if( bList.get(b).getName().equals(parts[0]) ){
				tB = bList.get(b);
				//System.out.println("here");
				break;
			}
		}

		Image image = null;
		BufferedImage buffered;
		try{
			image = ImageIO.read(new File("images/"+tB.getName().replace(" ", "_")+".png"));
			System.out.println("Loaded image");
		}
		catch(IOException e){
			System.out.println("Failed to load image");
		}
		buffered = (BufferedImage) image;


		for(int c=0; c<tB.getQWidth(); c++){
			for(int r=0; r<tB.getQHeight(); r++){
				if(i+c == 40 || j+r == 40){
					JOptionPane.showMessageDialog(null, "Placing a building here would be out of bounds");
					return;
				}

				if(!tiles[i+c][j+r].getValue().equals("")){
					JOptionPane.showMessageDialog(null, "Placing a building here will result to a overlap");
					return;	
				}
			}	
		}

		double w = buffered.getWidth()/tB.getQWidth();
		double h = buffered.getHeight()/tB.getQHeight();

		for(int c=0; c<tB.getQWidth(); c++){
			for(int r=0; r<tB.getQHeight(); r++){				
				tiles[i+c][j+r].setBackground(new Color(51, 204, 51));
				tiles[i+c][j+r].setIcon(new ImageIcon(  resize( buffered.getSubimage((int)w*r,(int)h*c,(int)w,(int)h), tiles[i+c][j+r].getWidth(), tiles[i+c][j+r].getHeight() )  )   );
					
				String tValue = (c==0 && r==0)? tB.getName(): i+"-"+j;
				//System.out.println(tValue);
				tiles[i+c][j+r].setValue(tValue);
			}	
		}

		//tiles[i][j].setBackground(Color.BLUE);
		bb.get(index).setText(parts[0] + "-" + newQ );
	}

	public void validAuthenticate(){
		chatStatus.setText("Enter your message below");
		connected = true;
		
		// disable login button
		login.setEnabled(false);
		// enable the 2 buttons
		logout.setEnabled(true);
		//whoIsIn.setEnabled(true);
		// disable the Server and Port JTextField
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// Action listener for when the user enter a message
		chatField.addActionListener(this);

		switchCards("Menu");
	}

	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		//whoIsIn.setEnabled(false);
		chatStatus.setText("Please login first");
		chatField.setText("");
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		chatField.removeActionListener(this);
		connected = false;
	}



	// to start the whole thing the server
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int portNumber = 1500;
		int udpPortNumber = 1600; //DEFAULT (for now). NEED A WAY TO ADD A TEXT FIELD JUST LIKE IN THE SERVERGUI


		// depending of the number of arguments provided we fall through
		switch(args.length) {
			// > javac Client username portNumber serverAddr
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java ClientGUI [serverAddress] [portNumber]");
					return;
				}
			// > javac Client username portNumber
			case 1:
				serverAddress = args[0];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java ClientGUI [serverAddress] [portNumber]");
			return;
		}


		//new ClientGUI("localhost", 1500);
		new ClientGUI(serverAddress, portNumber, udpPortNumber);
	}

}

class Tile extends JButton{
	String value;
	int i, j;

	public Tile(int i, int j){
		this.value = "";
		this.i = i;
		this.j = j;

		setBorderPainted(false);
		setFocusPainted(false);
	}

	public void setValue(String str){
		this.value = str;
	}

	public int geti(){
		return i;
	}

	public int getj(){
		return j;
	}

	public String getValue(){
		return value;
	}	
}



