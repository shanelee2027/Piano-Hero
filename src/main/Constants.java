package main;
import java.awt.*;

public class Constants {
	// milliseconds between updates
	public final int DELAY = 20; // 20
	
	// screen size
	Rectangle rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	public final int WIDTH = rect.width;
	public final int HEIGHT = rect.height;
	
	
	public Constants() {
		
	}
}
