package com.spell.Checkers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Square{
	private int x, y;
	private Color color;
	private boolean busy;
	public static final int SQRDIM = 90; //The dimension in pixel of the square
	private BufferedImage whiteTile, blackTile;
	
	/* The square is the smallest piece of the board, where pawns are placed and moved
	 * The constructor takes four args: the position on the board of the square, the color
	 * and a boolean saying if the square is occupied by a pawn
	 */
	
	public Square(int x, int y, Color color, boolean busy){
		this.x = x/SQRDIM;
		this.y = y/SQRDIM;
		this.color = color;
		this.busy = busy;
		try{
			whiteTile = ImageIO.read(new File("img/boardElements/wt.jpg"));
			blackTile = ImageIO.read(new File("img/boardElements/bt.jpg"));
		}catch(java.io.IOException e){
			whiteTile = null;
			blackTile = null;
		}
		
	}
	/* The paint method draws a single square with a "texture". The color variable is 
	 * useful in order to know wether black or white square has to be drawn.
	 * The positions are incremented by 30 only because of the border of the game board.
	 */
	
	public void paintSquare(Graphics g){
		if(this.color == Color.black)
			g.drawImage(blackTile, (x*SQRDIM)+30, (y*SQRDIM)+30, (x*SQRDIM)+90+30, (y*SQRDIM)+90+30, 0, 0, 90, 90, null);
		else if(this.color == Color.white)
			g.drawImage(whiteTile, (x*SQRDIM)+30, (y*SQRDIM)+30, (x*SQRDIM)+90+30, (y*SQRDIM)+90+30, 0, 0, 90, 90, null);
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Color getColor() {
		return color;
	}
	
	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}
}
