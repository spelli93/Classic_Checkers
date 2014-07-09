package it.univr.Dama;

import java.util.ArrayList;


public class GameData{
	
	public final static int PLAYER1 = 1;
	public final static int PLAYER2 = 2;
	private boolean kingEaten = false;
	private boolean kingCreated = false;
	
	/*
	 * The make move method takes three args: the move to be executed,
	 * the list of pawns on which the move has to be made and the board.
	 */
	
	public void makeMove(Move move, ArrayList<Pawn> pawns, Square[][] board) {
		/*
		 * If the move has no intermediate positions, it is a simple move or a simple jump
		 * so call the other makeMove method that takes as args the position.
		 * This is the base case of the recursion.
		 */
		if(move.getIntermediatePos().isEmpty()){
			makeMove(move.getxInit(), move.getyInit(), move.getxDest(), move.getyDest(), pawns, board);
		} else {
			/*
			 * The move is a multiple jump, so for every simple jump (intermediatePos) 
			 * we have to make a move (tempMove), set the initial x and y to the first 
			 * intermediatePos and then remove the first element of the intermediatePos 
			 * arrayList. Then the recursion begins, called on the tempMove.
			 */
			for(int[] i: move.getIntermediatePos()){
				Move tempMove = new Move(move);
				makeMove(tempMove.getxInit(), tempMove.getyInit(), i[0], i[1], pawns, board);
				tempMove.setxInit(i[0]);
				tempMove.setyInit(i[1]);
				tempMove.getIntermediatePos().remove(0);
				makeMove(tempMove, pawns, board);
			}
		}
    }
	
	/*
	 * The take move method takes three args: the move to be taken,
	 * the list of pawns on which the move has to be made and the board.
	 * This is simply a reversed makeMove.
	 */
	
	public void takeMove(Move move, ArrayList<Pawn> pawns, Square[][] board){
		/*
		 * If the move has no intermediate positions, it is a simple move or a simple jump
		 * so call the other takeMove method that takes as args the position.
		 * This is the base case of the recursion.
		 */
		if(move.getIntermediatePos().isEmpty()){
			takeMove(move.getxDest(), move.getyDest(), move.getxInit(), move.getyInit(), pawns, board);
		} else {
			/*
			 * The move is a multiple jump, so for every simple jump (intermediatePos) 
			 * we have to make a move (tempMove), set the destination x and y to the first 
			 * intermediatePos and then remove the first element of the intermediatePos 
			 * arrayList. Then the recursion begins, called on the tempMove.
			 */
			for(int[] i: move.getIntermediatePos()){
				Move tempMove = new Move(move);
				takeMove(tempMove.getxDest(), tempMove.getyDest(), i[0], i[1], pawns, board);
				tempMove.setxDest(i[0]);
				tempMove.setyDest(i[1]);
				tempMove.getIntermediatePos().remove(0);
				takeMove(tempMove, pawns, board);
				break;
			}
		}
		kingCreated = false; // Re-initialize damCreated and damEaten
		kingEaten = false;
	}
	
	/*
	 * The take move method takes four args: the initial and final positions,
	 * the list of pawns on which the move has to be made and the board.
	 * This is simply a reversed makeMove.
	 */
	
	public void takeMove(int x1, int y1, int x2, int y2, ArrayList<Pawn> pawns, Square[][] board) {
		
		/*
		 * If the makeMove method has created a King, to take the move we have to
		 * remove the king and add a pawn where the king was
		 */
		
		Pawn movingPawn = getPawn(x1, y1, pawns);
		if(kingCreated){
			if (y1 == 0 && getPawn(x1, y1, pawns).getPlayer() == PLAYER1){
		    	pawns.remove(movingPawn);
		    	movingPawn = new Pawn(x1, y1, PLAYER1);
		    	pawns.add(movingPawn);
		    }
		    if (y1 == 7 && getPawn(x1, y1, pawns).getPlayer() == PLAYER2){
		    	pawns.remove(movingPawn);
		    	movingPawn = new Pawn(x1, y1, PLAYER2);
		    	pawns.add(movingPawn);
		    }
		}
		
		/*
		 * Move the pawn and set the board squares busy where the pawn has been moved.
		 */
	    
		movingPawn.setX(x2);
		movingPawn.setY(y2);
		
	    board[x1][y1].setBusy(false);
	    board[x2][y2].setBusy(true);
	    
	    /*
	     * If the move was a jump, we have to place the pawn eaten where it was before.
	     * If the pawn eaten was a king we must know, in order to add it instead of adding
	     * a simple pawn.
	     */
	    
	    if (y1 - y2 == 2 || y1 - y2 == -2) {
	    	int jumpy = (y1 + y2) / 2;  
	        int jumpx = (x1 + x2) / 2;  
	        if(!kingEaten)
	        	pawns.add(new Pawn(jumpx, jumpy, movingPawn.getPlayer() == 1 ? 2 : 1));
	        else
	        	pawns.add(new King(new Pawn(jumpx, jumpy, movingPawn.getPlayer() == 1 ? 2 : 1)));
	        board[jumpx][jumpy].setBusy(true); 
	    } 
	}
	
	/*
	 * The make move method takes four args: the initial and final positions,
	 * the list of pawns on which the move has to be made and the board.
	 * This is simply a reversed takeMove.
	 */
	
	public void makeMove(int x1, int y1, int x2, int y2, ArrayList<Pawn> pedine, Square[][] board) {
		
		/*
		 * Move the pawn and set the board squares busy where the pawn has been moved.
		 */
		
		Pawn movingPawn = getPawn(x1, y1, pedine);
		movingPawn.setX(x2);
		movingPawn.setY(y2);
		
	    board[x1][y1].setBusy(false);
	    board[x2][y2].setBusy(true);
	    
	    /*
	     * If the move was a jump, we have to remove the eaten pawn from the 
	     * arrayList. If a king was eaten, we set the boolean KingEaten to true;
	     */
	    
	    if (y1 - y2 == 2 || y1 - y2 == -2) {
	    	int jumpy = (y1 + y2) / 2;  
	        int jumpx = (x1 + x2) / 2;  
	        if(getPawn(jumpx, jumpy, pedine) instanceof King)
	        	kingEaten = true;
	        pedine.remove(getPawn(jumpx, jumpy, pedine));
	        board[jumpx][jumpy].setBusy(false);
	        
	    }
	    
	    /*
	     * Here we handle the creation of Kings. If a player arrives to the end of the
	     * board (Player 1 to y = 0 and Player 2 to y = 7) we must remove a pawn and add a
	     * new King. Also we set the boolean kingCreated to true.
	     */
	    
	    if (y2 == 0 && getPawn(x2, y2, pedine).getPlayer() == PLAYER1 && !(getPawn(x2, y2, pedine) instanceof King)){
	    	pedine.remove(movingPawn);
	    	pedine.add(new King(new Pawn(x2, y2, PLAYER1)));
	    	kingCreated = true;
	    }
	    if (y2 == 7 && getPawn(x2, y2, pedine).getPlayer() == PLAYER2 && !(getPawn(x2, y2, pedine) instanceof King)){
	    	pedine.remove(movingPawn);
	    	pedine.add(new King(new Pawn(x2, y2, PLAYER2)));
	    	kingCreated = true;
	    }
	}
	
	/*
	 * The canEat method takes six args: the player turn, the position of the pawn that will try to eat,
	 * the position of the pawn that has to be eaten, the final position, the list of pawn and the board.
	 */
	
	private boolean canEat(int player, int x1, int y1, int x2, int y2, int x3, int y3, ArrayList<Pawn> pawns, Square[][] board){
		if (!isValid(x3, y3)) // If the final pos is not valid return false
				return false;
		
		if(board[x3][y3].isBusy()) // If the final pos is not free return false
			return false;
		
		if(player == PLAYER1){ // Player one turn
			/*
			 * the method returns false only if the player one is trying to eat downward and
			 * the pawn is not a king; also if the pawn that has to be eaten is of the p1
			 * or it is trying to eat a king and it is not a king
			 */
			if(getPawn(x1, y1, pawns).getPlayer() == PLAYER1 && (y3 > y1 && !(getPawn(x1, y1, pawns) instanceof King)))
				return false;
			if(getPawn(x2, y2, pawns).getPlayer() != PLAYER2 || (getPawn(x2, y2, pawns) instanceof King) && !(getPawn(x1, y1, pawns) instanceof King))
	               return false;
			return true;
		} else { // Player two turn
			/*
			 * the method returns false only if the player two is trying to eat upward and
			 * the pawn is not a king; also if the pawn that has to be eaten is of the p2
			 * or it is trying to eat a king and it is not a king
			 */
			if(getPawn(x1, y1, pawns).getPlayer() == PLAYER2 && (y3 < y1 && !(getPawn(x1, y1, pawns) instanceof King)))
				return false;
			if(getPawn(x2, y2, pawns).getPlayer() != PLAYER1 || ((getPawn(x2, y2, pawns) instanceof King) && !(getPawn(x1, y1, pawns) instanceof King)))
	               return false;
			return true;
        }
	}
	
	/*
	 * The canMove method takes five args: the player turn, the position of the pawn that will try to move,
	 * the final position, the list of pawn and the board.
	 */
	
	private boolean canMove(int player, int x1, int y1, int x2, int y2, ArrayList<Pawn> pawns, Square[][] board) {
         
         if (!isValid(x2, y2)) // If the final pos is not valid return false
            return false;  
         
         if (board[x2][y2].isBusy()) // If the final pos is not free return false
        	 return false;
         
         if (player == PLAYER1) { // Player one turn
        	 /*
        	  * the method return false only if the player one is trying to move downward
        	  * and the pawn is not a king
        	  */
            if (getPawn(x1, y1, pawns).getPlayer() == PLAYER1 && y2 > y1 && !(getPawn(x1,y1, pawns) instanceof King))
               return false;  
            return true;  
         }
         else { // Player two turn
        	 /*
        	  * the method return false only if the player two is trying to move upward
        	  * and the pawn is not a king
        	  */
            if (getPawn(x1, y1, pawns).getPlayer() == PLAYER2 && y2 < y1 && !(getPawn(x1,y1, pawns) instanceof King))
               return false;  
            return true;  
         }
	}
	
	/*
	 * isValid returns true only if the position argument is inside the board
	 */
	
	private boolean isValid(int x, int y){
		if((x <= 7 && x >= 0) && (y <= 7 && y >= 0)){
			return true;
		}
		return false;
	}
	
	/*
	 * This method returns an arrayList with all possible simple jumps.
	 * Arguments are: the player turn, the position of the pawn, the arrayList of pawn
	 * and the board.
	 */
	
	public ArrayList<Move> jumpsFrom(int player, int x, int y, ArrayList<Pawn> pawns, Square[][] board) {
        /*
         * We create an ArrayList of moves and add to it where the pawn can eat.
         * Returns null if there are no possible simple jumps.
         */
        ArrayList<Move> moves = new ArrayList<Move>();
        if (getPawn(x, y, pawns).getPlayer() == player || getPawn(x, y, pawns) instanceof King) {
           if (canEat(player, x, y, x+1, y+1, x+2, y+2, pawns, board)) 
              moves.add(new Move(x, y, x+2, y+2));
           if (canEat(player, x, y, x-1, y+1, x-2, y+2, pawns, board)) 
              moves.add(new Move(x, y, x-2, y+2));
           if (canEat(player, x, y, x+1, y-1, x+2, y-2, pawns, board)) 
              moves.add(new Move(x, y, x+2, y-2));
           if (canEat(player, x, y, x-1, y-1, x-2, y-2, pawns, board)) 
              moves.add(new Move(x, y, x-2, y-2));
        }
        if (moves.size() == 0)
        	return null;
        else {
        	return moves;
       }
    }
	
	/*
	 * This method implements a binary-tree search algorithm in order to get all possible 
	 * multiple jumps. It takes as args the player turn, the initial position of the pawn, the
	 * ArrayList of pawns, the first simple jump, an ArrayList of moves on which we will add
	 * all possible jumps and the board.
	 */
	
	public void possibleJumps(int player, int x, int y, ArrayList<Pawn> pawns, Move jump, ArrayList<Move> moves, Square[][] board) {

		//We get all possible simple jump from the initial position
	    ArrayList<Move> simpleJ = this.jumpsFrom(player, x, y, pawns, board);
	    
	    /*
	     * if there are no possible jumps, we set the destination pos to be the x
	     * and y passed as parameter. We also remove the last intermediatePos but
	     * only if that ArrayList is not empty. This is done in order to not have 
	     * the last intermediatePos equal the desination position.
	     */
	    if (simpleJ == null) {
	        jump.setxDest(x);
	        jump.setyDest(y);
	        if(!jump.getIntermediatePos().isEmpty())
	        	jump.getIntermediatePos().remove(jump.getIntermediatePos().size()-1);
	        
	        moves.add(new Move(jump));
	        return;
	    }

	    /*
	     * For every simple jump we create a temp ArrayList of pawns, copy of the parameter one,
	     * we create a temp move, copy of the parameter one, we add to the temp move intermediatePos
	     * the destination position of the jump move passed as parameter (that is the previous jump).
	     * Then we make the move virtually, we call begin the recursion from the destination of the last jump 
	     * and then we take the move.
	     */
	    for(Move j:simpleJ) {
	    	ArrayList<Pawn> tempPawns = copyPawns(pawns);
	    	Move nj = new Move(jump);
	    	nj.addIntermediatePos(j.getxDest(), j.getyDest());	
	    	makeMove(j, tempPawns, board);
	    	
	    	possibleJumps(player, j.getxDest(), j.getyDest() , tempPawns, new Move(nj) , moves, board);
	    	takeMove(j, tempPawns, board);
	    }
	}
	
	/*
	 * The method possJumps returns an ArrayList of Moves from the other method
	 * possibleJumps.
	 */
	
	public ArrayList<Move> possJumps(int player, int x, int y, ArrayList<Pawn> pawns, Move jump, Square[][] board){
		ArrayList<Move> moves = new ArrayList<Move>();
		this.possibleJumps(player, x, y, pawns, jump, moves, board);
		return moves;
	}
	
	/*
	 * This method returns an Array of all possible moves of one player.
	 * It takes 3 args: the player, the ArrayList of pawns and the board.
	 */
	
	public Move[] legalMoves(int player, ArrayList<Pawn> pawns, Square[][] board) {
		  
		ArrayList<Move> moves = new ArrayList<Move>();
       
        /*
         * For every Pawn we see if the pawn can eat, and if yes, we add to the arrayList 
         * (that will be converted to an array later) all possible multiple jumps.
         */
        for(Pawn pawn:pawns){
        	if (pawn.getPlayer() == player) {
        		int x = pawn.getX();
        		int y = pawn.getY();
        		 
        		if (canEat(player, x, y, x+1, y+1, x+2, y+2, pawns, board)){
        			moves.addAll(this.possJumps(player, x, y, pawns, new Move(x, y, x+2, y+2), board));
        		}
                if (canEat(player, x, y, x-1, y+1, x-2, y+2, pawns, board)){
                	moves.addAll(this.possJumps(player, x, y, pawns, new Move(x, y, x+2, y+2), board));
                }
                if (canEat(player, x, y, x+1, y-1, x+2, y-2, pawns, board)){
                    moves.addAll(this.possJumps(player, x, y, pawns, new Move(x, y, x+2, y+2), board));
                }
                if (canEat(player, x, y, x-1, y-1, x-2, y-2, pawns, board)){
                	moves.addAll(this.possJumps(player, x, y, pawns, new Move(x, y, x+2, y+2), board));
                }
        	}   
        }
         
         /*
          * If there are no moves at this point, all pawn can only move, so we 
          * add all possible movements.
          */
      
        if (moves.size() == 0) {
        	for(Pawn pedina:pawns){
                 if (pedina.getPlayer() == player) {
                	 int x = pedina.getX();
                	 int y = pedina.getY();
                	 if (canMove(player,x,y,x+1,y+1, pawns, board))
                		  moves.add(new Move(x,y,x+1,y+1));
                	 if (canMove(player,x,y,x-1,y+1, pawns, board))
                		  moves.add(new Move(x,y,x-1,y+1));
                	 if (canMove(player,x,y,x+1,y-1, pawns, board))
                		  moves.add(new Move(x,y,x+1,y-1));
                	 if (canMove(player,x,y,x-1,y-1, pawns, board))
                		  moves.add(new Move(x,y,x-1,y-1));
                 }
            }
        }
        //If there are no moves, we return null
        if (moves.size() == 0)
           return null;
        else { //else we convert the arrayList to an array and we return it
           Move[] movesArr = new Move[moves.size()];
           for (int i = 0; i < moves.size(); i++)
              movesArr[i] = moves.get(i);
           return movesArr;
        } 
    }

	/*
	 * This method return a Pawn of coords x and y (parameters) from an 
	 * ArrayList of pawns
	 */
	private Pawn getPawn(int x, int y, ArrayList<Pawn> pawns){
		//TODO
		Pawn pedina = new Pawn();
		for(Pawn ped:pawns){
			if(ped.getX() == x && ped.getY() == y){
				pedina = ped;
				break;
			}
		}
		
		return pedina;
	}
	
	/*
	 * This method clones an ArrayList of Pawn taken as arguments
	 */
	private ArrayList<Pawn> copyPawns(ArrayList<Pawn> pawns){
		ArrayList<Pawn> copy = new ArrayList<Pawn>();
		for(Pawn ped:pawns){	
			if(ped instanceof King){
				King pawnAsKing = new King(ped);
				copy.add(pawnAsKing);
			}
			else
				copy.add(new Pawn(ped.getX(), ped.getY(), ped.getPlayer()));
		}
		return copy;
	}
}

