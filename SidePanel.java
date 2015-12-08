import javax.swing.*;
import javax.imageio.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;


public class SidePanel extends JPanel{
	public SidePanel(){
		//this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setSize(200,600);
		this.setPreferredSize(new Dimension(200,600));
		this.setBounds(800,0,200,600);
	}

	public SidePanel(LayoutManager l){
		this.setLayout(l);
		this.setSize(200,600);
		this.setPreferredSize(new Dimension(200,600));
		this.setBounds(800,0,200,600);
	}
}

class MainPanel extends JPanel{
	public MainPanel(){
		this.setPreferredSize(new Dimension(600,600));
		this.setSize(600,600);
		this.setBounds(200,0,600,600);
	}
	public MainPanel(LayoutManager l){
		this.setLayout(l);
		this.setPreferredSize(new Dimension(600,600));
		this.setSize(600,600);
		this.setBounds(200,0,600,600);
	}
}

class CButton extends JButton{
	public CButton(String s){
		this.setText(s);
		this.setPreferredSize(new Dimension(150,30));
		this.setSize(12 ,this.getHeight());
	}
}
