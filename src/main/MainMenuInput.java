package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import piano.PianoDisplay;
import piano.PianoInput;
import song.SongDisplay;
import song.SongInput;

public class MainMenuInput {

	MainWindow mw;
	MainMenuVariables mmv;

	JButton gameModeSelect = new JButton();

	final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	final String MOVE_UP = "move up";
	final String MOVE_DOWN = "move down";
	final String ENTER = "enter";

	public MainMenuInput(MainWindow mw, MainMenuVariables mmv) {
		this.mw = mw;
		this.mmv = mmv;
		
		mw.add(gameModeSelect);
		gameModeSelect.addActionListener(mw);

		gameModeSelect.getInputMap(IFW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		gameModeSelect.getInputMap(IFW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
		gameModeSelect.getInputMap(IFW).put(KeyStroke.getKeyStroke("ENTER"), ENTER);

		gameModeSelect.getActionMap().put(MOVE_UP, new mainMenuUpAction());
		gameModeSelect.getActionMap().put(MOVE_DOWN, new mainMenuDownAction());
		gameModeSelect.getActionMap().put(ENTER, new mainMenuEnter());
	}

	public class mainMenuUpAction extends AbstractAction {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			mmv.gameModeSelected--;
			if (mmv.gameModeSelected < 0)
				mmv.gameModeSelected += mmv.numberOfGameModes;
		}
		
	}

	public class mainMenuDownAction extends AbstractAction {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			mmv.gameModeSelected++;
			mmv.gameModeSelected %= mmv.numberOfGameModes;
		}
		
	}
	
	public class mainMenuEnter extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			switch (mmv.gameModeSelected) {
			case 0:
				mw.currentScreen = "Piano";
				mw.getContentPane().removeAll();
				mw.pd = new PianoDisplay(mw, mw.c, mw.v, mw.pv, mw.sv);
				mw.pi = new PianoInput(mw, mw.c, mw.v, mw.pv, mw.sv);
				mw.add(mw.pd);
				mw.pack();
				break;
			case 1:
				mw.currentScreen = "Song Selection";
				mw.getContentPane().removeAll();
				mw.sd = new SongDisplay(mw, mw.c, mw.v, mw.sv);
				mw.si = new SongInput(mw, mw.c, mw.v, mw.sv, mw.pv);
				mw.add(mw.sd);
				mw.pack();
				break;
			}
			
		}
		
	}
}
