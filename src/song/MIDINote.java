package song;

public class MIDINote implements Comparable<MIDINote> {
	public long onTick;
	public long offTick;
	public int velocity;
	public int key;
	public int track;
	public MIDINote(long onTick, long offTick, int key, int velocity, int track) {
		this.onTick = onTick;
		this.offTick = offTick;
		this.key = key;
		this.velocity = velocity;
		this.track = track;
	}
	@Override
	public int compareTo(MIDINote arg0) {
		return (int) (this.onTick - arg0.onTick);
	}
}
