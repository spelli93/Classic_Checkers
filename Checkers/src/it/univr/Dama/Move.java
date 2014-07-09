package it.univr.Dama;

import java.util.ArrayList;

public class Move {
	
	private int xInit, yInit; 
    private int xDest, yDest; 
    private ArrayList<int[]> intermediatePos;
    
    /* 
     * The Move class is an abstract rappresentation of a pawn move, from xInit and yInit
     * to xDest and yDest. The args of the first constructor are
     * the initial position and the ending position.
     * The arrayList intermediatePos is a list of intermediate steps of a multiple jump
     */
    
    public Move(int x1, int y1, int x2, int y2) {
    	intermediatePos = new ArrayList<int[]>();
    	this.xInit = x1;
    	this.yInit = y1;
    	this.xDest = x2;
    	this.yDest = y2;
    }
    
    /* 
     * This constructor simply copies a move
     */
     
    public Move(Move move){
    	this.xInit = move.getxInit();
    	this.yInit = move.getyInit();
    	this.xDest = move.getxDest();
    	this.yDest = move.getyDest();
    	this.intermediatePos = new ArrayList<int[]>();
    	this.setIntermediatePos(move.getIntermediatePos());
    }
    
    /*
     * This methods returns true only if a move is a jump. 
     * A move is a jump if a pawn is moved by not only a square but 2 (simple jump)
     * or if the arrayList intermediatePos is not empty
     */
    
	boolean isJump() {
    	return (yInit - yDest == 2 || yInit - yDest == -2 || !intermediatePos.isEmpty());
    }

	public int getxInit() {
		return xInit;
	}

	public void setxInit(int xInit) {
		this.xInit = xInit;
	}

	public int getyInit() {
		return yInit;
	}

	public void setyInit(int yInit) {
		this.yInit = yInit;
	}

	public int getxDest() {
		return xDest;
	}

	public void setxDest(int xDest) {
		this.xDest = xDest;
	}

	public int getyDest() {
		return yDest;
	}

	public void setyDest(int yDest) {
		this.yDest = yDest;
	}
	
	public ArrayList<int[]> getIntermediatePos() {
		return intermediatePos;
	}

	public void setIntermediatePos(ArrayList<int[]> intermediatePos) {
		for(int[] i:intermediatePos){
			this.addIntermediatePos(i);
		}
	}
	
	/*
	 * This method adds to the ArrayList intermediatePos a position taken by argument
	 * The x is in position 0 and the y in position 1
	 */

	public void addIntermediatePos(int x, int y){
		int[] pos = new int[2];
		pos[0] = x;
		pos[1] = y;
		this.intermediatePos.add(pos);
	}
	
	public void addIntermediatePos(int[] pos){
		intermediatePos.add(pos);
	}
	
	@Override
	public String toString(){
		String res = "";
		res += "xInit: " + this.xInit + " ,yInit: " + this.yInit + " ,xDest: " + this.xDest + " ,yDest: " + this.yDest; 
		return res;
	}
}
