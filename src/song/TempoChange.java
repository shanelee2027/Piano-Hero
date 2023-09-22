package song;

public class TempoChange implements Comparable<TempoChange> {
	public long tick;
	public int bpm;
	public int track;
	public TempoChange(long tick, int bpm, int track) {
		this.tick = tick;
		this.bpm = bpm;
		this.track = track;
	}
	@Override
	public int compareTo(TempoChange arg0) {
		return (int) (this.tick - arg0.tick);
	}
}
