import java.awt.Polygon;


@SuppressWarnings("serial")

public class Player extends Polygon {
	
	int gBWidth = GameBoard.gBWidth;
	int gBHeight = GameBoard.gBHeight;
	
	// Arrays contain the x and y coordinates of the player triangle
	// Based on if center of player is (0,0)
	public static int[] playerXArray = {0, 15, -15, 0};
	public static int[] playerYArray = {-15, 15, 15, -15};
	
	static int playerWidth = 27;
	static int playerHeight = 30;
	
	private double centerX = gBWidth / 2;
	private double centerY = gBHeight - playerHeight * 3;
	
	// Direction constants
	static int LEFT = -1;
	static int RIGHT = 1;
	static int STATIONARY = 0;
	
	// Used to indicate if player is moved to the left, to the right, or is stationary
	int direction = STATIONARY;

	public Player() {
		super(playerXArray, playerYArray, 4);
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
	
	// Allows player to move to the left (when amt is negative)
	// and to the right (when amt is positive)
	public void horizontalMovement(double amt) {
		this.centerX += amt;
	}
	
	public int getPlayerWidth() {
		return playerWidth;
	}
		
	public int getPlayerHeight() {
		return playerHeight;
	}
	
	public double getPlayerNoseX() {
		return getCenterX();
	}
	
	public double getPlayerNoseY() {
		return getCenterY() - playerHeight / 2;
	}
	
	public void move(int direction) {
		if (direction == LEFT) {
			horizontalMovement(-4);
		}
		else if (direction == RIGHT) {
			horizontalMovement(4);
		}
		else {
			return;
		}
		// Sets the boundaries for the player (cannot go off the screen)
		// If the player reaches the left boundary
		if (getCenterX() <= (playerWidth / 2 + 30)) {
			setCenterX(playerWidth / 2 + 30);
		}
		// If the player reaches the right boundary
		if (getCenterX() >= (gBWidth - playerWidth + playerWidth / 2 - 40)) {
			setCenterX(gBWidth - playerWidth + playerWidth / 2 - 40);
		}
	}
	
}
