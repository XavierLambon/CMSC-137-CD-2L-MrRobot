import javax.swing.*;
import javax.imageio.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;

class Map extends JPanel implements ActionListener {
    int[][] logicMap;
	int tileCount = 40;
    int dimension;
    int range = 1;
    ArrayList<Unit> unitArr;
    ArrayList<Building> buildingArr;

    public Map(int dimension) {
        Map me = this;

        setBorder(BorderFactory.createLineBorder(Color.black));
        
        this.dimension = dimension;

        buildingArr = new ArrayList<Building>();
        unitArr = new ArrayList<Unit>();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	Coordinate pos = new Coordinate(e.getX(),e.getY());
                int ms = 5;
                int as = 20;
                int range = 1;
                Unit unit = new Unit("Barbarian", pos, new javax.swing.Timer(ms, me));
            	//Unit unit = new Unit(pos, range, new javax.swing.Timer(ms, me), as, ms);
                unit.setTarget(findNearestBuilding(unit));
            	unit.setPath(aStarSearch(unit, unit.getTarget()));
                unit.startTimer();
                unitArr.add(unit);
            	repaint();
            }
        });
    }

    public boolean isInRange(Unit u, Building b){
        if(dist(u.getPos(), b.getCenter()) <= b.getRange()){
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent e) {

        for(Unit unit : unitArr){
            if(unit.getHp() <= 0){
                unit.stopTimer();
                unitArr.remove(unit);
                unit = null;
            }

            /*
            for(Building building : buildingArr){
                if(building.isDefense() && !building.isAttacking() && isInRange(unit, building)){
                    building.attack(unit);
                }
            }
            */
            
            if(e.getSource() == unit.getTimer()){
                if(unit.getPath().size() > 0){
                    repaint();
                    unit.move();
        			repaint();
        		}
        		else{
                    if(!unit.isAttacking()){
                        unit.attack();
                    }
                    
                    unit.getTarget().addDamage(unit.getDamage());
                    repaint();

                    if(unit.getTarget().getHp() <= 0){
                        unit.stopTimer();
                        buildingArr.remove(unit.getTarget());
                        unit.setTarget(findNearestBuilding(unit));
                        if(unit.getTarget() != null){
                            unit.setPath(aStarSearch(unit, unit.getTarget()));
                            unit.startTimer();
                        }
                        repaint();
                    }

        		}
        	}
        }
    }

    public void setBuildings(ArrayList<Building> bArr){
        buildingArr = bArr;
        unitArr = new ArrayList<Unit>();
        repaint();
    }
    

    public Dimension getPreferredSize() {
        return new Dimension(dimension,dimension);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 

        int td = (int) dimension/tileCount;
        
        //paint background
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
        	g.fillRect(unit.getPos().getX()-((int)unit.getWidth()/2),unit.getPos().getY()-((int)unit.getHeight()/2),unit.getWidth(),unit.getHeight());
		
            float hpPercent = (float)(unit.getHp()/unit.getFull());
            int green = (int)(255*hpPercent);
            int red = 255-green;

            g.setColor(Color.BLACK);
            g.drawRect(unit.getPos().getX(),unit.getPos().getY()-5,7,3);
            g.setColor(new Color(red%255, green%255, 0));
            g.fillRect(unit.getPos().getX(),unit.getPos().getY()-5,(int)(7*hpPercent),3);
           
        }

        //paints buildings
        for(Building building : buildingArr){

            Image image = null;
            BufferedImage buffered;
            try{
                image = ImageIO.read(new File("images/"+building.getName().replace(" ", "_")+".png"));
            }
            catch(IOException e){

            }
            buffered = (BufferedImage) image;



            float hpPercent = (float)building.getHp()/building.getFull();
            int green = (int)(255*hpPercent);
            int red = 255-green;

            g.setColor(Color.BLACK);
            g.drawRect(building.getPos().getX(),building.getPos().getY()-10,building.getWidth(),5);
            g.setColor(new Color(red, green, 0));
            g.fillRect(building.getPos().getX(),building.getPos().getY()-10,(int)(building.getWidth()*hpPercent),5);
            
            g.setColor(Color.BLUE);
            g.drawImage(image, building.getPos().getX(),building.getPos().getY(),building.getWidth(),building.getHeight(), Color.GREEN, null);
        }

    }


    public ArrayList<Coordinate> findNeighbors(Coordinate c){
        ArrayList<Coordinate> neighbors = new ArrayList<Coordinate>();
        for(int i=0; i<3; i++)
            for(int j=0; j<3; j++){
                int m = c.getX()-1+i;
                int n = c.getY()-1+j;
                if(m>=0 && m<dimension && n>=0 && n<dimension){
                    neighbors.add(new Coordinate(m,n,c.getDim()));
                }
            }
        return neighbors;
    }


    //assumes building is square
    public boolean isInRange(Coordinate u, Coordinate g, int r){
    	Coordinate u1 = new Coordinate(u.getX()-u.getDim(), u.getY()-u.getDim());
    	Coordinate g1 = new Coordinate(g.getX()-g.getDim(), g.getY()-g.getDim());
    	Coordinate u2 = new Coordinate(u.getX()+u.getDim(), u.getY()+u.getDim());
    	Coordinate g2 = new Coordinate(g.getX()+g.getDim(), g.getY()+g.getDim());
    	if( u2.getX()+r >= g1.getX()  &&  u1.getX()-r <= g2.getX()  &&  u1.getY()+r <= g2.getY()  &&  u2.getY()-r >= g1.getY() ){
    		return true;
    	}
    	else return false;
    }

    public Building findNearestBuilding(Unit unit){
    	Coordinate nc = null;
        Coordinate uPos = unit.getPos();
        Building nb = null;
    	for(Building building : buildingArr){
            Coordinate bPos = building.getPos();
    		if(nc == null || dist(uPos, bPos) < dist(uPos, nc)){
    			nc = bPos;
    			nb = building;
            }
    	}
    	return nb;
    }

    public ArrayList<Coordinate> aStarSearch(Unit unit, Building building){
        ArrayList<Coordinate> openList = new ArrayList<Coordinate>();
        ArrayList<Coordinate> closedList = new ArrayList<Coordinate>();


        Coordinate start = new Coordinate(unit.getPos().getX(), unit.getPos().getY(), (int)(unit.getWidth()/2));
        int adjX = building.getPos().getX() + (int)(building.getWidth()/2);
        int adjY = building.getPos().getY() + (int)(building.getHeight()/2);

        Coordinate goal = new Coordinate(adjX, adjY, (int)(building.getWidth()/2));
        int range = unit.getRange();


        openList.add(start); 

        start.setG(0);
        start.setH(dist(start, goal));

        while( !(openList.isEmpty()) ) { 
            Coordinate currentC = openList.remove(0); 
            ArrayList<Coordinate> neighbors = findNeighbors(currentC);
            for(Coordinate neighbor : neighbors){

                Coordinate newC = new Coordinate(neighbor.getX(), neighbor.getY(), neighbor.getDim());
                newC.setParent(currentC);

                //System.out.println("Still searching");
                if (isInRange(newC, goal, range)){
                    //System.out.println("Done searching");
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
