import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class LoadTester{

	Scanner in = new Scanner(System.in);
	ArrayList<Long> timeArr = new ArrayList<Long>();
	ArrayList<Client> clientArr = new ArrayList<Client>();


	public LoadTester(){
		System.out.print("Enter number of clients you want to create: ");
		int num = in.nextInt();


		for(int i=0; i<num; i++){
			//ms = 0;

			try {

			long startTime = System.nanoTime();

			Client tc = new Client("localhost", 1500, "user", "password");
			tc.start();

			long endTime = System.nanoTime();
			clientArr.add(tc);
			timeArr.add(endTime - startTime); 

			}
			catch(Exception e){
				i--;
			}
			
		}
			

		for(long time : timeArr){
			System.out.println(time+"ms");
		}
		
	}


	public static void main(String args[]){
		new LoadTester();
	}

}