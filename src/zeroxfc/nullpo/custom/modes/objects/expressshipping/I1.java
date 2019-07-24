package zeroxfc.nullpo.custom.modes.objects.expressshipping;

import mu.nu.nullpo.game.component.Block;

public class I1 implements GamePiece {
	private static int[][][] contents = {
			new int[][] {
					new int[] { 1 }
			}
	};

	private int[] location;
	private int state;

	public I1(int x, int y) {
		location = new int[] { x, y };
		state = 0;
	}

	@Override
	public int getScore() {
		return 25;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@Override
	public int getX() {
		return location[0];
	}

	@Override
	public int getY() {
		return location[1];
	}

	@Override
	public int[] getLocation() {
		return location;
	}

	@Override
	public int[][] getContents() {
		return contents[state];
	}

	@Override
	public int[] getCursorOffset() {
		return new int[] { 0, 0 };
	}

	@Override
	public double getConveyorYOffset() {
		return 0.0;
	}

	@Override
	public int getState() {
		return state;
	}

	@Override
	public int getColour() {
		return Block.BLOCK_COLOR_RED;
	}

	@Override
	public void rotate() {
		// DO NOTHING.
	}

	@Override
	public void setLocation(int x, int y) {
		location = new int[] { x, y };
	}
}