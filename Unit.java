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
	private int range;
	private javax.swing.Timer timer;
	private ArrayList<Coordinate> path;
	private int width = 7;
    private int height = 7;
    private Color color;
    private Building target;

	public Unit(Coordinate pos, int range, javax.swing.Timer timer){
		this.pos = pos;
		this.range = range;
		this.timer = timer;

		/*
        Random rand = new Random();
		float r = rand.nextFloat();
		float gr = rand.nextFloat();
		float b = rand.nextFloat();
		this.color = new Color(r, gr, b);
		*/
		this.color = Color.RED;
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
		this.pos = path.remove(0);
	}
	public void setPath(ArrayList<Coordinate> path){
		this.path = path;
	}
	public void setTarget(Building target){
		this.target = target;
	}
}