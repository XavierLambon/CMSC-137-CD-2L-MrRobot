import javax.swing.*;
import javax.imageio.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;

class Building{
	private String name;
	private Coordinate pos;
	private int width, height, hp, full, quantity;
	private int qwidth, qheight;


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

	private void quickInit(String name, Coordinate pos, int width, int height, int hp, int quantity){
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

	public Building(String name, Coordinate pos){
		if(name.equals("Gold Mine")) quickInit("Gold Mine", pos, 3, 3, 960, 7);
		else if(name.equals("Elixir Collector")) quickInit("Elixir Collector", pos, 3, 3, 960, 7);
		else if(name.equals("Dark Elixir Drill")) quickInit("Dark Elixir Drill", pos, 3, 3, 1160, 3);
		else if(name.equals("Gold Storage")) quickInit("Gold Storage", pos, 3, 3, 2100, 4);
		else if(name.equals("Elixir Storage")) quickInit("Elixir Storage", pos, 3, 3, 2100, 4);
		else if(name.equals("Dark Elixir Storage")) quickInit("Dark Elixir Storage", pos, 3, 3, 3200, 1);
		else if(name.equals("Builder Hut")) quickInit("Builder Hut", pos, 2, 2, 250, 5);
		else if(name.equals("Army Camp")) quickInit("Army Camp", pos, 5, 5, 500, 4);
		else if(name.equals("Barracks")) quickInit("Barracks", pos, 3, 3, 860, 4);
		else if(name.equals("Dark Barracks")) quickInit("Dark Barracks", pos, 3, 3, 900, 2);
		else if(name.equals("Laboratory")) quickInit("Laboratory", pos, 4, 4, 950, 1);
		else if(name.equals("Spell Factory")) quickInit("Spell Factory", pos, 3, 3, 615, 1);
		else if(name.equals("Barbarian King Altar")) quickInit("Barbarian King Altar", pos, 3, 3, 250, 1);
		else if(name.equals("Dark Spell Factory")) quickInit("Dark Spell Factory", pos, 3, 3, 750, 1);
		else if(name.equals("Archer Queen Altar")) quickInit("Archer Queen Altar", pos, 3, 3, 250, 1);
		else if(name.equals("Town Hall")) quickInit("Town Hall", pos, 4, 4, 5500, 1);
		else if(name.equals("Clan Castle")) quickInit("Clan Castle", pos, 3, 3, 3400, 1);

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

	public String getName(){
		return this.name;
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

