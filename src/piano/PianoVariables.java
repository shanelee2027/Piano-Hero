package piano;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;

import song.MIDINote;


public class PianoVariables {
	HashMap<Character, Integer> charToNoteNumber = new HashMap<Character, Integer>();
	HashMap<Integer, Character> noteNumberToChar = new HashMap<Integer, Character>();
	HashMap<Character, Integer> charToWhiteKeyNumber = new HashMap<Character, Integer>();
	HashMap<Character, Integer> charToBlackKeyNumber = new HashMap<Character, Integer>();
	
	double playedNoteVelocityMultiplier = 1.4;
	double songNoteVelocityMultiplier = 1;
	int playedNoteVelocity = 64;
	int numberOfOctaves = 3;
	
	Synthesizer synthesizer;
	MidiChannel midiChannel;
	Instrument[] instruments;
	
	char[] whiteKeyPrint;
	char[] blackKeyPrint;
	
	boolean tabHeld = false;
	boolean shiftHeld = false;
	public boolean paused = false;
	public boolean escapeScreen = false;
	
	int instrumentSelected = 0;
	int numberOfInstrumentsDisplayed = 20;
	int firstInstrumentDisplayed = 0;
	int numberOfInstruments = 115;
	
	public boolean playingSong = false;
	ArrayList<Integer> playedNotesOn = new ArrayList<Integer>();
	ArrayList<MIDINote> songNotesCurrentlyPlaying = new ArrayList<MIDINote>();
	ArrayList<MIDINote> songNotesCurrentlyDisplayed = new ArrayList<MIDINote>();
	public int[] trackSettings; // 0 through 3
	String[] trackSettingsStrings = {"Displayed & Played", "Only Displayed", "Only Played", "None"};
	public int trackSelected = 1;
	
	public int tick = 0;
	public int heightInTicks;
	public double pxpt; // pixel per tick
	public double mspt; // milliseconds per tick
	public double quarterNoteHeightProportion = 0.1;
	
	public PianoVariables() {
		String listOfChars = "1234567890poiuytrewqasdfghjklmnbvcxz";
		String listOfWhiteChars = "135680oiyreqsfgjlmbcz";
		String listOfWhiteCapChars = "135680OIYREQSFGJLMBCZ";
		String listOfBlackChars = "2479putwadhknvx";
		String listOfBlackCapChars = "2479PUTWADHKNVX";
		char[] charArray = listOfChars.toCharArray();
		int noteNumber = 48;
		for(char c : charArray) {
			charToNoteNumber.put(c, noteNumber);
			noteNumberToChar.put(noteNumber, c);
			noteNumber++;
		}
		whiteKeyPrint = listOfWhiteCapChars.toCharArray();
		blackKeyPrint = listOfBlackCapChars.toCharArray();
		
		char[] whiteKeyChars = listOfWhiteChars.toCharArray();
		char[] blackKeyChars = listOfBlackChars.toCharArray();
		for(int i = 0; i < 21; i++) {
			charToWhiteKeyNumber.put(whiteKeyChars[i], i);
		}
		for(int i = 0; i < 15; i++) {
			charToBlackKeyNumber.put(blackKeyChars[i], i);
		}
	}
}
