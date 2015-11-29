import javax.swing.*;
import javax.imageio.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;


class Coordinate{
    private int x, y, dim;
    int g, h;
    public Coordinate parent;



    public Coordinate(int x, int y){
        this.parent = null;
        this.x = x;
        this.y = y;
    }
    public Coordinate(int x, int y, int dim){
        this.parent = null;
        this.x = x;
        this.y = y;
        this.dim = dim;
    }

    public void print(){
        System.out.println(" "+x+", "+y+"  -  "+dim);
    }

    public int getDim(){
        return this.dim;
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


    public void setDim(int dim){
        this.dim = dim;
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


