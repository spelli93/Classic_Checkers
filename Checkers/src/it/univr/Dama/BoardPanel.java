package it.univr.Dama;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
 * This is the biggest class of the entire project: it handles every
 * object of the game, the graphics part and it's also a mouseListener and an 
 * Action Listener. It's a panel, so it has to be added to the mainFrame.
 */

public class BoardPanel extends JPanel implements ActionListener, MouseListener{
	
	/*Dimension variables, of the Entire panel (780 + the size of the side panel(240) and 780 + 
	 * the menu bar (23)). In addiction there are two variables defining the size of the button
	 * on the side panel
	 */
	private static int HEIGHT = 780 + 23;
	private static int WIDTH = 780 + 240;
	private final int BUTTONWIDTH = 200;
	private final int BUTTONHEIGHT = 60;
	Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
	
	/*
	 * Those variables are all main game objects
	 */
	private Square[][] board;
	private GameData gd;
	private ArrayList<Pawn> pawns;
	private int playerTurn;
	private Move[] legalMoves;
	private CPU cpu;
	private Move cpuMove;
	
	/*
	 * selectX and selectY are variables keeping track on where the user clicks
	 * various booleans useful in order to know which strings are to be drawn.
	 * WinnerPlayer is declared in order to draw the right congrats image!
	 */
	private int selectX = -1;
	private int selectY = -1;
	private boolean vsCpu = false, cDifficulty = false, isLoading = false;
	private boolean playing;
	private int winnerPlayer = 0;
	
	/*
	 * A simple JMenuBar
	 */
	private JMenuBar menu;
	private JMenuItem reset, info, vsHuman, easyM, mediumM, hardM, quit;
	private JMenu newGame, vsCPU;
	
	/*
	 * All the image we use in the projects
	 */
	private BufferedImage G1, G2, border, panel, buttonWait, terminateString, changeTurn, loadingString;
	private BufferedImage[] numbers = new BufferedImage[13];
	
	
	
	/*
	 * Static variables created for diffculty choosing
	 */
	public static boolean easy = false;
	public static boolean medium = false;
	public static boolean hard = false;
	
	/*
	 * The constructor simply initializes all elements
	 */
	public BoardPanel(){		
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		this.gd = new GameData();
		this.cpu = new CPU();
		this.board = new Square[8][8];
		initBoard();
		this.pawns = new ArrayList<Pawn>();
		this.menu = new JMenuBar();
		
		JMenu game = new JMenu("Partita");
		JMenu about = new JMenu("About");
		newGame = new JMenu("Nuova Partita");
		vsCPU = new JMenu("vs CPU");
		
		for(int i = 0; i < numbers.length; i++){
			try{
				numbers[i] = ImageIO.read(new File("img/numbers/" + i + ".png"));
			}catch(java.io.IOException e){
				System.out.println("Nulla");
				numbers[i] = null;
			}
		}
		
		
		try{
			G1 = ImageIO.read(new File("img/strings/G1.png"));
			G2 = ImageIO.read(new File("img/strings/G2.png"));
			border = ImageIO.read(new File("img/boardElements/border.png"));
			panel = ImageIO.read(new File("img/boardElements/Panel.png"));
			buttonWait = ImageIO.read(new File("img/strings/loadingString.png"));
			terminateString = ImageIO.read(new File("img/strings/terminate.png"));
			changeTurn = ImageIO.read(new File("img/boardElements/changeTurn.png"));
			loadingString = ImageIO.read(new File("img/strings/loading.png"));
			
			
		}catch(java.io.IOException e){
			System.out.println("Nulla");
			G1 = null;
			G2 = null;
			border = null;
			panel = null;
			buttonWait = null;
			terminateString = null;
			changeTurn = null;
			loadingString = null;
		}
		
		reset = new JMenuItem("Arrenditi");
		info = new JMenuItem("Informazioni");
		vsHuman = new JMenuItem("vs Giocatore");
		easyM = new JMenuItem("Facile");
		mediumM = new JMenuItem("Normale");
		hardM = new JMenuItem("Difficile");
		quit = new JMenuItem("Esci");
		
		
		vsHuman.addActionListener(this);
		reset.addActionListener(this);
		info.addActionListener(this);
		easyM.addActionListener(this);
		mediumM.addActionListener(this);
		hardM.addActionListener(this);
		quit.addActionListener(this);
		
		newGame.add(vsHuman);
		newGame.add(vsCPU);
		vsCPU.add(easyM);
		vsCPU.add(mediumM);
		vsCPU.add(hardM);
		
		game.add(newGame);
		game.add(reset);
		game.add(quit);
		about.add(info);
		
		game.setBorder(BorderFactory.createLineBorder(Color.black));
		
		menu.add(game);
		menu.add(about);
		
		this.addMouseListener(this);
		this.setLayout(new BorderLayout());
		
		this.add(menu, BorderLayout.SOUTH);
		this.setVisible(true);
		
	}
	
	/*
	 * This method creates a new game, but only if we are not playing.
	 * Sets the first turn to the first player, gets the legal Moves of
	 * the first player and initializes all pawns on the board.
	 */
    void newGame() {
    	
    	if(!playing){
	        this.pawns.removeAll(pawns);
	        deselAllBoard();
	        initPawns(); 
	        playerTurn = GameData.PLAYER1;
	        legalMoves = gd.legalMoves(GameData.PLAYER1, pawns, board);
	        
	        playing = true;
	        winnerPlayer = 0;
	        selectX = -1;
	        repaint();
    	} 
	        
     }
    
    /*
     * (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     * This huge method draws literally everything. We used Graphics 2D because
     * in our opinion there was too aliasing on the board.
     */

	@Override
	protected void paintComponent(Graphics g){
		
		Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
		
		super.paintComponent(g);
		
		//Draw the panel and the number of the alive pawns, both of player 1 and 2
		g.drawImage(panel, 780, 0, 780+240, 780, 0, 0, 240, 780, null);
		g.drawImage(numbers[getWhitePawns()], 780+90, 120, 780+90+60, 120+60, 0, 0, 60, 60, null);
		g.drawImage(numbers[getBlackPawns()], 780+90, 280, 780+90+60, 280+60, 0, 0, 60, 60, null);
		
		//Draw the button to change the turn and get the move by the cpu. Only if we
		//are playing against AI
		if(vsCpu && playerTurn == GameData.PLAYER2){
			g.drawImage(changeTurn, 800, 680, 800+BUTTONWIDTH, 680+BUTTONHEIGHT, 0, 0, BUTTONWIDTH, BUTTONHEIGHT, null);
			g.setColor(Color.black);
			g.drawRect(800, 680, BUTTONWIDTH, BUTTONHEIGHT);
			g.drawRect(800+1, 680+1, BUTTONWIDTH-1, BUTTONHEIGHT-1);
		}

		g.drawImage(border, 0, 0, 780, 780, 0, 0, 780, 780, null);
		if(playerTurn == GameData.PLAYER2 && vsCpu && !isLoading){
			g.drawImage(buttonWait, 825, 470, 825+151, 470+151, 0, 0, 151, 151, null);
		}
		if(playerTurn == GameData.PLAYER2 && vsCpu && isLoading){
			g.drawImage(loadingString, 825, 470, 825+151, 470+151, 0, 0, 151, 151, null);
		}
		if(cDifficulty){
			g.drawImage(terminateString, 825, 470, 825+151, 470+151, 0, 0, 151, 151, null);
		}
		
		//Paint all the squares of the board and pawns
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				board[i][j].paintSquare(g);
			}
		}
		
		for(Pawn pawn : pawns){
			pawn.paintPawn(g);
		}
		
		
		/*
		 * Here we draw some squares that helps the user to select possible moves and possible movable pawns
		 * In addiction squares that keep track of the cpu move (is playing against it) are drawn on the board
		 * If a square is selected a little white square is drawn upon others
		 */
		if (playing) {
			//Possible movable pawns
			if(!vsCpu || this.playerTurn == GameData.PLAYER1){
		        g.setColor(Color.red);
		        for (int i = 0; i < legalMoves.length; i++) {
		        	g2D.drawRect(legalMoves[i].getxInit()*Square.SQRDIM+30, legalMoves[i].getyInit()*Square.SQRDIM+30, Square.SQRDIM, Square.SQRDIM);
		            g2D.drawRect(3 + legalMoves[i].getxInit()*Square.SQRDIM+30, 3 + legalMoves[i].getyInit()*Square.SQRDIM+30, Square.SQRDIM - 6, Square.SQRDIM - 6);
		            g2D.drawRect(2 + legalMoves[i].getxInit()*Square.SQRDIM+30, 2 + legalMoves[i].getyInit()*Square.SQRDIM+30, Square.SQRDIM - 4, Square.SQRDIM - 4);
		            
		        }
			}
	        //Possible destinations of a move
	        if (selectX >= 0) {
	            g2D.setColor(Color.white);
	            g2D.drawRect(3 + selectX*Square.SQRDIM+30, 3 + selectY*Square.SQRDIM+30, Square.SQRDIM - 6, Square.SQRDIM - 6);
	            g2D.setColor(Color.green);
	            for (int i = 0; i < legalMoves.length; i++) {
	            	if (legalMoves[i].getxInit() == selectX && legalMoves[i].getyInit() == selectY) {
	            		g2D.drawRect(legalMoves[i].getxDest()*Square.SQRDIM+30, legalMoves[i].getyDest()*Square.SQRDIM+30, Square.SQRDIM, Square.SQRDIM);
	            		g2D.drawRect(2 + legalMoves[i].getxDest()*Square.SQRDIM+30, 2 + legalMoves[i].getyDest()*Square.SQRDIM+30, Square.SQRDIM - 4, Square.SQRDIM - 4);
	                    g2D.drawRect(3 + legalMoves[i].getxDest()*Square.SQRDIM+30, 3 + legalMoves[i].getyDest()*Square.SQRDIM+30, Square.SQRDIM - 6, Square.SQRDIM - 6);
	                    for(int[] j:legalMoves[i].getIntermediatePos()){
	                    	g.setColor(Color.blue);
	                    	g2D.drawRect(j[0]*Square.SQRDIM+30, j[1]*Square.SQRDIM+30, Square.SQRDIM, Square.SQRDIM);
		            		g2D.drawRect(2 + j[0]*Square.SQRDIM+30, 2 + j[1]*Square.SQRDIM+30, Square.SQRDIM - 4, Square.SQRDIM - 4);
		                    g2D.drawRect(3 + j[0]*Square.SQRDIM+30, 3 + j[1]*Square.SQRDIM+30, Square.SQRDIM - 6, Square.SQRDIM - 6);
	                    }
	            	}
	            }
	        }
	        //Previous move of the cpu
	        if(playerTurn == GameData.PLAYER1 && cpuMove != null){
	        	g.setColor(Color.orange);
	        	g2D.drawRect(cpuMove.getxDest()*Square.SQRDIM+30, cpuMove.getyDest()*Square.SQRDIM+30, Square.SQRDIM, Square.SQRDIM);
        		g2D.drawRect(2 + cpuMove.getxDest()*Square.SQRDIM+30, 2 + cpuMove.getyDest()*Square.SQRDIM+30, Square.SQRDIM - 4, Square.SQRDIM - 4);
                g2D.drawRect(3 + cpuMove.getxDest()*Square.SQRDIM+30, 3 + cpuMove.getyDest()*Square.SQRDIM+30, Square.SQRDIM - 6, Square.SQRDIM - 6);
                g2D.drawRect(cpuMove.getxInit()*Square.SQRDIM+30, cpuMove.getyInit()*Square.SQRDIM+30, Square.SQRDIM, Square.SQRDIM);
        		g2D.drawRect(2 + cpuMove.getxInit()*Square.SQRDIM+30, 2 + cpuMove.getyInit()*Square.SQRDIM+30, Square.SQRDIM - 4, Square.SQRDIM - 4);
                g2D.drawRect(3 + cpuMove.getxInit()*Square.SQRDIM+30, 3 + cpuMove.getyInit()*Square.SQRDIM+30, Square.SQRDIM - 6, Square.SQRDIM - 6);
	        	if(!cpuMove.getIntermediatePos().isEmpty()){
	        		for(int[] j:cpuMove.getIntermediatePos()){
			        	//g.setColor(Color.blue);
		            	g2D.drawRect(j[0]*Square.SQRDIM+30, j[1]*Square.SQRDIM+30, Square.SQRDIM, Square.SQRDIM);
		        		g2D.drawRect(2 + j[0]*Square.SQRDIM+30, 2 + j[1]*Square.SQRDIM+30, Square.SQRDIM - 4, Square.SQRDIM - 4);
		                g2D.drawRect(3 + j[0]*Square.SQRDIM+30, 3 + j[1]*Square.SQRDIM+30, Square.SQRDIM - 6, Square.SQRDIM - 6);
	        		}
	        	}
	        }
		}
		
		//Two Images that say what player has won the game!
		if(winnerPlayer == 1){
			g2D.drawImage(G1, 35, 0, 755, 720, 0, 0, 720, 720, null);
		} else if(winnerPlayer == 2){
			g2D.drawImage(G2, 35, 0, 755, 720, 0, 0, 720, 720, null);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 * this is the standard mousePressed method. It knows if we are playing against
	 * a human opponent or the cpu.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		cDifficulty = false;
		//Here we are playing against human 
		if(!vsCpu){
			reset.setEnabled(true);
			if (playing){
				
				int x = ((e.getX()-30) / Square.SQRDIM);
				int y = ((e.getY()-30) / Square.SQRDIM);
				if ((x >= 0 && x < 8 && y >= 0 && y < 8)){
					clickOnSquare(x, y);	
				}	
			}
	    } else {
	    	//Here against the CPU
	    	if(playing){
		    	int x = (e.getX()-30) / Square.SQRDIM;
				int y = (e.getY()-30) / Square.SQRDIM;
			    if(playerTurn == GameData.PLAYER1){
			    	clickOnSquare(x, y);
			    	reset.setEnabled(false);
			    }else{
			    	reset.setEnabled(true);
			    	int x1 = (e.getX()-30);
			    	int y1 = (e.getY()-30);
			    	if((x1 >= 772 && x1 <= 772+BUTTONWIDTH) && (y1 >= 653 && y1 <= 653+BUTTONHEIGHT)){
			    		isLoading = true;
			    		this.paintImmediately(825, 470, 825+151, 470+151);
			    		makeCPUMove();
			    		isLoading = false;
			    	}
			    }		
			}
	    }
	}
	/*
	 * This method gets the maxCpuMove, using the negamax algorithm and 
	 * makes it.
	 */
	private void makeCPUMove(){
		cpuMove = cpu.findMaxMove(legalMoves, pawns, gd, board);
		makeMove(cpuMove);
	}
	
	/*
	 * The clickOnSquare method tries to understand if we click on a possible move or not.
	 * If yes, it makes that move. If not, returns and we have to click somewhere valid.
	 */
	private void clickOnSquare(int x, int y) {
        
        for (int i = 0; i < legalMoves.length; i++){
        	if (legalMoves[i].getxInit() == x && legalMoves[i].getyInit() == y) {
        		selectX = x;
        		selectY = y;
        		repaint();
        		return;
        	}
        }
        
        
        if (selectX < 0) {
        	return;
        }
        
        
        for (int i = 0; i < legalMoves.length; i++){
        	if (legalMoves[i].getxInit() == selectX && legalMoves[i].getyInit() == selectY
              && legalMoves[i].getxDest() == x && legalMoves[i].getyDest() == y) {
        		makeMove(legalMoves[i]);
        		return;
        	}
        }
     }
	
	/*
	 * Here we recall the GameData makeMove and we switch the turn.
	 * Also we put selectX to be -1, in order to "clear" the board from
	 * paints of the old move
	 */
	public void makeMove(Move mossa) {
        
        gd.makeMove(mossa, pawns, board);
        
        changeTurn();
        
        selectX = -1;
        repaint();
	}
	
	/*
	 * Simply switch turn and if a player has no more legalMoves, this player 
	 * lose the game and we call the gameOver function.
	 */
	private void changeTurn(){
		if (playerTurn == GameData.PLAYER1) {
        	playerTurn = GameData.PLAYER2;
        	legalMoves = gd.legalMoves(playerTurn, pawns, board);
        	if (legalMoves == null)
        		gameOver(GameData.PLAYER1);
          
        }
        else {
        	playerTurn = GameData.PLAYER1;
        	legalMoves = gd.legalMoves(playerTurn, pawns, board);
        	if (legalMoves == null)
        		gameOver(GameData.PLAYER2);
        }
	}
	
	void gameOver(int player){
        playing = false;
        winnerPlayer = player;
        BoardPanel.easy = false;
        BoardPanel.medium = false;
        BoardPanel.hard = false;
        repaint();
	}
	
	/*
	 * Here we add the possibility to the player to give up, calling the gameOver function again
	 */
	void giveUp(){
		 if (playing == false) {
	         return;
	     }
	     if (playerTurn == GameData.PLAYER1)
	    	 gameOver(GameData.PLAYER2);
	     else
	    	 gameOver(GameData.PLAYER1);     
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * This action Performed handles buttons in the JMenuBar.
	 */
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		 Object src = evt.getSource();
         if (src == vsHuman){
        	 if(!playing){
	        	 vsCpu = false;
	        	 cpuMove = null;
	        	 newGame();
        	 } else {
        		cDifficulty = true;
         		this.paintImmediately(825, 470, 825+151, 470+151);
        	 }
         }
         else if (src == reset)
        	 giveUp();
         else if(src == info)
        	 aboutFrame();
         else if(src == easyM){
        	 if(!playing){
	        	 vsCpu = true;
	        	 cpuMove = null;
	        	 BoardPanel.easy = true;
	        	 newGame();
        	 } else {
        		cDifficulty = true;
          		this.paintImmediately(825, 470, 825+151, 470+151);
        	 }
         }
         else if(src == mediumM){
        	 if(!playing){
	        	 vsCpu = true;
	        	 cpuMove = null;
	        	 BoardPanel.medium = true;
	        	 newGame();
        	 } else {
        		cDifficulty = true;
          		this.paintImmediately(825, 470, 825+151, 470+151);
        	 }
         }
         else if(src == hardM){
        	 if(!playing){
	        	 vsCpu = true;
	        	 cpuMove = null;
	        	 BoardPanel.hard = true;
	        	 Object[] options = {"Si",
                 "No"};
	        	 int choose = JOptionPane.showOptionDialog(this, "Attenzione, se si seleziona la difficoltˆ massima,\ni tempi di caricamento della mossa della IA saranno lunghi.\nSei sicuro di voler continuare?",
	        			 "Attenzione!!", JOptionPane.YES_NO_OPTION, 
	        			 JOptionPane.WARNING_MESSAGE, 
	        			 null, options, options[1]);
	        	 if(choose == 0)
	        		 newGame();
        	 } else {
        		cDifficulty = true;
          		this.paintImmediately(825, 470, 825+151, 470+151);
        	 }
         }
         else if(src == quit)
        	 System.exit(0);
         
	}
	
	/*
	 * Here is the creation of the about frame with a panel and an image inside
	 * It's the about frame, in which there are informations
	 */
	private void aboutFrame(){
		JFrame frame = new JFrame("About");
   	 	frame.setPreferredSize(new Dimension(800, 426));
   	 	frame.setLocation(this.getLocationOnScreen());
   	 	frame.setResizable(false);
   	 	frame.add(new AboutPanel());
   	 	frame.pack();
   	 	frame.setVisible(true);
	}
	
	/*
	 * Next two methods initialize the board and all pawns
	 */
	private void initBoard(){
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				if((i % 2) == (j % 2)){
					board[i][j] = new Square(i * Square.SQRDIM, j * Square.SQRDIM, Color.black, false);
				}else{
					board[i][j] = new Square(i * Square.SQRDIM, j * Square.SQRDIM, Color.white, false);
				}
			}
		}
	}
	
	public void initPawns(){
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 3; j++){
				if((i % 2) == (j % 2)){
					pawns.add(new Pawn(i, j, 2));
					board[i][j].setBusy(true);
				}
			}
		}
		
		for(int i = 0; i < 8; i++){
			for(int j = 5; j < 8; j++){
				if((i % 2) == (j % 2)){
					pawns.add(new Pawn(i, j, 1));
					board[i][j].setBusy(true);
				}
			}
		}
	}
	
	private void deselAllBoard(){
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length; j++){
				board[i][j].setBusy(false);
			}
		}
	}
	
	private int getBlackPawns(){
		int count = 0;
		for(Pawn pawn:pawns){
			if(pawn.getPlayer() == GameData.PLAYER2)
				count++;
		}
		return count;
	}
	
	private int getWhitePawns(){
		int count = 0;
		for(Pawn pawn:pawns){
			if(pawn.getPlayer() == GameData.PLAYER1)
				count++;
		}
		return count;
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
