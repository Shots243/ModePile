package zeroxfc.nullpo.custom.modes.objects.expressshipping;

public interface GamePiece {
	int getScore();
	int getWidth();
	int getHeight();
	int getX();
	int getY();
	int[] getLocation();
	int[][] getContents();
	int[] getCursorOffset();
	double getConveyorYOffset();
	int getState();
	int getColour();

	void rotate();
	void setLocation(int x, int y);
}
