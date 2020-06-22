package com.fatalcubez.game;

import java.awt.Graphics2D;

public interface Fing {
	
	public Tile[][] getBoard();

	public ScoreManager getScores();

	public void update();

	public boolean isDead();

	public void render(Graphics2D g2d);

	public void reset();

	public void setju(boolean b);

	public boolean getju();

	public void fill();
}

