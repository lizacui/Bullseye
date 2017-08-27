import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;


public class Target {
	
	private static int gBWidth = GameBoard.gBWidth;
	
	static int outRingWidth = 60;
	static int outRingHeight = 60;
	int outRingUpLeftX;
	int outRingUpLeftY;
	
	static int midRingWidth = 40;
	static int midRingHeight = 40;
	int midRingUpLeftX;
	int midRingUpLeftY;
	
	static int bullseyeWidth = 20;
	static int bullseyeHeight = 20;
	int bullseyeUpLeftX;
	int bullseyeUpLeftY;
	
	private int speedX = 1;
	
	// Direction constants
	static int LEFT = -1;
	static int RIGHT = 1;
	
	// Used to indicate if the target starts on the left or on the right side of the screen
	int startPos;
	
	static ArrayList<Target> targets;
	
	// Possible starting points for targets
	// (either from the left side or the right side of the window)
	static int[] upLeftXStartPosArray = {-outRingWidth, gBWidth};

	public boolean onScreen = true;
	
	// Location of sound file
	String targetHitSound = "file:./src/target_hit.wav";
	
	public Target(int outRingStartPosX, int outRingStartPosY,
			      int midRingStartPosX, int midRingStartPosY,
			      int bullseyeStartPosX, int bullseyeStartPosY) {
		this.outRingUpLeftX = outRingStartPosX;
		this.outRingUpLeftY = outRingStartPosY;
		this.midRingUpLeftX = midRingStartPosX;
		this.midRingUpLeftY = midRingStartPosY;
		this.bullseyeUpLeftX = bullseyeStartPosX;
		this.bullseyeUpLeftY = bullseyeStartPosY;
		
		// Generate a random speed for the target
		this.speedX = (int) (Math.random() * 4 + 1);
	}
	
	// Creates a bounding Rectangle around the target used for collision detection
	public Rectangle getOutRingBounds() {
		return new Rectangle(outRingUpLeftX, outRingUpLeftY, outRingWidth, outRingHeight);
	}
	
	public Rectangle getMidRingBounds() {
		return new Rectangle(midRingUpLeftX, midRingUpLeftY, midRingWidth, midRingHeight);
	}
	
	public Rectangle getBullseyeBounds() {
		return new Rectangle(bullseyeUpLeftX, bullseyeUpLeftY, bullseyeWidth, bullseyeHeight);
	}
	
	public void move(ArrayList<Dart> darts, int startPos) {
		for (Target target : targets) {
			if (target.onScreen) {
				Rectangle outRingBounds = target.getOutRingBounds();
				Rectangle midRingBounds = target.getMidRingBounds();
				Rectangle bullseyeBounds = target.getBullseyeBounds();
				// Check if a target has been hit by a dart
				for (Dart dart : darts) {
					if (dart.onScreen) {
	    				if (bullseyeBounds.contains(dart.getCenterX(), dart.getCenterY())) {
	    					target.onScreen = false;
	    					dart.onScreen = false;
	    					GameBoard.playSound(targetHitSound);
	    					// Points
	    					DrawGamePanel.bullseyeHits += 1;
	    					DrawGamePanel.totalPoints += 20;
	    				}
	    				else if (midRingBounds.contains(dart.getCenterX(), dart.getCenterY())
	    						 && (((dart.getCenterX() > (target.outRingUpLeftX + Target.outRingWidth - 20))
		    						   || (dart.getCenterX() < (target.outRingUpLeftX + 20))))) {
	    					target.onScreen = false;
	    					dart.onScreen = false;
	    					GameBoard.playSound(targetHitSound);
	    					// Points
	    					DrawGamePanel.midRingHits += 1;
	    					DrawGamePanel.totalPoints += 10;
	    				}
	    				else if (outRingBounds.contains(dart.getCenterX(), dart.getCenterY())
	    						 && (((dart.getCenterX() > (target.outRingUpLeftX + Target.outRingWidth - 10))
	    							   || (dart.getCenterX() < (target.outRingUpLeftX + 10))))) {
	    					target.onScreen = false;
	    					dart.onScreen = false;
	    					GameBoard.playSound(targetHitSound);
	    					// Points
	    					DrawGamePanel.outRingHits += 1;
	    					DrawGamePanel.totalPoints += 5;
	    				}
					}
				}
				// If the target goes off the left or right boundary of the screen
				if ((target.outRingUpLeftX < -Target.outRingWidth)
					|| (target.outRingUpLeftX > gBWidth)) {
					target.onScreen = false;
				}
			}
		}
		
		// Moves the target
		// If the target starts on the left side of the screen
		if (startPos == LEFT) {
			this.outRingUpLeftX += speedX;
			this.midRingUpLeftX += speedX;
			this.bullseyeUpLeftX += speedX;
		}
		// If the target starts on the right side of the screen
		else {
			this.outRingUpLeftX -= speedX;
			this.midRingUpLeftX -= speedX;
			this.bullseyeUpLeftX -= speedX;
		}
	}
}


// Adds 3 new targets to the game
class AddTargets implements Runnable {
	
	ArrayList<Target> targets;
	
	public AddTargets() {
		this.targets = GameBoard.targets;
	}

	@Override
	public void run() {
		for (int i = 0; i < 3; i++) {
			// Generate a random starting x-coordinate for the target
			int upLeftXStartIndex = (int) (Math.random() * 2);
			int upLeftXStartPos = Target.upLeftXStartPosArray[upLeftXStartIndex];
			
			// Generate a random starting y-coordinate for the target
			int upLeftYStartPos = (int) (Math.random() * (GameBoard.gBHeight - Target.outRingHeight - Player.playerHeight * 4));

			Target newTarget = new Target(upLeftXStartPos, upLeftYStartPos,
					                      upLeftXStartPos + 10, upLeftYStartPos + 10,
			                              upLeftXStartPos + 20, upLeftYStartPos + 20);
			
			if (upLeftXStartPos == Target.upLeftXStartPosArray[0]) {
				newTarget.startPos = Target.LEFT;
			}
			else {
				newTarget.startPos = Target.RIGHT;
			}
			
			targets.add(newTarget);
		}
	}
	
}


// Removes all targets that are not on the screen from the ArrayList, targets
class RemoveTargets implements Runnable {
	
	ArrayList<Target> targets;
	
	public RemoveTargets() {
		this.targets = GameBoard.targets;
	}

	@Override
	public void run() {
		Iterator<Target> iterator = targets.iterator();
		while (iterator.hasNext()) {
		    Target target = iterator.next();
		    if (target.onScreen == false) {
		    	iterator.remove();
		    }
		}
	}
	
}