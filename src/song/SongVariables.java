package song;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SongVariables {
	int numberOfSongs;
	int songSelected = 0;
	int firstSongShown = 0;
	public int bpm;
	public int baseBPM;
	public int tempoMultiplier = 100; // in percent
	// 1.25 when delay = 20
	// 1.7 when delay = 5
	public final double globalTempoMultiplier = 1.25; // due to "lag", music needs to be sped up
	public int ppq = 1;
	public int numberOfTracks = 0;
	
	ArrayList<File> MIDIs = new ArrayList<File>();
	ArrayList<String> songNames = new ArrayList<String>();
	ArrayList<Integer> songBPMs = new ArrayList<Integer>();

	public ArrayList<MIDINote> songNotes = new ArrayList<MIDINote>();
	public ArrayList<TempoChange> tempoChanges = new ArrayList<TempoChange>();
	
	int initialDelay = 2000; // 2 seconds

	public SongVariables() {
		String MIDIFolderName = System.getProperty("user.dir") + "/src/song/MIDI Files";
		File folder = new File(MIDIFolderName);
		
		for(File f : folder.listFiles()) {
			// remove files that start with .
			String directory = f.toString();
			if(directory.substring(directory.length() - 9, directory.length()).equals(".DS_Store")) continue;
			MIDIs.add(f);
		}
		Collections.sort(MIDIs);
		numberOfSongs = MIDIs.size();
		for (int i = 0; i < numberOfSongs; i++) {
			File f = MIDIs.get(i);
			String directory = f.toString();
			String songName = directory.substring(0, directory.length() - 4);
			if(songName.charAt(0) == '.') continue;
			if (songName.lastIndexOf("/") > songName.lastIndexOf("\\")) {
				songName = songName.substring(songName.lastIndexOf("/") + 1);
			} else {
				songName = songName.substring(songName.lastIndexOf("\\") + 1);
			}
			songNames.add(songName);
			
			// add bpms of songs if there is one
//			String[] splitString = songName.split("-");
//			String endingNumber = splitString[splitString.length - 1];
//			if(endingNumber.length() > 4) songBPMs.add(100);
//			else songBPMs.add(Integer.parseInt(endingNumber.substring(1)));
		}
		
		bpm = 100;
		
	}
}
