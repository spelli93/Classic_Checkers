package it.univr.Dama;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

/*
 * This class creates the main frame, where the board panel is added.
 * There also is the main method, that only creates the frame.
 */
public class MainFrame extends Applet{
	
	private JFrame frame;
	
	public MainFrame(){
		BoardPanel sc = new BoardPanel();
		
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
	    
		frame = new JFrame("Dama Programmazione II 2013/2014");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		frame.add(sc);
		frame.pack();
		
		//With this method call the frame is created in the middle of the screen, independently the resolution
		frame.setLocation((screensize.width - frame.getWidth())/2, (screensize.height - frame.getHeight())/2);
		
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		MainFrame mf = new MainFrame();
		
	}
}
