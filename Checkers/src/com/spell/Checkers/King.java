package com.spell.Checkers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class King extends Pawn{
	
	private static final int PEDDIM = 70;  //The dimensions of the King in pixels		
	private int offset;
	private BufferedImage whiteDam, blackDam;
	
	/* 
	 * The constructor takes three args: the position on the board of the king
	 * and an integer identifying the getPlayer() that controls it
	 */

	public King(Pawn pawn){
		super(pawn);
		offset = (Square.SQRDIM - PEDDIM)/2;
		try{
			whiteDam = ImageIO.read(new File("img/boardElements/damw.png"));
			blackDam = ImageIO.read(new File("img/boardElements/damb.png"));
		}catch(java.io.IOException e){
			whiteDam = null;
			blackDam = null;
		}
		
	}
	
	/* The paint method draws a single king with a "texture". 
	 * setColor and fillOval methods draw a sort of "shadow" on the right of the king
	 * The positions are incremented by 30 only because of the border of the game board.
	 */
	
	public void paintDamone(Graphics g){
		if(super.getPlayer() == GameData.PLAYER1){
			g.setColor(new Color(47, 36, 27));
			g.fillOval((super.getX()*Square.SQRDIM)+offset+4+30, (super.getY()*Square.SQRDIM)+offset+4+30, PEDDIM, PEDDIM);
			g.drawImage(whiteDam, (super.getX()*Square.SQRDIM)+offset+30, (super.getY()*Square.SQRDIM)+offset+30, ((super.getX()*Square.SQRDIM))+PEDDIM+offset+30, ((super.getY()*Square.SQRDIM))+PEDDIM+offset+30, 0, 0, PEDDIM, PEDDIM, null);
		} else if(super.getPlayer() == GameData.PLAYER2){
			g.setColor(new Color(47, 36, 27));
			g.fillOval((super.getX()*Square.SQRDIM)+offset+4+30, (super.getY()*Square.SQRDIM)+offset+4+30, PEDDIM, PEDDIM);
			g.drawImage(blackDam, (super.getX()*Square.SQRDIM)+offset+30, (super.getY()*Square.SQRDIM)+offset+30, ((super.getX()*Square.SQRDIM))+PEDDIM+offset+30, ((super.getY()*Square.SQRDIM))+PEDDIM+offset+30, 0, 0, PEDDIM, PEDDIM, null);
		}
	}
}
