import javax.swing.*;
import javax.imageio.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;

class Building implements ActionListener{
	private String name;
	private Coordinate pos; 
	private int hp, full, quantity, as, dmg, range;
	private int width, height; 				//width heigh in pixels
	private int qwidth, qheight; 			//quantized width and height, for ex 3x3 or 3x4,
	private boolean defense = false;
	private boolean attacking = false;

	private javax.swing.Timer timer;
	private Unit target;

	int tileCount = 40;
	int dimension = 600;
	int tileDim = (int)dimension/tileCount;

	public Building(String name, int width, int height, int hp, int quantity){
		this.name = name;
		this.qwidth = width;
		this.qheight = height;
		this.width = width*tileDim;
		this.height = height*tileDim;
		this.hp = hp;
		this.quantity = quantity;
		this.full = hp;
	}


	private void quickInit(String name, Coordinate pos, int width, int height, int hp, int quantity, boolean defense, int as, int dmg, int range){
		this.name = name;
		this.pos = pos;
		this.qwidth = width;
		this.qheight = height;
		pos.setX(pos.getX()*tileDim);
		pos.setY(pos.getY()*tileDim);
		this.width = width*tileDim;
		this.height = height*tileDim;
		this.hp = hp;
		this.quantity = quantity;
		this.full = hp;
		this.defense = defense;
		this.as = as;
		this.dmg = dmg;
		this.range = range;
		timer = new javax.swing.Timer(as, this);
	}



	public Building(String name, Coordinate pos){
		int width = 1, height = 1;
		int hp = 1, full, quantity = 1, as=0, dmg=0, range=0;
		boolean defense = false;


		if(name.equals("Gold Mine")){
			width = 3;
			height = 3;
			hp = 960;
			quantity = 7;

		}
		else if(name.equals("Elixir Collector")){
			width = 3;
			height = 3;
			hp = 960;
			quantity = 7;

		}
		else if(name.equals("Dark Elixir Drill")){
			width = 3;
			height = 3;
			hp = 1160;
			quantity =3;

		}
		else if(name.equals("Gold Storage")){
			width = 3;
			height = 3;
			hp = 2100;
			quantity = 4;

		}
		else if(name.equals("Elixir Storage")){
			width = 3;
			height = 3;
			hp = 2100;
			quantity =4;

		}
		else if(name.equals("Dark Elixir Storage")){
			width = 3;
			height = 3;
			hp = 3200;
			quantity = 1;

		}
		else if(name.equals("Builder Hut")){
			width = 2;
			height = 2;
			hp = 250;
			quantity = 5;

		}
		else if(name.equals("Army Camp")){
			width = 5;
			height = 5;
			hp = 500;
			quantity = 4;

		}
		else if(name.equals("Barracks")){
			width = 3;
			height = 3;
			hp = 860;
			quantity = 4;

		}
		else if(name.equals("Dark Barracks")){
			width = 3;
			height = 3;
			hp = 900;
			quantity = 2;

		}
		else if(name.equals("Laboratory")){
			width = 4;
			height = 4;
			hp = 950;
			quantity = 1;

		}
		else if(name.equals("Spell Factory")){
			width = 3;
			height = 3;
			hp = 615;
			quantity = 1;

		}
		else if(name.equals("Barbarian King Altar")){
			width = 3;
			height = 3;
			hp = 250;
			quantity = 1;

		}
		else if(name.equals("Dark Spell Factory")){
			width = 3;
			height = 3;
			hp = 750;
			quantity = 1;	

		}
		else if(name.equals("Archer Queen Altar")){
			width = 3;
			height = 3;
			hp = 250;
			quantity = 1;

		}
		else if(name.equals("Town Hall")){
			width = 4;
			height = 4;
			hp = 5500;
			quantity = 1;	

		}
		else if(name.equals("Clan Castle")){
			width = 3;
			height = 3;
			hp = 3400;
			quantity = 1;	

		}
		else if(name.equals("Archer Tower")){
			width = 3;
			height = 3;
			hp = 1050;
			quantity = 7;
			defense = true;	
			as = 500;
			dmg = 49;
			range = 10*tileDim;

		}
		else if(name.equals("Cannon")){
			width = 3;
			height = 3;
			hp = 1260;
			quantity = 6;
			defense = true;
			as = 800;
			dmg = 78;
			range = 9*tileDim;
		}

		quickInit(name, pos, width, height, hp, quantity, defense, as, dmg, range);
	
	}

	public Building(String name, Coordinate pos, int width, int height, int hp, int quantity){
		this.name = name;
		this.pos = pos;
		this.qwidth = width;
		this.qheight = height;
		pos.setX(pos.getX()*tileDim);
		pos.setY(pos.getY()*tileDim);
		this.width = width*tileDim;
		this.height = height*tileDim;
		this.hp = hp;
		this.quantity = quantity;
		this.full = hp;
	}

	public void actionPerformed(ActionEvent e){
		if(e.getSource() == timer){
			if(target.getHp() > 0){
				target.setHp(target.getHp()-5);
			}
			else{
				attacking = false;
				timer.stop();
			}
		}
	}

	public int getRange(){
		return range;
	}

	public void attack(Unit u){
		u.setHp(u.getHp()-dmg);
		target = u;
		attacking = true;
		timer.start();
	}

	public boolean isDefense(){
		return this.defense;
	}

	public boolean isAttacking(){
		return this.attacking;
	}

	public String getName(){
		return this.name;
	}
	public Coordinate getPos(){
		return this.pos;
	}
	public int getWidth(){
		return this.width;
	}
	public Coordinate getCenter(){
		return new Coordinate((int)(pos.getX() + width/2),(int)(pos.getY() + height/2));
	}
	public int getHeight(){
		return this.height;
	}
	public int getQWidth(){
		return this.qwidth;
	}
	public int getQHeight(){
		return this.qheight;
	}
	public int getHp(){
		return this.hp;
	}
	public int getFull(){
		return this.full;
	}
	public int getQuantity(){
		return this.quantity;
	}

	public void addDamage(int dmg){
		this.hp -= dmg;
	}

	public void setName(String name){
		this.name = name;
	}
	public void setCoordinate(Coordinate pos){
		this.pos = pos;
	}
	public void setWidth(int width){
		this.width = width;
	}
	public void setHeight(int height){
		this.height = height; 
	}
	public void setHp(int hp){
		this.hp = hp;
	}
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

}

