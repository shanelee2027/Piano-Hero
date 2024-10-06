package piano;

import javax.swing.JPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import main.Constants;
import main.MainWindow;
import main.Variables;
import song.MIDINote;
import song.SongVariables;
import song.TempoChange;

public class PianoDisplay extends JPanel {
	Constants c;
	Variables v;
	PianoVariables pv;
	SongVariables sv;

	MainWindow mw;

	Font whiteFont = new Font("Comic Sans", Font.BOLD, 20);
	Font blackFont = new Font("Comic Sans", Font.BOLD, 20);
	Font instrumentFont = new Font("Comic Sans", Font.BOLD, 20);
	Font songWhiteFont = new Font("Comic Sans", Font.BOLD, 20);
	Font songBlackFont = new Font("Comic Sans", Font.BOLD, 20);
	
	Color whiteGreen = new Color(54, 247, 0);
	Color blackGreen = new Color(32, 145, 0);
	Color whiteBlue = new Color(3, 173, 252);
	Color blackBlue = new Color(5, 125, 181);
	Color grayEscapeScreen = new Color(135, 135, 135);
	Color[] trackWhiteColors = {
			new Color(3, 173, 252) // blue
			,new Color(237, 62, 62) // red
			,new Color(209, 58, 240) // purple
			,new Color(255, 233, 38) // yellow
	};
	Color[] trackBlackColors = {
			new Color(5, 125, 181) // blue
			,new Color(173, 42, 42) // red
			,new Color(142, 41, 163) // purple
			,new Color(201, 184, 30) // yellow
	};
	

	int[] whiteKeyWidths = new int[21];
	int[] whiteKeyHeights = new int[21];
	int[] whiteKeyXs = new int[21];
	int[] whiteKeyYs = new int[21];
	int[] whiteTextXs = new int[21];
	int[] whiteTextYs = new int[21];
	int[] blackKeyWidths = new int[15];
	int[] blackKeyHeights = new int[15];
	int[] blackKeyXs = new int[15];
	int[] blackKeyYs = new int[15];
	int[] blackTextXs = new int[15];
	int[] blackTextYs = new int[15];
	double[] blackKeyDistances = { 0.61, 0.8, 0.55, 0.7, 0.85 };

	

	public PianoDisplay(MainWindow mw, Constants c, Variables v, PianoVariables pv, SongVariables sv) {
		this.c = c;
		this.v = v;
		this.pv = pv;
		this.mw = mw;
		this.sv = sv;

		setPreferredSize(new Dimension(v.sWidth, v.sHeight)); // set size of display

		setPianoKeys();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(pv.escapeScreen) {
			g2.setColor(grayEscapeScreen);
			g2.fillRect(0, 0, v.sWidth, v.sHeight);
			g2.setFont(whiteFont);
			g2.setColor(Color.BLACK);
			g2.drawString("Are you sure you want to exit?", (int) (v.sWidth * 0.2), (int) (v.sHeight * 0.5));
			return;
		}
		
		// update bpm
		for(int i = 0; i < sv.tempoChanges.size(); i++) {
			TempoChange tc = sv.tempoChanges.get(i);
			if(tc.tick > pv.tick) break;
			sv.baseBPM = tc.bpm;
			sv.bpm = (int) (sv.baseBPM * sv.tempoMultiplier * sv.globalTempoMultiplier / 100.0);
			pv.mspt = 60000.0 / ((sv.bpm * 1.0) * (sv.ppq));
			sv.tempoChanges.remove(i);
			i--;
		}
		
		// white keys in song (but plays black notes)
		g2.setFont(songWhiteFont);
		for(int i = 0; i < sv.songNotes.size(); i++) {
			MIDINote note = sv.songNotes.get(i);
			if(note.onTick > pv.tick + pv.heightInTicks) break;
			
			int trackSetting = pv.trackSettings[note.track]; // DP, D, P, N
			boolean played = trackSetting == 0 || trackSetting == 2;
			boolean displayed = trackSetting == 0 || trackSetting == 1;
			
			if(note.offTick < pv.tick) {
				sv.songNotes.remove(i);
				i--;
				// turn off note
				pv.songNotesCurrentlyPlaying.remove(note);
				pv.songNotesCurrentlyDisplayed.remove(note);
				if(!pv.playedNotesOn.contains(note.key)) {
					pv.midiChannel.noteOff(note.key);
				}
				continue;
			}
			
			if(note.onTick < pv.tick && !pv.songNotesCurrentlyPlaying.contains(note) && !pv.paused) {
				pv.songNotesCurrentlyPlaying.add(note);
				if(!pv.playedNotesOn.contains(note.key)) {
					pv.songNotesCurrentlyDisplayed.add(note);
					if(played) pv.midiChannel.noteOn(note.key, (int) (pv.playedNoteVelocity * pv.songNoteVelocityMultiplier));
				}
			}
			
			// if track is not displayed
			if(!displayed) continue;
			// if note is not on the three octaves, dont draw it
			if(note.key < 48 || note.key >= 84) continue;
			
			char keyChar = pv.noteNumberToChar.get(note.key);
			if(pv.charToWhiteKeyNumber.keySet().contains(keyChar)) {
				int index = pv.charToWhiteKeyNumber.get(keyChar);
				int height = (int) ((note.offTick - note.onTick) * pv.pxpt);
				int y = whiteKeyYs[index] - (int) ((note.offTick - pv.tick) * pv.pxpt);
				
				g2.setColor(trackWhiteColors[(note.track - 1) % trackWhiteColors.length]);
				g2.fillRect(whiteKeyXs[index], y, whiteKeyWidths[index], height);
				g2.setColor(Color.BLACK);
				g2.drawRect(whiteKeyXs[index], y, whiteKeyWidths[index], height);
				g2.drawString(pv.whiteKeyPrint[index] + "", whiteTextXs[index], y + (int) (height * 0.7));
			}
		}
		
		// black keys in song
		g2.setFont(songWhiteFont);
		for(int i = 0; i < sv.songNotes.size(); i++) {
			MIDINote note = sv.songNotes.get(i);
			if(note.onTick > pv.tick + pv.heightInTicks) break;
			
			int trackSetting = pv.trackSettings[note.track]; // DP, D, P, N
			boolean displayed = trackSetting == 0 || trackSetting == 1;
			
			// if track is not displayed
			if(!displayed) continue;
			// if note is not on the three octaves
			if(note.key < 48 || note.key >= 84) continue;
			
			char keyChar = pv.noteNumberToChar.get(note.key);
			if(pv.charToBlackKeyNumber.keySet().contains(keyChar)) {
				int index = pv.charToBlackKeyNumber.get(keyChar);
				int height = (int) ((note.offTick - note.onTick) * pv.pxpt);
				int y = blackKeyYs[index] - (int) ((note.offTick - pv.tick) * pv.pxpt);
				
				g2.setColor(trackBlackColors[(note.track - 1) % trackBlackColors.length]);
				g2.fillRect(blackKeyXs[index], y, blackKeyWidths[index], height);
				g2.setColor(Color.BLACK);
				g2.drawRect(blackKeyXs[index], y, blackKeyWidths[index], height);
				g2.drawString(pv.blackKeyPrint[index] + "", blackTextXs[index], y + (int) (height * 0.7));
			}
		}
		
		// white key text in song
		g2.setColor(Color.BLACK);
		g2.setFont(songWhiteFont);
		for (int i = 0; i < 21; i++) {
			g2.drawString(pv.whiteKeyPrint[i] + "", whiteTextXs[i], whiteTextYs[i]);
		}
		// black key text in song
		g2.setFont(songBlackFont);
		g2.setColor(Color.WHITE);
		for (int i = 0; i < 15; i++) {
			g2.drawString(pv.blackKeyPrint[i] + "", blackTextXs[i], blackTextYs[i]);
		}
		
		// white keys
		g2.setColor(Color.WHITE);
		for (int i = 0; i < 21; i++) {
			g2.fillRect(whiteKeyXs[i], whiteKeyYs[i], whiteKeyWidths[i], whiteKeyHeights[i]);
		}
		// black keys
		g2.setColor(Color.BLACK);
		for (int i = 0; i < 15; i++) {
			g2.fillRect(blackKeyXs[i], blackKeyYs[i], blackKeyWidths[i], blackKeyHeights[i]);
		}
		
		// song played white keys
		g2.setColor(whiteBlue);
		for(MIDINote note : pv.songNotesCurrentlyDisplayed) {
			int trackSetting = pv.trackSettings[note.track]; // DP, D, P, N
			boolean displayed = trackSetting == 0;
			if(!displayed) continue;
			
			if(note.key < 48 || note.key >= 84) continue;
			char keyChar = pv.noteNumberToChar.get(note.key);
			if(pv.charToWhiteKeyNumber.keySet().contains(keyChar)) {
				g2.setColor(trackWhiteColors[(note.track - 1) % trackWhiteColors.length]);
				
				int i = pv.charToWhiteKeyNumber.get(keyChar);
				g2.fillRect(whiteKeyXs[i], whiteKeyYs[i], whiteKeyWidths[i], whiteKeyHeights[i]);
			}
		}
		
		// played white keys
		g2.setColor(whiteGreen);
		for(int noteNumber : pv.playedNotesOn) {
			char keyChar = pv.noteNumberToChar.get(noteNumber);
			if(pv.charToWhiteKeyNumber.keySet().contains(keyChar)) {
				int i = pv.charToWhiteKeyNumber.get(keyChar);
				g2.fillRect(whiteKeyXs[i], whiteKeyYs[i], whiteKeyWidths[i], whiteKeyHeights[i]);
			}
		}
		
		// white key borders
		g2.setColor(Color.BLACK);
		for (int i = 0; i < 21; i++) {
			g2.drawRect(whiteKeyXs[i], whiteKeyYs[i], whiteKeyWidths[i], whiteKeyHeights[i]);
		}
		// black keys
		for (int i = 0; i < 15; i++) {
			g2.fillRect(blackKeyXs[i], blackKeyYs[i], blackKeyWidths[i], blackKeyHeights[i]);
		}
		
		// song played black keys
		g2.setColor(blackBlue);
		for(MIDINote note : pv.songNotesCurrentlyDisplayed) {
			int trackSetting = pv.trackSettings[note.track]; // DP, D, P, N
			boolean displayed = trackSetting == 0;
			if(!displayed) continue;
			
			if(note.key < 48 || note.key >= 84) continue;
			char keyChar = pv.noteNumberToChar.get(note.key);
			if(pv.charToBlackKeyNumber.keySet().contains(keyChar)) {
				g2.setColor(trackBlackColors[(note.track - 1) % trackBlackColors.length]);
				
				int i = pv.charToBlackKeyNumber.get(keyChar);
				g2.fillRect(blackKeyXs[i], blackKeyYs[i], blackKeyWidths[i], blackKeyHeights[i]);
			}
		}
		
		// played black keys
		g2.setColor(blackGreen);
		for(int noteNumber : pv.playedNotesOn) {
			char keyChar = pv.noteNumberToChar.get(noteNumber);
			if(pv.charToBlackKeyNumber.keySet().contains(keyChar)) {
				int i = pv.charToBlackKeyNumber.get(keyChar);
				g2.fillRect(blackKeyXs[i], blackKeyYs[i], blackKeyWidths[i], blackKeyHeights[i]);
			}
		}
		
		// black key borders
		g2.setColor(Color.BLACK);
		for (int i = 0; i < 15; i++) {
			g2.drawRect(blackKeyXs[i], blackKeyYs[i], blackKeyWidths[i], blackKeyHeights[i]);
		}
		
		// white key text
		g2.setColor(Color.BLACK);
		g2.setFont(whiteFont);
		for (int i = 0; i < 21; i++) {
			g2.drawString(pv.whiteKeyPrint[i] + "", whiteTextXs[i], whiteTextYs[i]);
		}
		// black key text
		g2.setFont(blackFont);
		g2.setColor(Color.WHITE);
		for (int i = 0; i < 15; i++) {
			g2.drawString(pv.blackKeyPrint[i] + "", blackTextXs[i], blackTextYs[i]);
		}
		
		// instrument text
		g2.setColor(Color.BLACK);
		if(pv.tabHeld) {
			g2.setFont(instrumentFont);
			for(int i = pv.firstInstrumentDisplayed; i < pv.firstInstrumentDisplayed + pv.numberOfInstrumentsDisplayed; i++) {
				if(i == pv.instrumentSelected) g2.setColor(whiteGreen);
				g2.drawString((i + 1) + ": " + pv.instruments[i].getName(), (int) (v.sWidth * 0.8), (int) (v.sHeight * 0.0325 * ((i - pv.firstInstrumentDisplayed) + 2)));
				g2.setColor(Color.BLACK);
			}
		}
		
		// track text and colored Boxes
		if(pv.shiftHeld) {
			g2.setColor(Color.BLACK);
			g2.setFont(instrumentFont);
			for(int i = 1; i <= sv.numberOfTracks; i++) {
				if(i == pv.trackSelected) g2.setColor(whiteGreen);
				g2.drawString("< " + pv.trackSettingsStrings[pv.trackSettings[i]] + " >", (int) (v.sWidth * 0.05), (int) (v.sHeight * 0.0325 * (i + 3)));
				g2.setColor(Color.BLACK);
			}
			for(int i = 1; i <= sv.numberOfTracks; i++) {
				g2.setColor(trackWhiteColors[(i - 1) % trackWhiteColors.length]);
				g2.fillRect((int) (v.sWidth * 0.02), (int) (v.sHeight * 0.0325 * (i + 2.35)), (int) (v.sWidth * 0.015), (int) (v.sWidth * 0.015));
				g2.setColor(Color.BLACK);
				g2.drawRect((int) (v.sWidth * 0.02), (int) (v.sHeight * 0.0325 * (i + 2.35)), (int) (v.sWidth * 0.015), (int) (v.sWidth * 0.015));
			}
		}
		
		// track colored boxes
		
		// tempo and volume text in topleft
		g2.setColor(Color.GRAY);
		g2.fillRect(0, 0, 410, 50);
		g2.setColor(Color.WHITE);
		g2.setFont(instrumentFont);
		g2.drawString("Tempo: " + sv.tempoMultiplier + "%", 10, 30);
		g2.drawString("PVol: " + pv.playedNoteVelocityMultiplier, 190, 30);
		g2.drawString("SVol: " + pv.songNoteVelocityMultiplier, 310, 30);
	}

	public void setPianoKeys() {
		
		Dimension dimension = mw.getContentPane().getSize();
		v.sWidth = (int) dimension.getWidth();
		v.sHeight = (int) dimension.getHeight();
		
		pv.pxpt = (v.sWidth * pv.quarterNoteHeightProportion) / (sv.ppq * 1.0);
		
		whiteFont = new Font("Comic Sans", Font.BOLD, (int) (v.sWidth * 0.03));
		blackFont = new Font("Comic Sans", Font.BOLD, (int) (v.sWidth * 0.02));
		songWhiteFont = new Font("Comic Sans", Font.BOLD, (int) (v.sWidth * 0.025));
		songBlackFont = new Font("Comic Sans", Font.BOLD, (int) (v.sWidth * 0.02));
		instrumentFont = new Font("Comic Sans", Font.BOLD, (int) (v.sWidth * 0.015));

		// set sizes and coordinates of piano keys
		for (int i = 0; i < 21; i++) {
			whiteKeyWidths[i] = v.sWidth / 21;
			if (i < v.sWidth % 21)
				whiteKeyWidths[i]++;
			whiteKeyHeights[i] = (int) (v.sHeight / 5.0);
			if (i != 0)
				whiteKeyXs[i] = whiteKeyXs[i - 1] + whiteKeyWidths[i - 1];
			whiteKeyYs[i] = v.sHeight - whiteKeyHeights[i];

		}
		for (int i = 0; i < 15; i++) {
			blackKeyWidths[i] = (int) (whiteKeyWidths[0] * 0.65);
			blackKeyHeights[i] = (int) (whiteKeyHeights[0] * 0.65);
			blackKeyYs[i] = whiteKeyYs[0];
		}
		for (int i = 0; i < 3; i++) {
			blackKeyXs[5 * i] = (int) (whiteKeyXs[7 * i] + whiteKeyWidths[7 * i] * blackKeyDistances[0]);
			blackKeyXs[5 * i + 1] = (int) (whiteKeyXs[7 * i + 1] + whiteKeyWidths[7 * i + 1] * blackKeyDistances[1]);
			blackKeyXs[5 * i + 2] = (int) (whiteKeyXs[7 * i + 3] + whiteKeyWidths[7 * i + 3] * blackKeyDistances[2]);
			blackKeyXs[5 * i + 3] = (int) (whiteKeyXs[7 * i + 4] + whiteKeyWidths[7 * i + 4] * blackKeyDistances[3]);
			blackKeyXs[5 * i + 4] = (int) (whiteKeyXs[7 * i + 5] + whiteKeyWidths[7 * i + 5] * blackKeyDistances[4]);
		}
		for (int i = 0; i < 21; i++) {
			whiteTextXs[i] = whiteKeyXs[i] + (int) (whiteKeyWidths[i] * 0.3);
			whiteTextYs[i] = whiteKeyYs[i] + (int) (whiteKeyHeights[i] * 0.93);
		}
		for (int i = 0; i < 15; i++) {
			blackTextXs[i] = blackKeyXs[i] + (int) (blackKeyWidths[i] * 0.33);
			blackTextYs[i] = blackKeyYs[i] + (int) (blackKeyHeights[i] * 0.7);
		}
		
		pv.heightInTicks = (int) ((v.sHeight - whiteKeyHeights[0]) * 1.2 / pv.pxpt);
	}

}


// SET AN ON AND OFF TIME FOR EACH NOTE,
// SET A VARIABLE "PIXEL PER TICK"
