package song;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import main.Constants;
import main.MainWindow;
import main.Variables;

public class SongDisplay extends JPanel {
	Constants c;
	Variables v;
	SongVariables sv;

	MainWindow mw;
	Font font = new Font("Comic Sans", Font.BOLD, 20);
	
	int bpmX;
	int bpmY;
	int[] songXs;
	int[] songYs;
	
	Color green = new Color(54, 247, 0);
	
	public SongDisplay(MainWindow mw, Constants c, Variables v, SongVariables sv) {
		this.mw = mw;
		this.c = c;
		this.v = v;
		this.sv = sv;
		
		songXs = new int[sv.numberOfSongs];
		songYs = new int[sv.numberOfSongs];
		
		updateDimensions();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setFont(font);
		g2.setColor(Color.BLACK);
//		g2.drawString("BPM: " + sv.bpm, bpmX, bpmY);
		if(sv.numberOfSongs < 16) {
			for(int i = 0; i < sv.numberOfSongs; i++) {
				if(i == sv.songSelected) g2.setColor(green);
				g2.drawString(sv.songNames.get(i), songXs[i], songYs[i]);
				g2.setColor(Color.BLACK);
			}
		} else {
			for(int i = sv.firstSongShown; i < sv.firstSongShown + 16; i++) {
				if(i == sv.songSelected) g2.setColor(green);
				g2.drawString(sv.songNames.get(i), songXs[i - sv.firstSongShown], songYs[i - sv.firstSongShown]);
				g2.setColor(Color.BLACK);
			}
		}
	}
	
	public void updateDimensions() {
		bpmX = (int) (v.sWidth * 0.1);
		bpmY = (int) (v.sHeight * 0.05);
		for(int i = 0; i < Math.min(16, sv.numberOfSongs); i++) {
			songXs[i] = (int) (v.sWidth * 0.1);
			songYs[i] = (int) (v.sHeight * 0.05 * (i + 3));
		}
		
		font = new Font("Comic Sans", Font.BOLD, (int) (v.sWidth * 0.02));
	}
}
