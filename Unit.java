import javax.swing.*;
import javax.imageio.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;

class Unit {
	private Coordinate pos;
	private String name;
	private int range, ms, as, dmg;
	private javax.swing.Timer timer;
	private ArrayList<Coordinate> path;
	private int width = 7;
    private int height = 7;
    private Color color;
    private Building target;
    private boolean attacking;
    private int full, hp = 200;

	int tileCount = 40;
	int dimension = 600;
	int tileDim = (int)dimension/tileCount;

	public Unit(Coordinate pos, int range, javax.swing.Timer timer, int as, int ms){
		this.pos = pos;
		this.range = range;
		this.timer = timer;
		this.as = as;
		this.ms = ms;
		attacking = false;

		/*
        Random rand = new Random();
		float r = rand.nextFloat();
		float gr = rand.nextFloat();
		float b = rand.nextFloat();
		this.color = new Color(r, gr, b);
		*/
		if(range == 1){
			this.color = Color.RED;
		}

		else{
			this.color = Color.BLUE;

		}
	}

	public Unit(String name, Coordinate pos, javax.swing.Timer timer){
		this.width = 7;
		this.height = 7;
		//int hp = 1, full = 1, ms = 1, as, dmg, range;

		this.name = name;
		this.pos = pos;
		this.timer = timer;

		if(name.equals("Barbarian")){
			this.hp = 125;
			this.ms = 16;
			this.as = 1000;
			this.dmg = 30;
			this.range = 1;
			this.color = new Color(255, 0, 0);
			
		}
		else if(name.equals("Archer")){
			this.hp = 48;
			this.ms = 24;
			this.as = 1000;
			this.dmg = 25;
			this.range = 4*tileDim;
			this.color = new Color(0, 0, 255);
		}

		this.full = hp;
		timer.setDelay(ms*2);
		
	}

	public void setHp(int hp){
		this.hp = hp;
	}

	public float getHpPercent(){
		return (float)(this.hp/this.full);
	}

	public int getHp(){
		return this.hp;
	
	}

	public int getFull(){
		return this.full;
	}


	public int getDamage(){
		return this.dmg;
	}

	public boolean isAttacking(){
		return attacking;
	}

	public void attack(){
		this.timer.stop();
		this.timer.setDelay(as);
		this.timer.start();
		attacking = true;
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
	public Building getTarget(){
		return this.target;
	}
	public void stopTimer(){
		this.timer.stop();
	}
	public void startTimer(){
		this.timer.start();
	}
	public void move(){
		if(attacking){
			attacking = false;
			this.timer.stop();
			this.timer.setDelay(ms);
			this.timer.start();
		}
		this.pos = path.remove(0);
	}
	public void setPath(ArrayList<Coordinate> path){
		this.path = path;
	}
	public void setTarget(Building target){
		this.target = target;
	}
}