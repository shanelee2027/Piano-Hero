package main;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.Timer;

import piano.PianoDisplay;
import piano.PianoInput;
import piano.PianoVariables;
import song.SongDisplay;
import song.SongInput;
import song.SongVariables;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.*;

public class MainWindow extends JFrame implements ActionListener {

	MainMenuDisplay mmd;
	MainMenuInput mmi;
	MainMenuVariables mmv = new MainMenuVariables();

	public PianoDisplay pd;
	public PianoInput pi;
	public PianoVariables pv = new PianoVariables();
	
	public SongDisplay sd;
	public SongInput si;
	public SongVariables sv = new SongVariables();

	public Constants c;
	public Variables v;
	Timer t;
	public String currentScreen;

	public MainWindow(Constants c, Variables v) {
		super("Piano Game"); // name of JFrame

		this.c = c;
		this.v = v;
		mmd = new MainMenuDisplay(c, v, mmv);
		mmi = new MainMenuInput(this, mmv);

		currentScreen = "Main Menu";

		// Initial Setup
		this.setLayout(new BorderLayout());
		this.add(mmd, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(c.WIDTH, c.HEIGHT));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		// this fixes tab keys not working
		this.setFocusTraversalKeysEnabled(false);
		this.getRootPane().setFocusTraversalKeysEnabled(false);
		
		startTimer();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// TODO Auto-generated method stub
		if (e.getActionCommand().equals("timerFired")) // timer has fired
		{
			v.frame++;
			Rectangle bounds;
			int height;
			int width;
			switch (currentScreen) {
			case "Main Menu":
				mmd.repaint();
				break;
			case "Piano":
				bounds = this.getBounds();
				width = bounds.width;
				height = bounds.height;
				if(width != v.sWidth || height != v.sHeight) {
					v.sWidth = width;
					v.sHeight = height;
					pd.setPianoKeys();
				}
				v.sWidth = width;
				v.sHeight = height;
				if(!pv.paused) pv.tick += (int) (c.DELAY / pv.mspt);
				
				pd.repaint();
				break;
			case "Song Selection":
				bounds = this.getBounds();
				width = bounds.width;
				height = bounds.height;
				if(width != v.sWidth || height != v.sHeight) {
					v.sWidth = width;
					v.sHeight = height;
					sd.updateDimensions();
				}
				v.sWidth = width;
				v.sHeight = height;
				
				sd.repaint();
				break;
			}

			// if (!mmd.paused) {
			// // System.out.println(v.frame);
			// mmd.repaint(); // calls paintComponent to redraw everything
			// }
		}
	}
	
	public void reset() {
//		this.getContentPane().removeAll();
//		this.getContentPane().invalidate();
//		this.getContentPane().validate();
//		this.setVisible(true);
//		this.pack();
//		
//		currentScreen = "Main Menu";
//		
//		mmd = new MainMenuDisplay(c, v, mmv);
//		this.add(mmd, BorderLayout.CENTER);
		this.getContentPane().removeAll();
		this.setRootPane(new JRootPane());
		mmv = new MainMenuVariables();
		pv = new PianoVariables();
		sv = new SongVariables();
		mmd = new MainMenuDisplay(c, v, mmv);
		mmi = new MainMenuInput(this, mmv);
		
		currentScreen = "Main Menu";
		
		this.add(mmd);
		this.pack();
	}

	public void startTimer() {
		t = new Timer(c.DELAY, this);
		t.setActionCommand("timerFired");
		t.start();
	}

}
