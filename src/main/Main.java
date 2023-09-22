package main;

public class Main {
	public static void main(String[] args) {
		System.out.println("hello");
		Constants c = new Constants();
		Variables v = new Variables(c.WIDTH, c.HEIGHT);
		MainWindow mw = new MainWindow(c, v);
		
	}
}
