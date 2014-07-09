package it.univr.Dama;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/*
 * This class only creates a JPanel to be added to the "About" JFrame
 * It draws the image that contains all the informations about
 * this project
 */
public class AboutPanel extends JPanel{
	private BufferedImage aboutScreen;

	public AboutPanel(){
		try{
			aboutScreen = ImageIO.read(new File("img/strings/about.png"));
		}catch(java.io.IOException e){
			System.out.println("Nulla");
			aboutScreen = null;
		}
		
		setPreferredSize(new Dimension(800, 426));
		this.setVisible(true);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponents(g);
		g.drawImage(aboutScreen, 0, 0, 800, 426, 0, 0, 800, 426, null);
	}
}
