package it.univr.Dama;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Pawn{
	private int x, y;
	public static final int PAWNDIM = 70;
	private int player; 		//1 is player 1, 2 is player2 or CPU
	private int offset;
	private BufferedImage whitePed, blackPed;

	/* 
	 * The constructor takes three args: the position on the board of the pawn
	 * and an integer identifying the player that controls it
	 */

	public Pawn(int x, int y, int player){
		this.x = x;
		this.y = y;
		this.player = player;
		offset = (Square.SQRDIM- PAWNDIM)/2;
		try{
			whitePed = ImageIO.read(new File("img/boardElements/wp.png"));
			blackPed = ImageIO.read(new File("img/boardElements/bp.png"));
		}catch(java.io.IOException e){
			whitePed = null;
			blackPed = null;
		}
	}

	public Pawn(){
		this.x = 0;
		this.y = 0;
		this.player = 0;
	}
	/* The paint method draws a single pawn with a "texture". 
	 * If the pawn is an instance of a King it calls the paint method on the King.
	 * setColor and fillOval methods draw a sort of "shadow" on the right of the pawn
	 * The positions are incremented by 30 only because of the border of the game board.
	 */
	
	public Pawn(Pawn pawn) {
		this.x = pawn.getX();
		this.y = pawn.getY();
		this.player = pawn.getPlayer();
	}

	public void paintPawn(Graphics g){
		
		if(this instanceof King){
			King thisAsKing = (King) this;
			thisAsKing.paintDamone(g);
		} else {
			if(this.player == GameData.PLAYER1){
				g.setColor(new Color(47, 36, 27));
				g.fillOval((x*Square.SQRDIM)+offset+4+30, (y*Square.SQRDIM)+offset+4+30, PAWNDIM, PAWNDIM);
				g.drawImage(whitePed, (x*Square.SQRDIM)+offset+30, (y*Square.SQRDIM)+offset+30, ((x*Square.SQRDIM))+PAWNDIM+offset+30, ((y*Square.SQRDIM))+PAWNDIM+offset+30, 0, 0, PAWNDIM, PAWNDIM, null);
			} else if(this.player == GameData.PLAYER2){
				g.setColor(new Color(47, 36, 27));
				g.fillOval((x*Square.SQRDIM)+offset+4+30, (y*Square.SQRDIM)+offset+4+30, PAWNDIM, PAWNDIM);
				g.drawImage(blackPed, (x*Square.SQRDIM)+offset+30, (y*Square.SQRDIM)+offset+30, ((x*Square.SQRDIM))+PAWNDIM+offset+30, ((y*Square.SQRDIM))+PAWNDIM+offset+30, 0, 0, PAWNDIM, PAWNDIM, null);
			}
		}
	}
	
	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
