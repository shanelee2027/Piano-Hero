package song;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import main.Constants;
import main.MainWindow;
import main.Variables;
import main.MainMenuInput.mainMenuDownAction;
import main.MainMenuInput.mainMenuEnter;
import main.MainMenuInput.mainMenuUpAction;
import piano.PianoDisplay;
import piano.PianoInput;
import piano.PianoVariables;
import piano.PianoInput.enterAction;

public class SongInput {
	Constants c;
	Variables v;
	MainWindow mw;
	SongVariables sv;
	PianoVariables pv;

	JButton songSelect = new JButton();
	JButton bpmSelect = new JButton();

	final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	final String MOVE_UP = "move up";
	final String MOVE_DOWN = "move down";
	final String ENTER = "enter";
	final String MOVE_LEFT = "move left";
	final String MOVE_RIGHT = "move right";
	final String ESCAPE_PRESSED = "escape pressed";

	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final int TEMPO_CHANGE = 81;

	public SongInput(MainWindow mw, Constants c, Variables v, SongVariables sv, PianoVariables pv) {
		this.mw = mw;
		this.c = c;
		this.v = v;
		this.sv = sv;
		this.pv = pv;

		mw.add(songSelect);
		mw.add(bpmSelect);
		songSelect.addActionListener(mw);
		bpmSelect.addActionListener(mw);
		songSelect.getInputMap(IFW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		songSelect.getInputMap(IFW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
		songSelect.getInputMap(IFW).put(KeyStroke.getKeyStroke("ENTER"), ENTER);

		songSelect.getActionMap().put(MOVE_UP, new songSelectUpAction());
		songSelect.getActionMap().put(MOVE_DOWN, new songSelectDownAction());
		songSelect.getActionMap().put(ENTER, new enterAction());

		bpmSelect.getInputMap(IFW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
		bpmSelect.getInputMap(IFW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);

		bpmSelect.getActionMap().put(MOVE_LEFT, new bpmSelectLeftAction());
		bpmSelect.getActionMap().put(MOVE_RIGHT, new bpmSelectRightAction());
		mw.getRootPane().getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESCAPE_PRESSED);
		mw.getRootPane().getActionMap().put(ESCAPE_PRESSED, new escapeAction());
	}

	public class songSelectUpAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (sv.songSelected > 0)
				sv.songSelected--;
			else {
				sv.songSelected = sv.numberOfSongs - 1;
				sv.firstSongShown = sv.songSelected - 15;
			}
			if (sv.songSelected < sv.firstSongShown)
				sv.firstSongShown--;
//			sv.bpm = sv.songBPMs.get(sv.songSelected);
		}

	}

	public class songSelectDownAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (sv.songSelected < sv.numberOfSongs - 1)
				sv.songSelected++;
			else {
				sv.songSelected = 0;
				sv.firstSongShown = 0;
			}
			if (sv.songSelected - sv.firstSongShown >= 16)
				sv.firstSongShown++;
//			sv.bpm = sv.songBPMs.get(sv.songSelected);
		}

	}

	public class enterAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			try {
				Sequence sequence = MidiSystem.getSequence(sv.MIDIs.get(sv.songSelected));
				sv.ppq = sequence.getResolution();
				pv.mspt = 60000.0 / ((sv.bpm * 1.0) * (sv.ppq));
				
				int trackNumber = 0;
				sv.numberOfTracks = sequence.getTracks().length;
				for (Track track : sequence.getTracks()) {
					trackNumber++;
					HashMap<Integer, Long> notesAndOnTicks = new HashMap<Integer, Long>();
					for (int i = 0; i < track.size(); i++) {
						MidiEvent event = track.get(i);
						MidiMessage message = event.getMessage();
						if (message instanceof ShortMessage) {
							ShortMessage sm = (ShortMessage) message;
							int key = sm.getData1();
							int velocity = sm.getData2();
							if (sm.getCommand() == NOTE_ON && velocity != 0) {
								if(!notesAndOnTicks.containsKey(key)) {
									notesAndOnTicks.put(key, event.getTick());
								}
							} else if (sm.getCommand() == NOTE_OFF || velocity == 0) {
								if(notesAndOnTicks.containsKey(key)) {
									sv.songNotes.add(new MIDINote(notesAndOnTicks.get(key), event.getTick(), key, velocity, trackNumber));
									notesAndOnTicks.remove(key);
								}
							}
						} else if (message instanceof MetaMessage) {
							MetaMessage mm = (MetaMessage) message;
							if(mm.getType() == TEMPO_CHANGE) {
								sv.tempoChanges.add(new TempoChange(event.getTick(), getTempoInBPM(mm), trackNumber));
							}
						}
					}
					
				}
				
			} catch (Exception e) {

			}
			
			Collections.sort(sv.songNotes);
			Collections.sort(sv.tempoChanges);
			// if tempo is not specified
			if(sv.tempoChanges.size() == 0) {
				sv.baseBPM = 100;
			} else {
				sv.baseBPM = sv.tempoChanges.get(0).bpm;
			}
			sv.bpm = (int) (sv.baseBPM * sv.tempoMultiplier * sv.globalTempoMultiplier / 100.0);
			pv.mspt = 60000.0 / ((sv.bpm * 1.0) * (sv.ppq));
			int tickDelay = (int) (sv.initialDelay / pv.mspt);
			pv.trackSettings = new int[sv.numberOfTracks+1];
			
			pv.tick -= tickDelay;
			
			pv.playingSong = true;
			mw.currentScreen = "Piano";
			mw.getContentPane().removeAll();
			mw.pd = new PianoDisplay(mw, mw.c, mw.v, mw.pv, mw.sv);
			mw.pi = new PianoInput(mw, mw.c, mw.v, mw.pv, mw.sv);
			mw.add(mw.pd);
			mw.pack();
		}
	}

	public class bpmSelectLeftAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (sv.bpm > 40)
				sv.bpm -= 4;
		}

	}

	public class bpmSelectRightAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (sv.bpm < 400)
				sv.bpm += 4;
		}

	}

	public class escapeAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			mw.reset();
//			MainWindow mw2 = new MainWindow(c, v);
//			mw.dispose();
//			mw = mw2;
		}

	}

	public static int getTempoInBPM(MetaMessage mm) {
		byte[] data = mm.getData();
		if (mm.getType() != 81 || data.length != 3) {
			return -1;
		}
		int mspq = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
		int tempo = Math.round(60000001f / mspq);
		return tempo;
	}
}
