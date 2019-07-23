package zeroxfc.nullpo.custom.modes.objects.gemswap;

import java.util.ArrayList;

import mu.nu.nullpo.game.component.Block;

public class EmptyGem implements Gem {
	/** Gem ID */
	private static final int ID = GemField.GEMID_EMPTY;
	
	/**
	 * Class properties.
	 */
	private int[] location;
	private int colour;
	private boolean recentSwap, recentFall, matchedHorizontal, matchedVertical, special, actionConducted, destroy;
	
	public EmptyGem(int x, int y) {
		location = new int[] { x, y };
		colour = Block.BLOCK_COLOR_NONE;
		
		recentSwap = false;
		recentFall = false;
		matchedHorizontal = false;
		matchedVertical = false;
		actionConducted = false;
		special = false;
		destroy = false;
	}
	
	@Override
	public int getID() {
		return ID;
	}

	@Override
	public int getColour() {
		return colour;
	}

	@Override
	public int[] getLocation() {
		return location;
	}

	@Override
	public boolean getRecentSwap() {
		return recentSwap;
	}

	@Override
	public boolean getRecentFall() {
		return recentFall;
	}

	@Override
	public void setColour(int colour) {
		this.colour = colour;
	}

	@Override
	public void setLocation(int x, int y) {
		location = new int[] { x, y };
	}

	@Override
	public void setRecentSwap(boolean isRecentSwap) {
		recentSwap = isRecentSwap;
	}

	@Override
	public void setRecentFall(boolean isRecentFall) {
		recentFall = isRecentFall;
	}

	@Override
	public int conductAction(GemField field, int[] args, ArrayList<ScoreEvent> eventList) {
		actionConducted = true;

		return 0;
	}

	@Override
	public boolean getSpecial() {
		return special;
	}

	@Override
	public boolean getMatchedHorizontal() {
		return matchedHorizontal;
	}

	@Override
	public boolean getMatchedVertical() {
		return matchedVertical;
	}

	@Override
	public void setMatchedHorizontal(boolean isMatched) {
		matchedHorizontal = isMatched;
	}

	@Override
	public void setMatchedVertical(boolean isMatched) {
		matchedVertical = isMatched;
	}

	@Override
	public boolean getActionConducted() {
		return actionConducted;
	}

	@Override
	public void setActionConducted(boolean isConduted) {
		actionConducted = isConduted;
	}

	@Override
	public Gem getSelf() {
		return new EmptyGem(location[0], location[1]);
	}

	@Override
	public boolean getDestroy() {
		// TODO Auto-generated method stub
		return destroy;
	}

	@Override
	public void setDestroy(boolean shouldDestroy) {
		// TODO Auto-generated method stub
		destroy = false;
	}

}