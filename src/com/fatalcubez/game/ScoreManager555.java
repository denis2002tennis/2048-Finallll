package com.fatalcubez.game;

import java.io.*;

public class ScoreManager555 {

	private int currentScore;
	private int currentTopScore;
	private long time;
	private long startingTime;
	private long bestTime;
	private int[] board = new int[25];

	private String filePath;
	private String temp;
	private GameBoard555 gBoard;
	private boolean newGame;

	public ScoreManager555(GameBoard555 gBoard) {
		try {
			filePath = new File("").getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		temp = "555.xxx";

		this.gBoard = gBoard;
	}

	public void reset() {
		File f = new File(filePath, temp);
		if (f.isFile()) {
			f.delete();
		}
		newGame = true;
		startingTime = 0;
		currentScore = 0;
		time = 0;
	}

	private void createFile() {
		FileWriter output;
		newGame = true;

		try {
			File f = new File(filePath, temp);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			writer.write("" + 0);
			writer.newLine();
			writer.write("" + 0);
			writer.newLine();
			writer.write("" + 0);
			writer.newLine();
			writer.write("" + 0);
			writer.newLine();
			for (int row = 0; row < GameBoard555.ROWS; row++) {
				for (int col = 0; col < GameBoard555.COLS; col++) {
					if(row == GameBoard555.ROWS - 1 && col == GameBoard555.COLS - 1){
						writer.write("" + 0);
					}
					else{
						writer.write(0 + "-");
					}
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveGame() {
		FileWriter output;
		if (newGame) newGame = false;

		try {
			File f = new File(filePath, temp);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			writer.write("" + currentScore);
			writer.newLine();
			writer.write("" + currentTopScore);
			writer.newLine();
			writer.write("" + time);
			writer.newLine();
			writer.write("" + bestTime);
			writer.newLine();
			for (int row = 0; row < GameBoard555.ROWS; row++) {
				for (int col = 0; col < GameBoard555.COLS; col++) {
					this.board[row * GameBoard555.COLS + col] = gBoard.getBoard()[row][col] != null ? gBoard.getBoard()[row][col].getValue() : 0;
					if (row == GameBoard555.ROWS - 1 && col == GameBoard555.COLS - 1)
						writer.write("" + board[row * GameBoard555.COLS + col]);
					else writer.write(board[row * GameBoard555.COLS + col] + "-");
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadGame() {
		try {
			File f = new File(filePath, temp);

			if (!f.isFile()) {
				createFile();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			currentScore = Integer.parseInt(reader.readLine());
			currentTopScore = Integer.parseInt(reader.readLine());
			time = Long.parseLong(reader.readLine());
			startingTime = time;
			bestTime = Long.parseLong(reader.readLine());

			String[] board = reader.readLine().split("-");
			for (int i = 0; i < board.length; i++) {
				this.board[i] = Integer.parseInt(board[i]);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	public int getCurrentTopScore() {
		return currentTopScore;
	}

	public void setCurrentTopScore(int currentTopScore) {
		this.currentTopScore = currentTopScore;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time + startingTime;
	}

	public long getBestTime() {
		return bestTime;
	}

	public void setBestTime(long bestTime) {
		this.bestTime = bestTime;
	}

	public boolean newGame() {
		return newGame;
	}

	public int[] getBoard() {
		return board;
	}
}