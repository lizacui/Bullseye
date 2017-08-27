import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import java.util.ArrayList;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.*;


public class GameBoard extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	static int gBWidth = 1000;
	static int gBHeight = 800;
	
	static boolean keyPressed = false;
	static int keyPressedCode;
	
	static ArrayList<Dart> darts = new ArrayList<Dart>();
	
	public static ArrayList<Target> targets = new ArrayList<Target>();
	
	// Location of sound file
	String dartFiredSound = "file:./src/dart_fired.wav";
	
	public static void main(String[] args) {
		new GameBoard();
	}
	
	public GameBoard() {
		this.setSize(gBWidth, gBHeight);
		this.setTitle("Bullseye! The Game");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					keyPressed = true;
					keyPressedCode = e.getKeyCode();
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					keyPressed = true;
					keyPressedCode = e.getKeyCode();
				}
				else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					playSound(dartFiredSound);
					darts.add(new Dart(DrawGamePanel.player.getPlayerNoseX(),
									   DrawGamePanel.player.getPlayerNoseY()));
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				keyPressed = false;
			}

			@Override
			public void keyTyped(KeyEvent e) {}
			
		});
		
		DrawGamePanel gamePanel = new DrawGamePanel();
		this.add(gamePanel, BorderLayout.CENTER);
		
		// Threads
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
		// Repaints the game board every 20 milliseconds
		executor.scheduleAtFixedRate(new RepaintBoard(this), 0L, 20L, TimeUnit.MILLISECONDS);
		// Adds new targets to the game every 4 seconds
		executor.scheduleAtFixedRate(new AddTargets(), 0L, 4L, TimeUnit.SECONDS);
		// Removes all targets that are not on the screen from the ArrayList, targets
		executor.scheduleAtFixedRate(new RemoveTargets(), 10L, 10L, TimeUnit.SECONDS);
		
		this.setVisible(true);
	}
	
	// Plays all sound effects
	public static void playSound(String sound) {
		URL soundLocation;
		
		try {
			soundLocation = new URL(sound);
			Clip clip = null;
			clip = AudioSystem.getClip();
        		AudioInputStream inputStream;
        		inputStream = AudioSystem.getAudioInputStream(soundLocation);
			clip.open(inputStream);
			clip.loop(0);
			clip.start();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
                catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
                catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
}


class RepaintBoard implements Runnable {
	
	GameBoard board;
	
	public RepaintBoard(GameBoard board) {
		this.board = board;
	}

	@Override
	public void run() {
		board.repaint();
	}
	
}


@SuppressWarnings("serial")

class DrawGamePanel extends JComponent {
	
	// Keeps track of points
	static int bullseyeHits = 0;
	static int midRingHits = 0;
	static int outRingHits = 0;
	static int totalPoints = 0;
	
	// Points to targets ArrayList in GameBoard class
	public ArrayList<Target> targets = GameBoard.targets;
	
	static Player player = new Player();
	
	static int gBWidth = GameBoard.gBWidth;
	static int gBHeight = GameBoard.gBHeight;
	
	public DrawGamePanel() {
		Target.targets = targets;
	}
	
	public void paint(Graphics g) {
		Graphics2D gSettings = (Graphics2D) g;
		
		AffineTransform identity = new AffineTransform();
		
		// Draw a white background that is as big as the game board
		gSettings.setColor(Color.WHITE);
		gSettings.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// Set rendering rules
		gSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Set the drawing/outlining color to black
		gSettings.setPaint(Color.BLACK);
		
		// Settings to display the points
		Font font = new Font("Verdana", Font.BOLD, 20);
		gSettings.setFont(font);
		gSettings.drawString("SCOREBOARD" +
			             "     Outer Ring: " + Integer.toString(outRingHits) +
			             "     Middle Ring: " + Integer.toString(midRingHits) +
			             "     Bullseye: " + Integer.toString(bullseyeHits) +
			             "     Total Points: " + Integer.toString(totalPoints), 10, 20);

		for(Target target : targets) {
			if (target.onScreen) {
				target.move(GameBoard.darts, target.startPos);
				Shape outRing = new Ellipse2D.Double((double) target.outRingUpLeftX,
						                     (double) target.outRingUpLeftY,
						                     (double) Target.outRingWidth,
			                                             (double) Target.outRingHeight);
				Shape midRing = new Ellipse2D.Double((double) target.midRingUpLeftX,
                                                                     (double) target.midRingUpLeftY,
                                                                     (double) Target.midRingWidth,
                                                                     (double) Target.midRingHeight);
				Shape bullseye = new Ellipse2D.Double((double) target.bullseyeUpLeftX,
                                                                      (double) target.bullseyeUpLeftY,
                                                                      (double) Target.bullseyeWidth,
                                                                      (double) Target.bullseyeHeight);
				gSettings.setColor(Color.RED);
				gSettings.fill(outRing);
				gSettings.setColor(Color.WHITE);
				gSettings.fill(midRing);
				gSettings.setColor(Color.RED);
				gSettings.fill(bullseye);
			}
		}

		if (GameBoard.keyPressed == false) {
			player.direction = Player.STATIONARY;
		}
		else if (GameBoard.keyPressed == true && GameBoard.keyPressedCode == KeyEvent.VK_LEFT) {
			player.direction = Player.LEFT;
		}
		else if (GameBoard.keyPressed == true && GameBoard.keyPressedCode == KeyEvent.VK_RIGHT) {
			player.direction = Player.RIGHT;
		}
		
		player.move(player.direction);
		
		gSettings.setTransform(identity);
		
		gSettings.translate(player.getCenterX(), player.getCenterY());
		
		// Draw the player
		gSettings.setColor(Color.BLACK);
		gSettings.fill(player);
		
		// Draw the darts
		for (Dart dart : GameBoard.darts) {
			dart.move();
			if (dart.onScreen) {
				gSettings.setTransform(identity);
				gSettings.translate(dart.getCenterX(), dart.getCenterY());
				gSettings.fill(dart);
			}
		}
	}
	
}
