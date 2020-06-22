package com.fatalcubez.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.sound.sampled.Clip;

public class BikeBoard implements Fing {

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;
    public static int ROW=0, COL=0;
    public int ROD=0, COD=0;
	public static final int ROWS = 5;
	public static final int COLS = 4;

	private final int startingTiles = 1;
	private Tile[][] board;
	private boolean dead;
	private boolean won;
	private boolean ju;
	private BufferedImage gameBoard;
	private int x;
	private int y;
    private int b=0;
    private int gt=0;
    private boolean g=true;
	private static int SPACING = 10;
	public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
	public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + (ROWS) * Tile.HEIGHT;
    private int vvv=0;
	private long elapsedMS;
	private long startTime;
	private boolean hasStarted;
	static boolean bbb;
	
	private ScoreManager scores;
	private AudioHandler audio;
	static int saveCount = 0;
  
	public BikeBoard(int x, int y) {
		ju=true;
		this.x = x;
		this.y = y;
		bbb = true;
		board = new Tile[ROWS][COLS];
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		createBoardImage();
        
		audio = AudioHandler.getInstance();
		audio.load("click.wav", "click");
		
		scores = new ScoreManager(this);
		scores.loadGame();
		if(scores.newGame()){
			start();
			scores.saveGame();
		}
		else{

			for(int i = 0; i < 16; i++){
				if(scores.getBoard()[i] == 0) continue;
				spawn(i / 4+1, i % COLS, scores.getBoard()[i]);
				
			}

			dead = checkDead();

			won = checkWon();
		}
	}
	
	public void fill() {
		board = new Tile[ROWS][COLS];
		for(int i = 0; i < 16; i++){
			if(scores.getBoard()[i] == 0) continue;
			spawn(i / 4+1, i % COLS, scores.getBoard()[i]);}
			spawnRandom();
		
	}

	public void reset(){
		board = new Tile[ROWS][COLS];
		start();
		scores.saveGame();
		dead = false;
		won = false;
		hasStarted = false;
		startTime = System.nanoTime();
		elapsedMS = 0;
		saveCount = 0;
	}

	private void start() {
		for (int i = 0; i < startingTiles; i++) {
			spawnRandom();
		}}
	

	/** Debug method */
	private void spawn(int row, int col, int value) {
		board[row][col] = new Tile(value, getTileX(col), getTileY(row));
	}

	private void createBoardImage() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g.setColor(Color.lightGray);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				int x = SPACING + SPACING * col + Tile.WIDTH * col;
				int y = SPACING + SPACING * row + Tile.HEIGHT * row;
				g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
			}
		}
	}

	public void update() {
		if(ju) Tile.imm=false;
		else Tile.imm=true;
		saveCount++;
		gt++;
		if (gt > 100) {
			gt = 0;}
		if (saveCount >= 120) {
			saveCount = 0;
			scores.saveGame();
		}
		
		if (!won && !dead) {
			if (hasStarted) {
				elapsedMS = (System.nanoTime() - startTime) / 1000000;
				scores.setTime(elapsedMS);
			}
			else {
				startTime = System.nanoTime();
			}
		}

		checkKeys();
		
		if(vvv==0) {
			vvv=90;
			spawnRandom();
			setDead(checkDead());
			}
			
		if (scores.getCurrentScore() > scores.getCurrentTopScore()) {
			scores.setCurrentTopScore(scores.getCurrentScore());
		}

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null) continue;
				current.update();
				resetPosition(current, row, col);
				if (current.getValue() == 2048) {
					setWon(true);
				}
			}
		}
	}

	public void render(Graphics2D g) {
		BufferedImage finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g2d.drawImage(gameBoard, 0, 0, null);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null) continue;
				current.render(g2d);
			}
		}

		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();

		
	}
    
	private void resetPosition(Tile tile, int row, int col) {
		if (tile == null) return;

		int x = getTileX(col);
		int y = getTileY(row);
         
		int distX = tile.getX() - x;
		int distY = tile.getY() - y;

		if (Math.abs(distX) < Tile.SLIDE_SPEED & distX!=0) {
			tile.setX(tile.getX() - distX);
		    return;
		}

		if (Math.abs(distY) < Tile.SLIDE_SPEED & distY!=0) {
			tile.setY(tile.getY() - distY);
			return;
		}
		
//		if (Math.abs(distX) < Tile.SLIDE_SPEED & distX > 0) {
//			tile.setX(tile.getX() - distX);
//		}
//
//		if (Math.abs(distX) < Tile.SLIDE_SPEED & distX < 0) {
//			tile.setX(tile.getX() + distX);
//		}
//		
//		if (Math.abs(distY) <= Tile.SLIDE_SPEED & distY > 0) {
//			System.out.println(333333);
//			tile.setY(tile.getY() - distY);
//		}
//
//		if (Math.abs(distY) <= Tile.SLIDE_SPEED & distY < 0) {
//			System.out.println(444444);
//			tile.setY(tile.getY() + distY);
//		}
//
//		if (distX < 0 & Math.abs(distX) > Tile.SLIDE_SPEED) {
//			tile.setX(tile.getX() + Tile.SLIDE_SPEED);
//		}
//		if (distY < 0 & Math.abs(distY) > Tile.SLIDE_SPEED) {
//			System.out.println(77777);
//			tile.setY(tile.getY() + Tile.SLIDE_SPEED);
//		}
//		if (distX > 0 & Math.abs(distX) > Tile.SLIDE_SPEED) {
//			tile.setX(tile.getX() - Tile.SLIDE_SPEED);
//		}
//		if (distY > 0 & Math.abs(distY) > Tile.SLIDE_SPEED) {
//			System.out.println(88888);
//			tile.setY(tile.getY() - Tile.SLIDE_SPEED);
//		}
		
		if (distX < 0) {
			tile.setX(tile.getX() + Tile.SLIDE_SPEED);
		}
		if (distY < 0) {
			tile.setY(tile.getY() + Tile.SLIDE_SPEED);
		}
		if (distX > 0) {
			tile.setX(tile.getX() - Tile.SLIDE_SPEED);
		}
		if (distY > 0) {
			tile.setY(tile.getY() - Tile.SLIDE_SPEED);
		}
	}

	public int getTileX(int col) {
		return SPACING + col * Tile.WIDTH + col * SPACING;
	}

	public int getTileY(int row) {
		return SPACING + row * Tile.HEIGHT + row * SPACING;
	}

	private boolean checkOutOfBounds(int direction, int row, int col) {
		
		if (direction == LEFT) {
			return col < 0;
		}
		else if (direction == RIGHT) {
			return col > COLS - 1;
		}
		else if (direction == UP) {
			return row < 0;
		}
		else if (direction == DOWN) {
			return row > ROWS - 1;
		}
		return false;
	}

	private void move(int row, int col, int horizontalDirection, int verticalDirection, int direction) {
		Tile current = board[row][col];
		if(current==null) { return;}
		boolean move = true;
//		if(vvv==77){ROD=ROW; COD=COL; vvv=66;}
//		if(row==ROD&&col==COD&!g) { return; }
		ROW = row;
		COL = col;
		while (move) {
			COL += horizontalDirection;
			ROW += verticalDirection;
			if (checkOutOfBounds(direction, ROW, COL)) {
				if(direction==1||direction==0) { COL -= horizontalDirection; ROW -= verticalDirection; break;}
				else { COL -= horizontalDirection; ROW -= verticalDirection; b=14; break; }
			}
			if (board[ROW][COL] == null) {
				board[ROW][COL] = current;
				board[ROW - verticalDirection][COL - horizontalDirection] = null;
				board[ROW][COL].setSlideTo(new Point(ROW, COL));
				move = false;
			}
			else if (board[ROW][COL].getValue() == current.getValue() && board[ROW][COL].canCombine()&ju||
					(Math.abs(numFib(board[ROW][COL].getValue())-numFib(current.getValue()))==1&&!ju
					|| (numFib(board[ROW][COL].getValue())==1 & numFib(current.getValue())==1))&& board[ROW][COL].canCombine()&&!ju) {
				board[ROW][COL].setCanCombine(false);
				if(ju) {
					board[ROW][COL].setValue(board[ROW][COL].getValue() * 2);}
					else {board[ROW][COL].setValue(board[ROW][COL].getValue()+current.getValue());}
				board[ROW - verticalDirection][COL - horizontalDirection] = null;
				board[ROW][COL].setSlideTo(new Point(ROW, COL));
				board[ROW][COL].setCombineAnimation(true);
				scores.setCurrentScore(scores.getCurrentScore() + board[ROW][COL].getValue());
				move = false;
			}
			else { if(direction==1||direction==0) {COL -= horizontalDirection;
			ROW -= verticalDirection; move = false;}
			else { b=14; move = false;
			COL -= horizontalDirection;
			ROW -= verticalDirection; 
			if(ROW==0) {System.out.println(555555); setDead(true); }}}
		}
		}

	private boolean checkDead() {
		for (int row = 0; row < 1; row++) {
			for (int col = 0; col < COLS; col++) {
				if (board[row][col] == null) return false;
				boolean canCombine = checkSurroundingTiles(row, col, board[row][col]);
				if (canCombine) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkWon() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if(board[row][col] == null) continue;
				if(board[row][col].getValue() >= 2048) return true;
			}
		}
		return false;
	}

	private boolean checkSurroundingTiles(int row, int col, Tile tile) {
		if (row > 0) {
			Tile check = board[row - 1][col];
			if (check == null) return true;
			if(ju) {if (tile.getValue() == check.getValue()) return true;}
			else {if (Math.abs(numFib(tile.getValue())-numFib(check.getValue()))==1
					|| (tile.getValue()==1 & check.getValue()==1)) return true;}
		}
		if (row < ROWS - 1) {
			Tile check = board[row + 1][col];
			if (check == null) return true;
			if(ju) {if (tile.getValue() == check.getValue()) return true;}
			else {if (Math.abs(numFib(tile.getValue())-numFib(check.getValue()))==1
					|| (tile.getValue()==1 & check.getValue()==1)) return true;}
		}
		if (col > 0) {
			Tile check = board[row][col - 1];
			if (check == null) return true;
			if(ju) {if (tile.getValue() == check.getValue()) return true;}
			else {if (Math.abs(numFib(tile.getValue())-numFib(check.getValue()))==1
					|| (tile.getValue()==1 & check.getValue()==1)) return true;}
		}
		if (col < COLS - 1) {
			Tile check = board[row][col + 1];
			if (check == null) return true;
			if(ju) {if (tile.getValue() == check.getValue()) return true;}
			else {if (Math.abs(numFib(tile.getValue())-numFib(check.getValue()))==1
					|| (tile.getValue()==1 & check.getValue()==1)) return true;}
		}
		return false;
	}

	private void spawnRandom() {
		Random random = new Random();
		boolean notValid = true;
		while (notValid) {
            ROW=0;
			int location = random.nextInt(4);
			COL = location % 4;
			int value;
			Tile current = board[ROW][COL];
			if (current == null) {
				if(ju) value = random.nextInt(10) < 9 ? 2 : 4;
				else value = random.nextInt(10) < 9 ? 1 : 2;
				Tile tile = new Tile(value, getTileX(COL), getTileY(ROW));
				board[ROW][COL] = tile;
				notValid = false;
			}
		}
	}
	
	private void checkKeys() {
      
		if (!Keys.pressed[KeyEvent.VK_LEFT] && Keys.prev[KeyEvent.VK_LEFT]&&g) {
			fami(-1, 0, 0);
		}
		if (!Keys.pressed[KeyEvent.VK_RIGHT] && Keys.prev[KeyEvent.VK_RIGHT]&g) {
			fami(1, 0, 1); 
		}
		if (!Keys.pressed[KeyEvent.VK_DOWN] && Keys.prev[KeyEvent.VK_DOWN]&g) {
			fami(0, 1, 3); fami(0, 1, 3); 
		}
		if(gt==100) {fami(0, 1, 3);
		
		}
	}
		
		
		
	private void fami(int a, int d, int c) {
			if(b==0) {
//				vvv=77;
//				g=false;
//				for (int row = 1; row < ROWS-1; row++) {
//					for (int col = 0; col < COLS; col++) {
//						move(row, col, a, d, c); }}
//				  ROW=ROD; COL=COD;
//				  g=true;
//				  b=0;
				  move(ROW, COL, a, d, c); }
			
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					Tile current = board[row][col];
					if (current == null) continue;
					current.setCanCombine(true);
				}
			}

			if (b==14) {
				b=0;
				spawnRandom();
				
			}
			if (!hasStarted) hasStarted = !dead;
		}
	
	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public Tile[][] getBoard() {
		return board;
	}
	
	public void setBoard(Tile[][] board) {
		this.board = board;
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

	public boolean isWon() {
		return won;
	}

	public void setWon(boolean won) {
		this.won = won;
	}
	
	public ScoreManager getScores(){
		return scores;
	}
	
	public boolean getju(){
		return ju;
	}
	
	public void setju(boolean ju){
		this.ju=ju;
	}
    
	public int numFib(int value) {
	      switch (value) {
	        case 1:     return 1;
	        case 2:     return 2;
	        case 3:     return 3;
	        case 5:     return 4;
	        case 8:     return 5;
	        case 13:    return 6;
	        case 21:    return 7;
	        case 34:    return 8;
	        case 55:    return 9;
	        case 89:    return 10;
	        case 144:   return 11;
	        case 233:   return 12;
	        case 377:   return 13;
	        case 610:   return 14;
	        case 987:   return 15;
	        case 1597:  return 16;
	        case 2584:  return 17;
}
		return value;}
}
