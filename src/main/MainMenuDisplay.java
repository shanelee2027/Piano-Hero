package main;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class MainMenuDisplay extends JPanel {

	Constants c;
	Variables v;
	MainMenuVariables mmv;
	UsefulMethods um = new UsefulMethods();

	String currentScreen;
	boolean paused;
	Color colorUnselectedText = new Color(162, 175, 205);
	Color colorSelectedText = new Color(81, 235, 112);
	Color colorGrayBackground = new Color(224, 224, 224);
	Font fontGameModes = new Font("Comic Sans", Font.BOLD, 100);
	
	public MainMenuDisplay(Constants c, Variables v, MainMenuVariables mmv) {
		this.c = c;
		this.v = v;
		this.mmv = mmv;

		setPreferredSize(new Dimension(v.sWidth, v.sHeight)); // set size of display
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		mainPaint(g, g2);
	}

	public void mainPaint(Graphics g, Graphics2D g2) {
		g2.setBackground(colorGrayBackground);
		
		g.setFont(fontGameModes);
		g.setColor(colorUnselectedText);
		
		for(int i = 0; i < mmv.numberOfGameModes; i++) {
			if(i == mmv.gameModeSelected) {
				g.setColor(colorSelectedText);
			}
			Rectangle rect = new Rectangle(v.sWidth/2, 300 + 150 * i, 0, 0);
			um.drawCenteredString(g, mmv.gameModeNames[i], rect, fontGameModes);
			g.setColor(colorUnselectedText);
		}
	}


	

}
