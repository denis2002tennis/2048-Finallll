package com.fatalcubez.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.sound.sampled.Clip;

public class ScateBoard implements Fing {

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;

	public static final int ROWS = 4;
	public static final int COLS = 4;

	private final int startingTiles = 2;
	private Tile[][] board;
	private boolean dead;
	private boolean won;
	private BufferedImage gameBoard;
	private int x;
	private int y;

	private static int SPACING = 10;
	public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
	public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;

	private long elapsedMS;
	private long startTime;
	private boolean hasStarted;

	private ScoreManager scores;
	private AudioHandler audio;
	private int saveCount = 0;

	public ScateBoard(int x, int y) {
		this.x = x;
		this.y = y;
		board = new Tile[ROWS][COLS];
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		createBoardImage();
        
		scores = new ScoreManager(this);
		scores.loadGame();
		if(scores.newGame()){
			start();
			scores.saveGame();
		}
		else{
			for(int i = 0; i < scores.getBoard().length; i++){
				if(scores.getBoard()[i] == 0) continue;
				spawn(i / ROWS, i % COLS, scores.getBoard()[i]);
			}
			// not calling setDead because we don't want to save anything
			dead = checkDead();
			// not coalling setWon because we don't want to save the time
			won = checkWon();
		}
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
			
		}
	}

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
		saveCount++;
		Tile.imm=true;

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
		
		if (scores.getCurrentScore() > scores.getCurrentTopScore()) {
			scores.setCurrentTopScore(scores.getCurrentScore());
		}

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null) continue;
				current.update();
				resetPosition(current, row, col);
				if (current.getValue() == 2584) {
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

		if (Math.abs(distX) < Tile.SLIDE_SPEED) {
			tile.setX(tile.getX() - distX);
		}

		if (Math.abs(distY) < Tile.SLIDE_SPEED) {
			tile.setY(tile.getY() - distY);
		}

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

	private boolean move(int row, int col, int horizontalDirection, int verticalDirection, int direction) {
		boolean canMove = false;
		Tile current = board[row][col];
		if (current == null) return false;
		boolean move = true;
		int newCol = col;
		int newRow = row;
		while (move) {
			newCol += horizontalDirection;
			newRow += verticalDirection;
			if (checkOutOfBounds(direction, newRow, newCol)) break;
			if (board[newRow][newCol] == null) {
				board[newRow][newCol] = current;
				canMove = true;
				board[newRow - verticalDirection][newCol - horizontalDirection] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
			}
			else if ((Math.abs(numFib(board[newRow][newCol].getValue())-numFib(current.getValue()))==1
				|| (numFib(board[newRow][newCol].getValue())==1 & numFib(current.getValue())==1))&& board[newRow][newCol].canCombine()) {
				board[newRow][newCol].setCanCombine(false);
				board[newRow][newCol].setValue(board[newRow][newCol].getValue()+current.getValue());
				canMove = true;
				board[newRow - verticalDirection][newCol - horizontalDirection] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
				board[newRow][newCol].setCombineAnimation(true);
				scores.setCurrentScore(scores.getCurrentScore() + board[newRow][newCol].getValue());
			}
			else {
				move = false;
			}
		}
		return canMove;
	}

	public void moveTiles(int direction) {
		boolean canMove = false;
		int horizontalDirection = 0;
		int verticalDirection = 0;

		if (direction == LEFT) {
			horizontalDirection = -1;
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		}
		else if (direction == RIGHT) {
			horizontalDirection = 1;
			for (int row = 0; row < ROWS; row++) {
				for (int col = COLS - 1; col >= 0; col--) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		}
		else if (direction == UP) {
			verticalDirection = -1;
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		}
		else if (direction == DOWN) {
			verticalDirection = 1;
			for (int row = ROWS - 1; row >= 0; row--) {
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		}
		else {
			System.out.println(direction + " is not a valid direction.");
		}

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null) continue;
				current.setCanCombine(true);
			}
		}

		if (canMove) {
			//audio.play("click", 0);
			spawnRandom();
			setDead(checkDead());
		}
	}

	// MODIFIED
	private boolean checkDead() {
		for (int row = 0; row < ROWS; row++) {
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
				if(board[row][col].getValue() >= 2584) return true;
			}
		}
		return false;
	}

	private boolean checkSurroundingTiles(int row, int col, Tile tile) {
		if (row > 0) {
			Tile check = board[row - 1][col];
			if (check == null) return true;
			if (Math.abs(numFib(tile.getValue())-numFib(check.getValue()))==1
					|| (tile.getValue()==1 & check.getValue()==1)) return true;
		}
		if (row < ROWS - 1) {
			Tile check = board[row + 1][col];
			if (check == null) return true;
			if (Math.abs(numFib(tile.getValue())-numFib(check.getValue()))==1
					|| (tile.getValue()==1&check.getValue()==1)) return true;
		}
		if (col > 0) {
			Tile check = board[row][col - 1];
			if (check == null) return true;
			if (Math.abs(numFib(tile.getValue())-numFib(check.getValue()))==1
					|| (tile.getValue()==1&check.getValue()==1)) return true;
		}
		if (col < COLS - 1) {
			Tile check = board[row][col + 1];
			if (check == null) return true;
			if (Math.abs(numFib(tile.getValue())-numFib(check.getValue()))==1
					|| (tile.getValue()==1&check.getValue()==1)) return true;
		}
		return false;
	}

	private void spawnRandom() {
		Random random = new Random();
		boolean notValid = true;

		while (notValid) {
			int location = random.nextInt(16);
			int row = location / ROWS;
			int col = location % COLS;
			Tile current = board[row][col];
			if (current == null) {
				int value = random.nextInt(10) < 9 ? 1 : 2;
				Tile tile = new Tile(value, getTileX(col), getTileY(row));
				board[row][col] = tile;
				notValid = false;
			}
		}
	}
                                                      
	private void checkKeys() {
		if (!Keys.pressed[KeyEvent.VK_LEFT] && Keys.prev[KeyEvent.VK_LEFT]) {
			moveTiles(LEFT);
			if (!hasStarted) hasStarted = !dead;
		}
		if (!Keys.pressed[KeyEvent.VK_RIGHT] && Keys.prev[KeyEvent.VK_RIGHT]) {
			moveTiles(RIGHT);
			if (!hasStarted) hasStarted = !dead;
		}
		if (!Keys.pressed[KeyEvent.VK_UP] && Keys.prev[KeyEvent.VK_UP]) {
			moveTiles(UP);
			if (!hasStarted) hasStarted = !dead;
		}
		if (!Keys.pressed[KeyEvent.VK_DOWN] && Keys.prev[KeyEvent.VK_DOWN]) {
			moveTiles(DOWN);
			if (!hasStarted) hasStarted = !dead;
		}
	}

	public int getHighestTileValue(){
		int value = 1;
		for(int row = 0; row < ROWS; row++){
			for(int col = 0; col < COLS; col++){
				if(board[row][col] == null) continue;
				if(board[row][col].getValue() > value) value = board[row][col].getValue();
			}
		}
		return value;
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

	@Override
	public void setju(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getju() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void fill() {
		// TODO Auto-generated method stub
		
	}
	
}

