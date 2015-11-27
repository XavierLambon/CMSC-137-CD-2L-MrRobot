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

	JPanel gp;
	Tile[][] tiles;

	ArrayList<Building> bList;
	Map map2;
	JPanel map, cmControls;
	JButton back, save;
	ArrayList<JRadioButton> bb;


	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField tf;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort;
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn, customMap, tMove;
	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort, defaultUDPPort;
	private String defaultHost;

	private JTextField usernameField;
	private JTextField passwordField;

	// Constructor connection receiving a socket number
	ClientGUI(String host, int port, int udpPort) {

		super("Clash of Clans");
		defaultPort = port;
		defaultUDPPort = udpPort;
		defaultHost = host;
		
		// the server name and the port number
		
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		


		JPanel northPanel = new JPanel();

		label = new JLabel("Please login first", SwingConstants.CENTER);
		tf = new JTextField(18);
		tf.setBackground(Color.WHITE);
		
		northPanel.add(label);
		northPanel.add(tf);
		northPanel.setBounds(0,0,200,50);


		JPanel centerPanel = new JPanel(null);

		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JScrollPane jsp = new JScrollPane(ta);
		jsp.setBounds(0,50,200,550);
		
		centerPanel.setSize(1000,600);
		centerPanel.add(northPanel);
		centerPanel.add(jsp);
		


		usernameField = new JTextField("user", 20);
		passwordField = new JTextField("password", 20);
		// the 3 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		
		//game panel
		gp = new JPanel(new CardLayout());
		gp.setBounds(200,0,800,600);

		JPanel loginp = new JPanel();
		loginp.add(usernameField);
		loginp.add(passwordField);
		loginp.add(login);

		JPanel mainmenu = new JPanel();

		customMap = new JButton("Customize Map");
		tMove = new JButton("Troop Movement");
		customMap.addActionListener(this);
		tMove.addActionListener(this);
		mainmenu.add(customMap);
		mainmenu.add(tMove);
		mainmenu.add(logout);

	


		JPanel cm = new JPanel(null);
		cm.setPreferredSize(new Dimension(800,600));
		cm.setSize(800,600);
		//cm.setBackground(Color.BLUE);
		cm.setBounds(200,0,800,600);


		JPanel cm2 = new JPanel(null);
		cm2.setPreferredSize(new Dimension(800,600));
		cm2.setSize(800,600);
		//cm.setBackground(Color.BLUE);
		cm2.setBounds(200,0,800,600);


		map2 = new Map(600);
		map2.setPreferredSize(new Dimension(600,600));
		map2.setSize(600,600);
		map2.setBounds(0,0,600,600);
		cm2.add(map2);
		
		int mapsize = 40;
		int tilesize = 15;
		map = new JPanel(new GridLayout(mapsize,mapsize));
		map.setPreferredSize(new Dimension(600,600));
		map.setSize(600,600);
		map.setBounds(0,0,600,600);
		tiles = new Tile[mapsize][mapsize];
		for(int i=0; i<mapsize; i++){
			tiles[i] = new Tile[mapsize];
			for(int j=0; j<mapsize; j++){
				tiles[i][j] = new Tile(i, j);
				tiles[i][j].setPreferredSize(new Dimension(tilesize, tilesize));
				tiles[i][j].setSize(tilesize, tilesize);
				tiles[i][j].addActionListener(this);
				
				if( (i+j)%2 == 0 ) 
					tiles[i][j].setBackground(new Color(102, 255, 51));
				else
					tiles[i][j].setBackground(new Color(51, 204, 51));
				
				map.add(tiles[i][j]);
			}
		}
		
		cm.add(map);

		

		cmControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cmControls.setSize(200,600);
		cmControls.setPreferredSize(new Dimension(200,600));
		cmControls.setBounds(600,0,200,600);
		//cmControls.setBackground(Color.BLACK);
		back = new JButton("Main Menu");
		back.setSize(150,30);
		back.setPreferredSize(new Dimension(150,30));
		back.addActionListener(this);
		cmControls.add(back);
		


		createBuildings();
		bb = new ArrayList<JRadioButton>();
		//JOptionPane.showMessageDialog(null, "blist size-"+bList.size());

		ButtonGroup group = new ButtonGroup();

		for(int i=0; i<bList.size(); i++){
			JRadioButton button = new JRadioButton(bList.get(i).name+'-'+bList.get(i).quantity);
			bb.add(button);
			cmControls.add(button);
			group.add(button);
		}
		JRadioButton button = new JRadioButton("Remove Building");
		bb.add(button);
		cmControls.add(button);
		group.add(button);

		JScrollPane jsp2 = new JScrollPane(cmControls);
		jsp2.setSize(200,600);
		jsp2.setPreferredSize(new Dimension(200,600));
		jsp2.setBounds(600,0,200,600);
		cm.add(jsp2);
		//cm.add(group);

		gp.add(loginp, "Login");
		gp.add(cm, "Map Customization");
		gp.add(cm2, "Troop Movement");
		gp.add(mainmenu, "Main Menu");


		centerPanel.add(gp);


		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);		// you have to login before being able to Who is in

		JPanel southPanel = new JPanel();
		southPanel.add(whoIsIn);

		JPanel ns = new JPanel(new GridLayout(1,2));
		ns.add(southPanel);
		ns.add(new JPanel());
		//add(ns, BorderLayout.SOUTH);

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
		tf.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
		
	}
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Please login first");
		tf.setText("");
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		tf.removeActionListener(this);
		connected = false;
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

	}
		
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			ta.setText("");

			CardLayout cl = (CardLayout)(gp.getLayout());
    		cl.show(gp, "Login");
			return;
		}
		// if it the who is in button
		if(o == whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			return;
		}

		if(o == customMap){
			CardLayout cl = (CardLayout)(gp.getLayout());
    		cl.show(gp, "Map Customization");
			return;
		}

		if(o == tMove){
			CardLayout cl = (CardLayout)(gp.getLayout());
    		cl.show(gp, "Troop Movement");
			return;
		}

		if(o == back){
			CardLayout cl = (CardLayout)(gp.getLayout());
    		cl.show(gp, "Main Menu");
			return;
		}

		for(int i=0; i<40; i++){
			for(int j=0; j<40; j++){
				if(o == tiles[i][j]){
					String clientInput = "coordinate: "+i+", "+j;
					client.sendUDP(clientInput);	//can add a 'type' parameter for future use. i'll handle that~
					System.out.println(clientInput);	//currently HERE
					
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
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));				
			tf.setText("");
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
			tf.setText("");
			ta.setText("");
		}

	}

	public boolean getConnectStatus(){
		return connected;
	}

	public void validAuthenticate(){
		label.setText("Enter your message below");
		connected = true;
		
		// disable login button
		login.setEnabled(false);
		// enable the 2 buttons
		logout.setEnabled(true);
		whoIsIn.setEnabled(true);
		// disable the Server and Port JTextField
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// Action listener for when the user enter a message
		tf.addActionListener(this);

		CardLayout cl = (CardLayout)(gp.getLayout());
		cl.show(gp, "Main Menu");
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
			if( bList.get(b).name.equals(tiles[i][j].value) ){
				tB = bList.get(b);
				break;
			}
		}

		for(int c=0; c<tB.width; c++){
			for(int r=0; r<tB.height; r++){

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
			if( bList.get(b).name.equals(parts[0]) ){
				tB = bList.get(b);
				//System.out.println("here");
				break;
			}
		}

		Image image = null;
		BufferedImage buffered;
		try{
			image = ImageIO.read(new File("images/"+tB.name.replace(" ", "_")+".png"));
		}
		catch(IOException e){

		}
		buffered = (BufferedImage) image;


		for(int c=0; c<tB.width; c++){
			for(int r=0; r<tB.height; r++){
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

		double w = buffered.getWidth()/tB.width;
		double h = buffered.getHeight()/tB.height;

		//System.out.println("width:"+buffered.getWidth());
		//System.out.println("height:"+buffered.getHeight());
		//System.out.println("w:"+w+"  h:"+h);

		for(int c=0; c<tB.width; c++){
			for(int r=0; r<tB.height; r++){				
				tiles[i+c][j+r].setBackground(new Color(51, 204, 51));
				tiles[i+c][j+r].setIcon(new ImageIcon(  resize( buffered.getSubimage((int)w*r,(int)h*c,(int)w,(int)h), tiles[i+c][j+r].getWidth(), tiles[i+c][j+r].getHeight() )  )   );
					
				String tValue = (c==0 && r==0)? tB.name: i+"-"+j;
				//System.out.println(tValue);
				
				tiles[i+c][j+r].setValue(tValue);
			}	
		}

		//tiles[i][j].setBackground(Color.BLUE);
		bb.get(index).setText(parts[0] + "-" + newQ );
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


class Building{
	public String name;
	public int width, height, hp, quantity;

	public Building(String name, int width, int height, int hp, int quantity){
		this.name = name;
		this.width = width;
		this.height = height;
		this.hp = hp;
		this.quantity = quantity;
	}
}

class Coordinate{
    private int x, y;
    int g, h;
    public Coordinate parent;


    public Coordinate(int x, int y){
        this.parent = null;
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public int getG(){
        return this.g;
    }
    public int getH(){
        return this.h;
    }
    public int getF(){
        return this.g + this.h;
    }
    public Coordinate getParent(){
        return this.parent;
    }


    public boolean isIn(ArrayList<Coordinate> alc){
        for (Coordinate c : alc) {
            if(this.x == c.getX() && this.y == c.getY()){
                return true;
            }
        }
        return false;
    }
    public boolean isEqual(Coordinate c){
        if(this.x == c.getX() && this.y == c.getY()){
            return true;
        }
        return false;
    }


    public void setG(int g){
        this.g = g;
    }
    public void setH(int h){
        this.h = h;
    }
    public void setParent(Coordinate parent){
        this.parent = parent;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
}

class Unit {
	private Coordinate pos;
	private int range;
	private javax.swing.Timer timer;
	private ArrayList<Coordinate> path;
	private int width = 7;
    private int height = 7;
    private Color color;

	public Unit(Coordinate pos, int range, javax.swing.Timer timer, ArrayList<Coordinate> path){
		this.pos = pos;
		this.range = range;
		this.timer = timer;
		this.path = path;
		this.timer.start(); 

        Random rand = new Random();
		float r = rand.nextFloat();
		float gr = rand.nextFloat();
		float b = rand.nextFloat();
		this.color = new Color(r, gr, b);
	}

	public Coordinate getPos(){
		return this.pos;
	}
	public int getWidth(){
		return this.width;
	}
	public int getHeight(){
		return this.height;
	}
	public int getRange(){
		return this.range;
	}
	public javax.swing.Timer getTimer(){
		return this.timer;
	}
	public ArrayList<Coordinate> getPath(){
		return this.path;
	}
	public Color getColor(){
		return this.color;
	}
	public void stopTimer(){
		this.timer.stop();
	}
	public void move(){
		this.pos = path.remove(0);
	}
}

class Map extends JPanel implements ActionListener {
	int tileCount = 40;
    int dimension;
    Coordinate goal; 
    int range = 30;
    ArrayList<Unit> unitArr;

    public Map(int dimension) {
    	this.dimension = dimension;
    	goal = new Coordinate(dimension/2, dimension/2);
    	unitArr = new ArrayList<Unit>();

        setBorder(BorderFactory.createLineBorder(Color.black));

        Map me = this;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	Unit tempUnit = new Unit(new Coordinate(e.getX(),e.getY()), 200, new javax.swing.Timer(3, me), aStarSearch(new Coordinate(e.getX(),e.getY()), goal));
            	unitArr.add(tempUnit);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {

        for(Unit unit : unitArr){
        	if(e.getSource() == unit.getTimer()){
        		if(unit.getPath().size() > 0){
        			int OFFSET = 1;
		            repaint(unit.getPos().getX(),unit.getPos().getY(),unit.getWidth()+OFFSET,unit.getHeight()+OFFSET);
		            unit.move();
		            repaint(unit.getPos().getX(),unit.getPos().getY(),unit.getWidth()+OFFSET,unit.getHeight()+OFFSET);
        		}
        		else{
        			unit.stopTimer();
        		}
        	}
        }
    }
    

    public Dimension getPreferredSize() {
        return new Dimension(dimension,dimension);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 

        int td = (int) dimension/tileCount;
        for(int i=0; i<tileCount; i++){
        	for(int j=0; j<tileCount; j++){
		        if( (i+j)%2 == 0 ) 
					g.setColor(new Color(102, 255, 51));
				else
					g.setColor(new Color(51, 204, 51));
				g.fillRect(i*td, j*td, td, td);     
        	}
        } 


        //paints units
		for(Unit unit : unitArr){
			g.setColor(unit.getColor());
        	g.fillRect(unit.getPos().getX(),unit.getPos().getY(),unit.getWidth(),unit.getHeight());
		}

		//paints the goal
        g.setColor(Color.RED);
        g.fillRect(goal.getX() - ((int)td/2),goal.getY()- ((int)td/2),td,td);
        //g.fillRect(goal.getX(),goal.getY(),td,td);
    }


    public ArrayList<Coordinate> findNeighbors(Coordinate c){
        ArrayList<Coordinate> neighbors = new ArrayList<Coordinate>();
        for(int i=0; i<3; i++)
            for(int j=0; j<3; j++){
                int m = c.getX()-1+i;
                int n = c.getY()-1+j;
                if(m>=0 && m<dimension && n>=0 && n<dimension){
                    neighbors.add(new Coordinate(m,n));
                }
            }
        return neighbors;
    }

    public ArrayList<Coordinate> aStarSearch(Coordinate start, Coordinate goal){
        ArrayList<Coordinate> openList = new ArrayList<Coordinate>();
        ArrayList<Coordinate> closedList = new ArrayList<Coordinate>();
        openList.add(start); 

        start.setG(0);
        start.setH(dist(start, goal));

        while( !(openList.isEmpty()) ) { 
            Coordinate currentC = openList.remove(0); 

            ArrayList<Coordinate> neighbors = findNeighbors(currentC);

            for(Coordinate neighbor : neighbors){

                Coordinate newC = new Coordinate(neighbor.getX(),neighbor.getY());
                newC.setParent(currentC);

                if (dist(newC,goal) < range){
                    System.out.println("Done searching");
                    ArrayList<Coordinate> pathArr = new ArrayList<Coordinate>();
			        do{
			            pathArr.add(0, newC);
			            newC = newC.parent;
			        }while(newC != null);
                    return pathArr;
                }
                
                newC.setG(currentC.getG() + dist(newC, currentC));
                newC.setH(dist(newC, goal));

                int flag = 1;
                if(newC.isIn(closedList))
                    continue;

                if (!newC.isIn(openList)){
                    enqueue(newC, openList);
                }
                else {
                    for (Coordinate c : openList) {
                        if(newC.getX() == c.getX() && newC.getY() == c.getY()){
                            // This is not a better path.
                            if(newC.getF() >= c.getF()){
                                flag = 0;        
                            }
                            break;
                        }
                    }
                } 
                if(flag == 1){
                    enqueue(newC, openList);
                }
            }

            closedList.add(currentC); 

        }
        return null;
    } 

    public void enqueue(Coordinate c, ArrayList<Coordinate> alc){
        for(int i=0; i<alc.size(); i++){
            if(alc.get(i).getF() >= c.getF()){
                alc.add(i, c);
                return;
            }
        }
        alc.add(c);
    } 


    public int dist(Coordinate p1, Coordinate p2){
        int x1 = p1.getX();
        int x2 = p2.getX();
        int y1 = p1.getY();
        int y2 = p2.getY();
        return (int) Math.round(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)));
    }

}
