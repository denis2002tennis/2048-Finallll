package com.fatalcubez.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.fatalcubez.game.AudioHandler;
import com.fatalcubez.game.DrawUtils;
import com.fatalcubez.game.Game;

public class GuiButton {

	private State currentState = State.RELEASED;
	private Rectangle clickBox;
	private ArrayList<ActionListener> actionListeners;
	private String text = "";
	
	private Color main;
	private Color hover;
	private Color pressed;
	private Font font = Game.main.deriveFont(22f);
	private AudioHandler audio;
	
	public GuiButton(int x, int y, int width, int height){
		clickBox = new Rectangle(x, y, width, height);
		actionListeners = new ArrayList<ActionListener>();
		main = new Color(173, 177, 179);
		hover = new Color(150, 156, 158);
		pressed = new Color(111, 116, 117);
		
		audio = AudioHandler.getInstance();
		audio.load("select.wav", "select");
	}
	
	public void update(){
	}
	
	public void render(Graphics2D g){
		if(currentState == State.RELEASED){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			g.setColor(main);
			g.fill(clickBox);
		}
		else if(currentState == State.PRESSED){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			g.setColor(pressed);
			g.fill(clickBox);
		}
		else{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			g.setColor(hover);
			g.fill(clickBox);
		}
		g.setColor(Color.white);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g.drawString(text, clickBox.x + clickBox.width / 2  - DrawUtils.getMessageWidth(text, font, g) / 2, clickBox.y + clickBox.height / 2  + DrawUtils.getMessageHeight(text, font, g) / 2);
	}
	
	public void addActionListener(ActionListener listener){
		actionListeners.add(listener);
	}
	
	public void mousePressed(MouseEvent e) {
		if(clickBox.contains(e.getPoint())){
			currentState = State.PRESSED;
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(clickBox.contains(e.getPoint())){
			for(ActionListener al : actionListeners){
				al.actionPerformed(null);
			}
			audio.play("select", 0);
		}
		currentState = State.RELEASED;
	}

	public void mouseDragged(MouseEvent e) {
		if(clickBox.contains(e.getPoint())){
			currentState = State.PRESSED;
		}
		else{
			currentState = State.RELEASED;
		}
	}

	public void mouseMoved(MouseEvent e) {
		if(clickBox.contains(e.getPoint())){
			currentState = State.HOVER;
		}
		else{
			currentState = State.RELEASED;
		}
	}
	
	public int getX(){
		return clickBox.x;
	}
	
	public int getY(){
		return clickBox.y;
	}
	
	public int getWidth(){
		return clickBox.width;
	}
	
	public int getHeight(){
		return clickBox.height;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	private enum State{
		HOVER, RELEASED, PRESSED
	}
}
