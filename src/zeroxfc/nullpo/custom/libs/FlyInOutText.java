package zeroxfc.nullpo.custom.libs;

import java.util.Random;

import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;

public class FlyInOutText {
	// String to draw
	private String mainString;
	
	// Timings
	private int flyInTime;
	private int persistTime;
	private int flyOutTime;
	
	// Lifetime variable
	private int currentLifetime;
	
	// Vector array of positions
	private DoubleVector[][] letterPositions;
	
	// Vector array of velocities
	// private DoubleVector[] letterVelocities;
	
	// Start location
	private DoubleVector[][] startLocation;
	
	// Destination vector
	private DoubleVector[][] destinationLocation;
	
	// Shadow count
	private int shadowCount;
	
	// Colours: idx 0 - main, any other - shadows
	private int[] textColours;
	
	// Randomiser for start pos
	private Random positionRandomiser;
	
	// Text scale
	private float textScale;
	
	// Should it flash?
	private boolean flash;
	
	public FlyInOutText(String text, int destinationX, int destinationY, int timeIn, int timePersist, int timeOut, int[] colours, float scale, long seed, boolean flashOnLand) {
		// Independent vars.
		mainString = text;
		// destinationLocation = new DoubleVector(destinationX, destinationY, false);
		flyInTime = timeIn;
		persistTime = timePersist;
		flyOutTime = timeOut;
		textColours = colours;
		positionRandomiser = new Random(seed);
		
		currentLifetime = 0;
		textScale = scale;
		flash = flashOnLand;
		
		// Dependent vars.
		shadowCount = textColours.length;
		letterPositions = new DoubleVector[shadowCount][mainString.length()];
		startLocation = new DoubleVector[shadowCount][mainString.length()];
		destinationLocation = new DoubleVector[shadowCount][mainString.length()];
		// letterVelocities = new DoubleVector[mainString.length()];
		
		int sMod = 16;
		if (scale == 2.0f) sMod = 32;
		if (scale == 0.5f) sMod = 16;
		
		for (int i = 0; i < mainString.length(); i++) {
			int startX = 0, startY = 0;
			DoubleVector position = DoubleVector.zero();
			// double distanceX = 0, distanceY = 0;
			// DoubleVector velocity = DoubleVector.zero();
			
			double dec1 = positionRandomiser.nextDouble();
			double dec2 = positionRandomiser.nextDouble();
			
			if (dec1 < 0.5) {
				startX = -sMod;
				if (dec2 < 0.5) startX = 41 * sMod;
				
				startY = (int)(positionRandomiser.nextDouble() * (32 * sMod)) - sMod;
			} else {
				startY = -sMod;
				if (dec2 < 0.5) startY = 31 * sMod;
				
				startX = (int)(positionRandomiser.nextDouble() * (42 * sMod)) - sMod;
			}
			
			position = new DoubleVector(startX, startY, false);
			// distanceX = (destinationLocation.getX() + (i * sMod)) - position.getX();
			// distanceY = destinationLocation.getY() - position.getY();
			// velocity = new DoubleVector(distanceX / flyInTime, distanceY / flyInTime, false);
			
			// letterVelocities[i] = velocity;
			for (int j = 0; j < letterPositions.length; j++) {
				letterPositions[j][i] = position;
				startLocation[j][i] = position;
				destinationLocation[j][i] = new DoubleVector(destinationX + (sMod * i), destinationY, false);
			}
		}
	}
	
	public void draw(GameEngine engine, EventReceiver receiver, int playerID) {
		for (int i = letterPositions.length - 1; i >= 0; i--) {
			for (int j = 0; j < letterPositions[i].length; j++) {
				receiver.drawDirectFont(engine, playerID,
						(int)letterPositions[i][j].getX(), (int)letterPositions[i][j].getY(),
						String.valueOf(mainString.charAt(j)),
						((((currentLifetime - i) / 4) % 2 == 0) && flash) ? EventReceiver.COLOR_WHITE : textColours[i], textScale);
			}
		}
	}
	
	public void update() {
		for (int i = 0; i < letterPositions.length; i++) {
			if (currentLifetime - i >= 0 && currentLifetime - i < flyInTime) {
				for (int j = 0; j < letterPositions[i].length; j++) {
					int v1 = (int)(Interpolation.lerp(startLocation[i][j].getX(), destinationLocation[i][j].getX(), ((double)(currentLifetime - i) / flyInTime)));
					int v2 = (int)(Interpolation.lerp(startLocation[i][j].getY(), destinationLocation[i][j].getY(), ((double)(currentLifetime - i) / flyInTime)));
					letterPositions[i][j] = new DoubleVector(v1, v2, false);
				}
			} else if (currentLifetime - i >= flyInTime + persistTime) {
				for (int j = 0; j < letterPositions[i].length; j++) {
					int v1 = (int)(Interpolation.lerp(destinationLocation[i][j].getX(), startLocation[i][j].getX(), ((double)(currentLifetime - i - flyInTime - persistTime) / flyOutTime)));
					int v2 = (int)(Interpolation.lerp(destinationLocation[i][j].getY(), startLocation[i][j].getY(), ((double)(currentLifetime - i - flyInTime - persistTime) / flyOutTime)));
					letterPositions[i][j] = new DoubleVector(v1, v2, false);
				}
			} else if (currentLifetime - i == flyInTime) {
				for (int j = 0; j < letterPositions[i].length; j++) {
					letterPositions[i][j] = new DoubleVector(destinationLocation[i][j].getX(), destinationLocation[i][j].getY(), false);
				}
			}
		}
		
		currentLifetime++;
	}
	
	/**
	 * Tells a parent method whether to null this object.
	 * @return A boolean that tells parent method to purge or not.
	 */
	public boolean shouldPurge() {
		return currentLifetime >= (flyInTime + persistTime + flyOutTime + shadowCount + 1);
	}
}