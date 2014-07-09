package com.spell.Checkers;

import java.util.ArrayList;

public class CPU {

	/*
	 * Those variables are weights. 
	 */
	
	private static final int PAWN = 100;
	private static final int KING = 200;
	private static final int EDGE = 25;
	private static final int SECONDLINE = 15;
	private static final int POSITION = 1;
	

	/*
	 * This is the heuristic evaluation function. It returns an integer
	 * that reflects the state of the board and if it's favourable for
	 * one player or another. It takes as arguments the ArrayList of pawns, 
	 * that is the board state, and the turn.
	 */

	public int evaluatePawns(ArrayList<Pawn> pawns, int turn){
		int evaluation = 0;
		int who2move;
		
		/*
		 * for every pawn on the board we look if it's of player 1 or 2.
		 * If the pawn is controlled by player 1, we add the weight of the pawn
		 * (or the king if the pawn is a king).
		 * We also evaluate the position of the pawn, that is greater and greater
		 * approaching the opposite edge of the board.
		 */
		for(Pawn pawn:pawns){
			if(pawn.getPlayer() == GameData.PLAYER1 && !(pawn instanceof King)){
				evaluation += PAWN;
				evaluation += POSITION*pawn.getY()*pawn.getY();
			}
			/*
			 * Kings are penalyzed if they stay on the edge or on the second line.
			 */
			if(pawn.getPlayer() == GameData.PLAYER1 && (pawn instanceof King)){
				evaluation += KING;
				if(pawn.getY() == 0 || pawn.getY() == 7)
					evaluation -= EDGE;
				if(pawn.getX() == 0 || pawn.getX() == 7)
					evaluation -= EDGE;
				if(pawn.getY() == 1 || pawn.getY() == 6)
					evaluation -= SECONDLINE;
				if(pawn.getX() == 1 || pawn.getX() == 6)
					evaluation -= SECONDLINE;
			}
			
			/*
			 * Same as before, but inverted.
			 */
			if(pawn.getPlayer() == GameData.PLAYER2 && !(pawn instanceof King)){
				evaluation -= PAWN;
				evaluation -= POSITION*(7-pawn.getY())*(7-pawn.getY());
			}
			if(pawn.getPlayer() == GameData.PLAYER2 && (pawn instanceof King)){
				evaluation -= KING;
				if(pawn.getY() == 0 || pawn.getY() == 7)
					evaluation += EDGE;
				if(pawn.getX() == 0 || pawn.getX() == 7)
					evaluation += EDGE;
				if(pawn.getY() == 1 || pawn.getY() == 6)
					evaluation += SECONDLINE;
				if(pawn.getX() == 1 || pawn.getX() == 6)
					evaluation += SECONDLINE;
			}
		}
		
		/*
		 * We use the negamax algorithm, so this function has to be
		 * sensible to the side of when it's called. The maximizing player is the player1 
		 * and the minimizing is the player 2.
		 * In few words, the player2 has to minimize player1 advantage.
		 * For this reason we return the evaluation multiplyed by a sign,
		 * who2move, positive for p1 and negative for p2.
		 */
		if(turn == GameData.PLAYER1)
			who2move = 1;
		else
			who2move = -1;
		return who2move*evaluation;
	}
	
	/*
	 * This is the evaluation used in easy difficulty. It only counts how many
	 * black or white pawn are on the board.
	 */
	private int evalPawnsEasy(ArrayList<Pawn> pawns, int turn){
		int whiteCount=0;
		int blackCount=0;
		int who2move;
		if(turn == GameData.PLAYER1)
			who2move = 1;
		else
			who2move = -1;
		for(Pawn pawn:pawns){
			if(pawn.getPlayer() == GameData.PLAYER1){
				whiteCount++;
			} else {
				blackCount++;
			}
		}
		return  (whiteCount-blackCount) * who2move;
	}
	
	/*
	 * negaMax is the main function of the CPU. It is a recursive binary-search tree 
	 * that evaluates all leaf nodes and maximize or minimize depending on depth, till it
	 * arrives on root. Here we find the best move the player can make.
	 * Negamax is different from minimax only because it uses the principle
	 * that max(a,b) is equal to min(-a, -b). So it is a easier framework to use.
	 * We also implemented alpha and beta pruning, in order to speed up the evaluation
	 * cutting down the tree from unuseful nodes.
	 */
	
	public int negaMax(ArrayList<Pawn> pawns, int depth, int turn, int alpha, int beta, GameData gd, Square[][] board){
		
		Move[] moves = gd.legalMoves(turn, pawns, board);
		int maxScore = Integer.MIN_VALUE;
		
		if((depth == 0 || moves == null)) 
			return BoardPanel.easy ? evalPawnsEasy(pawns, turn) : evaluatePawns(pawns, turn);
		
		for(Move move:moves){
			gd.makeMove(move, pawns, board);
			int score = -negaMax(pawns, depth-1, opponent(turn), -beta, -alpha, gd, board);
			gd.takeMove(move, pawns, board);
			maxScore = Math.max(maxScore, score);
			System.out.println("MOVE " + move.toString() +  " Depth: " + depth + " , of player: " + turn + " Score " + maxScore);

			alpha = Math.max(alpha, score);
			if(alpha >= beta)
				break;
		}
		
		return maxScore;
	}
	
	
	/*
	 * The method findMaxMove returns the move whose evaluation is higher. It uses,
	 * of course, the negamax function.
	 */
	
	public Move findMaxMove(Move[] moves, ArrayList<Pawn> pawns, GameData gd, Square[][] board){
		ArrayList<Pawn> tempPawns = copyPawns(pawns);
		Square[][] boardTemp = copyBoard(board);
		int maxScore = Integer.MIN_VALUE;
		Move bestMove = null;
		for (Move move : moves){
			System.out.println(move.toString());
			gd.makeMove(move, tempPawns, boardTemp);
			
			//We selected a maxDepth of 2, move and counter.
			int newScore = 0;
			if(BoardPanel.medium)
				newScore = -negaMax(tempPawns, 2, GameData.PLAYER1, Integer.MIN_VALUE, Integer.MAX_VALUE, gd, boardTemp);
			else if(BoardPanel.easy)
				newScore = -negaMax(tempPawns, 2, GameData.PLAYER1, Integer.MIN_VALUE, Integer.MAX_VALUE, gd, boardTemp);
			else if(BoardPanel.hard)
				newScore = -negaMax(tempPawns, 4, GameData.PLAYER1, Integer.MIN_VALUE, Integer.MAX_VALUE, gd, boardTemp);
			
			gd.takeMove(move, tempPawns, boardTemp);
			if (newScore > maxScore){
				maxScore = newScore;
				
				bestMove = move;
			}
			
		}
		System.out.println("SELECTED MOVE: " + bestMove.toString() + " , SCORE: " + maxScore);
		return bestMove;
	}
	
	/*
	 * This function copies all pawns into a new ArrayList. It is used in order to 
	 * not make any change to the real board.
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
	
	/*
	 * Change turn
	 */
	
	private int opponent(int player){
		if(player == GameData.PLAYER1)
			return GameData.PLAYER2;
		else
			return GameData.PLAYER1;
	}
	
	/*
	 * This function copies the board into a array of Squares. It is used in order to 
	 * not make any change to the real board.
	 */
	
	private Square[][] copyBoard(Square[][] board){
		Square[][] squares = new Square[8][8];
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
					squares[i][j] = new Square(board[i][j].getX(), board[i][j].getY(), board[i][j].getColor(), board[i][j].isBusy());
			}
		}
		return squares;
	}
	
}
