package com.fatalcubez.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import com.fatalcubez.game.DrawUtils;
import com.fatalcubez.game.Game;

public class MainMenuPanel extends GuiPanel {

	private Font titleFont = Game.main.deriveFont(100f);
	private Font creatorFont = Game.main.deriveFont(24f);
	private String title = "2048";
	private String creator = "Безрукий А. Челомбітько Д. Колесніков А.";
	private int buttonWidth = 220;
	
	public MainMenuPanel() {
		super();
		GuiButton playButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 275, buttonWidth, 60);
		playButton.addActionListener(e -> GuiScreen.getInstance().setCurrentPanel("Normal"));
		playButton.setText("4x4");
		add(playButton);
		
		GuiButton playButton5x5 = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 195, buttonWidth, 60);
		playButton5x5.addActionListener(e -> GuiScreen.getInstance().setCurrentPanel("555"));
		playButton5x5.setText("5x5");
		add(playButton5x5);
		
		GuiButton playButton3x3 = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 355, buttonWidth, 60);
		playButton3x3.addActionListener(e -> GuiScreen.getInstance().setCurrentPanel("333"));
		playButton3x3.setText("3x3");
		add(playButton3x3);
				
		GuiButton quitButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 520, buttonWidth, 60);
		quitButton.addActionListener(e -> GuiScreen.getInstance().setCurrentPanel("Quit"));
		quitButton.setText("Quit");
		add(quitButton);
		
		GuiButton fib = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2+125, 440, buttonWidth-45, 55);
		fib.addActionListener(e -> GuiScreen.getInstance().setCurrentPanel("Fibonacci"));
		fib.setText("Fibonacci");
		add(fib);
		
		GuiButton tetris = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2-65, 440, buttonWidth-45, 55);
		tetris.addActionListener(e -> GuiScreen.getInstance().setCurrentPanel("Tetris-2048"));
		tetris.setText("2048-Tetris");
		add(tetris);

	}

	@Override
	public void render(Graphics2D g){
		super.render(g);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g.setFont(titleFont);
		g.setColor(Color.black);
		g.drawString(title, Game.WIDTH / 2 - DrawUtils.getMessageWidth(title, titleFont, g) / 2, 150);
		g.setFont(creatorFont);
		g.drawString(creator, 20, Game.HEIGHT - 10);
	}
}
