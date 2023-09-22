package piano;

import main.Constants;
import main.MainWindow;
import main.Variables;
import song.MIDINote;
import song.SongInput.bpmSelectLeftAction;
import song.SongInput.bpmSelectRightAction;
import song.SongVariables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.print.attribute.standard.PDLOverrideSupported;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class PianoInput {
	Constants c;
	Variables v;
	PianoVariables pv;
	MainWindow mw;
	SongVariables sv;

	JRootPane rootPane;

	Synthesizer synthesizer;
	Soundbank soundbank;
	MidiChannel midiChannel;
	Instrument[] instruments;

	final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	final int AFC = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
	String KEY_PRESSED = "key pressed";
	String KEY_RELEASED = "key released";
	String UP_PRESSED = "up pressed";
	String DOWN_PRESSED = "down pressed";
	String TAB_PRESSED = "tab pressed";
	String TAB_RELEASED = "tab released";
	String MOVE_LEFT = "move left";
	String MOVE_RIGHT = "move right";
	String SPACE_PRESSED = "space pressed";
	String MINUS_PRESSED = "minus pressed";
	String EQUAL_PRESSED = "equal pressed";
	String ESCAPE_PRESSED = "escape pressed";
	String ENTER_PRESSED = "enter pressed";
	String SHIFT_PRESSED = "shift pressed";
	String SHIFT_RELEASED = "shift released";

	public PianoInput(MainWindow mw, Constants c, Variables v, PianoVariables pv, SongVariables sv) {
		this.mw = mw;
		this.c = c;
		this.v = v;
		this.pv = pv;
		this.sv = sv;

		rootPane = mw.getRootPane();

//		mw.add(playNote);
//		playNote.addActionListener(mw);

		for (char key : pv.charToNoteNumber.keySet()) {
			rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke(key), KEY_PRESSED);
			rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + (key + "").toUpperCase()), KEY_RELEASED);
		}
		rootPane.getActionMap().put(KEY_PRESSED, new keyPressedAction());
		rootPane.getActionMap().put(KEY_RELEASED, new keyReleasedAction());

		try {
			synthesizer = MidiSystem.getSynthesizer();
			pv.synthesizer = synthesizer;

			// load different soundbank
			File file = new File(System.getProperty("user.dir") + "/src/piano/FluidR3 GM2-2.SF2");
			try {
				soundbank = MidiSystem.getSoundbank(file);
//				synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
				synthesizer.open();
				synthesizer.loadAllInstruments(soundbank);
			} catch (Exception e) {
				System.out.println("c");
			}

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		midiChannel = synthesizer.getChannels()[0];
		pv.midiChannel = midiChannel;

//		mw.add(switchInstrument);
//		switchInstrument.addActionListener(mw);

		instruments = soundbank.getInstruments();
		instruments = Arrays.copyOf(instruments, pv.numberOfInstruments);
		pv.instruments = instruments;
//		for(Instrument i : instruments) System.out.println(i.getPatch().getProgram());

		// switchInstrument.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
		// 0, false), TAB_PRESSED);
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), TAB_PRESSED);
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, true), TAB_RELEASED);
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke("UP"), UP_PRESSED);
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke("DOWN"), DOWN_PRESSED);
		rootPane.getActionMap().put(TAB_PRESSED, new tabPressedAction());
		rootPane.getActionMap().put(TAB_RELEASED, new tabReleasedAction());
		rootPane.getActionMap().put(UP_PRESSED, new upPressedAction());
		rootPane.getActionMap().put(DOWN_PRESSED, new downPressedAction());

//		mw.add(switchBPM);
//		switchBPM.addActionListener(mw);

		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);

		rootPane.getActionMap().put(MOVE_LEFT, new leftAction());
		rootPane.getActionMap().put(MOVE_RIGHT, new rightAction());

		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke("SPACE"), SPACE_PRESSED);

		rootPane.getActionMap().put(SPACE_PRESSED, new spacePressedAction());

//		mw.add(noteSizer);
//		noteSizer.addActionListener(mw);

		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke('-'), MINUS_PRESSED);
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke('='), EQUAL_PRESSED);

		rootPane.getActionMap().put(MINUS_PRESSED, new minusPressedAction());
		rootPane.getActionMap().put(EQUAL_PRESSED, new equalPressedAction());

//		mw.add(volumeSelector);
//		volumeSelector.addActionListener(mw);

		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke('['), "[");
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke(']'), "]");
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke(';'), ";");
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke('\''), "'");

		rootPane.getActionMap().put("[", new playedVolumeDown());
		rootPane.getActionMap().put("]", new playedVolumeUp());
		rootPane.getActionMap().put(";", new songVolumeDown());
		rootPane.getActionMap().put("'", new songVolumeUp());

		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESCAPE_PRESSED);
		rootPane.getActionMap().put(ESCAPE_PRESSED, new escapeAction());
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_PRESSED);
		rootPane.getActionMap().put(ENTER_PRESSED, new enterAction());

		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), SHIFT_PRESSED);
		rootPane.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, true), SHIFT_RELEASED);
		rootPane.getActionMap().put(SHIFT_PRESSED, new shiftPressedAction());
		rootPane.getActionMap().put(SHIFT_RELEASED, new shiftReleasedAction());
	}

	public class keyPressedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			int noteNumber = pv.charToNoteNumber.get(e.getActionCommand().charAt(0));
			if (!pv.playedNotesOn.contains(noteNumber)) {
				pv.playedNotesOn.add(noteNumber);
				
				// stop this note from being played by the song
				for(int i = 0; i < pv.songNotesCurrentlyPlaying.size(); i++) {
					MIDINote note = pv.songNotesCurrentlyPlaying.get(i);
					if(note.key == noteNumber) {
						pv.songNotesCurrentlyPlaying.remove(note);
						pv.songNotesCurrentlyDisplayed.remove(note);
						i--;
					}
				}
				midiChannel.noteOff(noteNumber);
				midiChannel.noteOn(noteNumber, (int) (pv.playedNoteVelocity * pv.playedNoteVelocityMultiplier));
			}
		}

	}

	public class keyReleasedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			int noteNumber = pv.charToNoteNumber.get(e.getActionCommand().charAt(0));
			pv.playedNotesOn.remove((Integer) noteNumber);
			midiChannel.noteOff(noteNumber);
		}

	}

	public class tabPressedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			pv.tabHeld = true;
		}

	}

	public class tabReleasedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			pv.tabHeld = false;
		}

	}

	public class upPressedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (pv.tabHeld) {
				if (pv.instrumentSelected > 0)
					pv.instrumentSelected--;
				else {
					pv.instrumentSelected = pv.numberOfInstruments - 1;
					pv.firstInstrumentDisplayed = pv.numberOfInstruments - pv.numberOfInstrumentsDisplayed;
				}
				if (pv.instrumentSelected < pv.firstInstrumentDisplayed)
					pv.firstInstrumentDisplayed--;
				if (pv.instrumentSelected < 0)
					pv.instrumentSelected += pv.numberOfInstruments;

				midiChannel.programChange(instruments[instruments[pv.instrumentSelected].getPatch().getProgram()]
						.getPatch().getProgram());
			}
			if (pv.shiftHeld) {
				if (pv.trackSelected == 1)
					pv.trackSelected = sv.numberOfTracks;
				else
					pv.trackSelected--;
			}
		}

	}

	public class downPressedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (pv.tabHeld) {
				if (pv.instrumentSelected < pv.numberOfInstruments - 1)
					pv.instrumentSelected++;
				else {
					pv.instrumentSelected = 0;
					pv.firstInstrumentDisplayed = 0;
				}
				if (pv.instrumentSelected - pv.firstInstrumentDisplayed >= pv.numberOfInstrumentsDisplayed)
					pv.firstInstrumentDisplayed++;

				midiChannel.programChange(instruments[instruments[pv.instrumentSelected].getPatch().getProgram()]
						.getPatch().getProgram());
			}
			if (pv.shiftHeld) {
				if (pv.trackSelected == sv.numberOfTracks)
					pv.trackSelected = 1;
				else
					pv.trackSelected++;
			}
		}

	}

	public class leftAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			// change track settings
			if (pv.shiftHeld) {
				if (pv.playingSong) {
					// DP, D, P, N
					int settings = pv.trackSettings[pv.trackSelected];
					boolean previouslyPlayed = settings == 0 || settings == 2;
					if (pv.trackSettings[pv.trackSelected] == 0) {
						pv.trackSettings[pv.trackSelected] = 3;
					} else {
						pv.trackSettings[pv.trackSelected] -= 1;
					}
					settings = pv.trackSettings[pv.trackSelected];
					boolean currentlyPlayed = settings == 0 || settings == 2;
					// turn off notes
					if (previouslyPlayed && !currentlyPlayed) {
						for (MIDINote note : pv.songNotesCurrentlyPlaying) {
							if (note.track == pv.trackSelected)
								pv.midiChannel.noteOff(note.key);
						}
					}
				}
			}
			// change tempo
			else {
				if (sv.tempoMultiplier > 25) {
					sv.tempoMultiplier -= 1;
					sv.bpm = (int) (sv.baseBPM * sv.tempoMultiplier * sv.globalTempoMultiplier / 100);
					pv.mspt = 60000.0 / ((sv.bpm * 1.0) * (sv.ppq));
				}
			}
		}

	}

	public class rightAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			// change track settings
			if (pv.shiftHeld) {
				if (pv.playingSong) {
					// DP, D, P, N
					int settings = pv.trackSettings[pv.trackSelected];
					boolean previouslyPlayed = settings == 0 || settings == 2;
					pv.trackSettings[pv.trackSelected] = (pv.trackSettings[pv.trackSelected] + 1) % 4;
					settings = pv.trackSettings[pv.trackSelected];
					boolean currentlyPlayed = settings == 0 || settings == 2;
					// turn off notes
					if (previouslyPlayed && !currentlyPlayed) {
						for (MIDINote note : pv.songNotesCurrentlyPlaying) {
							if (note.track == pv.trackSelected)
								pv.midiChannel.noteOff(note.key);
						}
					}
				}
			}
			// change tempo
			else {
				if (sv.tempoMultiplier < 200) {
					sv.tempoMultiplier += 1;
					sv.bpm = (int) (sv.baseBPM * sv.tempoMultiplier * sv.globalTempoMultiplier / 100);
					pv.mspt = 60000.0 / ((sv.bpm * 1.0) * (sv.ppq));
				}
			}
		}

	}

	public class spacePressedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			if (pv.escapeScreen)
				return;

			if (!pv.paused) {
				for (MIDINote note : pv.songNotesCurrentlyPlaying) {
					midiChannel.noteOff(note.key);
				}
			}
			pv.paused = !pv.paused;
		}

	}

	public class minusPressedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (pv.quarterNoteHeightProportion > 0.02)
				pv.quarterNoteHeightProportion -= 0.01;
			mw.pd.setPianoKeys();
		}

	}

	public class equalPressedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (pv.quarterNoteHeightProportion < 0.3)
				pv.quarterNoteHeightProportion += 0.01;
			mw.pd.setPianoKeys();
		}

	}

	public class playedVolumeUp extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (pv.playedNoteVelocityMultiplier < 2.5) {
				pv.playedNoteVelocityMultiplier += 0.1;
				pv.playedNoteVelocityMultiplier = (Math.floor(pv.playedNoteVelocityMultiplier * 10 + 1) - 1) / 10;
			}
		}

	}

	public class playedVolumeDown extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (pv.playedNoteVelocityMultiplier > 0.3) {
				pv.playedNoteVelocityMultiplier -= 0.1;
				pv.playedNoteVelocityMultiplier = (Math.floor(pv.playedNoteVelocityMultiplier * 10 + 1) - 1) / 10;
			}
		}

	}

	public class songVolumeUp extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (pv.songNoteVelocityMultiplier < 2.5) {
				pv.songNoteVelocityMultiplier += 0.1;
				pv.songNoteVelocityMultiplier = (Math.floor(pv.songNoteVelocityMultiplier * 10 + 1) - 1) / 10;
			}

		}

	}

	public class songVolumeDown extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (pv.songNoteVelocityMultiplier > 0.3) {
				pv.songNoteVelocityMultiplier -= 0.1;
				pv.songNoteVelocityMultiplier = (Math.floor(pv.songNoteVelocityMultiplier * 10 + 1) - 1) / 10;
			}
		}

	}

	public class escapeAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (pv.escapeScreen) {
				pv.escapeScreen = false;
				pv.paused = false;
			} else {
				pv.escapeScreen = true;
				pv.paused = true;
				for (MIDINote note : pv.songNotesCurrentlyPlaying) {
					midiChannel.noteOff(note.key);
				}
			}
		}

	}

	public class enterAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (pv.escapeScreen) {
				mw.reset();
//				MainWindow mw2 = new MainWindow(c, v);
//				mw.dispose();
//				mw = mw2;
			}
		}

	}

	public class shiftPressedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			pv.shiftHeld = true;
		}

	}

	public class shiftReleasedAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			pv.shiftHeld = false;
		}

	}
}
