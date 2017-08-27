import java.awt.Polygon;


public class Dart extends Polygon {
	
	private static final long serialVersionUID = 1L;
	
	// All darts start on the screen at the nose of player (tip of the triangle)
	
	int gBWidth = GameBoard.gBWidth;
	int gBHeight = GameBoard.gBHeight;

	private double centerX = 0;
	private double centerY = 0;
	
	// Arrays hold the x and y coordinates of the dart
	public static int[] dartXArray = {0, 3, -3, 0};
	public static int[] dartYArray = {-3, 3, 3, -3};
	
	private int dartWidth = 6;
	private int dartHeight = 6;
	
	public boolean onScreen = false;
	
	public Dart(double centerX, double centerY) {
		super(dartXArray, dartYArray, 4);
		
		// Sets the center of the dart to be the coordinates of the player's nose
		this.centerX = centerX;
		this.centerY = centerY;

		onScreen = true;
	}
	
	public double getCenterX() {
		return centerX;
	}
		
	public double getCenterY() {
		return centerY;
	}
	
	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}
		
	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}
		
	public void decreaseYPos(double amt) {
		centerY -= amt;
	}
	
	public int getDartWidth() {
		return dartWidth;
	}
			
	public int getDartHeight() {
		return dartHeight;
	}
	
	public void move() {
		if (onScreen) {
			decreaseYPos(5);
			
			// If the dart goes off the screen, it disappears
			if (getCenterY() < 0) {
				onScreen = false;
			}
		}
	}
	
}