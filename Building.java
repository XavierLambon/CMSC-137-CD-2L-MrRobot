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
	private int width, height, hp, quantity;

	public Building(String name, int width, int height, int hp, int quantity){
		this.name = name;
		this.width = width;
		this.height = height;
		this.hp = hp;
		this.quantity = quantity;
	}

	public Building(String name, Coordinate pos, int width, int height, int hp, int quantity){
		this.name = name;
		this.pos = pos;
		this.width = width;
		this.height = height;
		this.hp = hp;
		this.quantity = quantity;
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
	public int getHp(){
		return this.hp;
	}
	public int getQuantity(){
		return this.quantity;
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

