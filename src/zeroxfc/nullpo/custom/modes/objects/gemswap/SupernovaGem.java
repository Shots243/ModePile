package zeroxfc.nullpo.custom.modes.objects.gemswap;

import java.util.ArrayList;

public class SupernovaGem implements Gem {
    /**
     * Gem ID
     */
    private static final int ID = GemField.GEMID_EMPTY;
    private final boolean special;
    /**
     * Class properties.
     */
    private int[] location;
    private int colour;
    private boolean recentSwap;
    private boolean recentFall;
    private boolean matchedHorizontal;
    private boolean matchedVertical;
    private boolean actionConducted;
    private boolean destroy;

    public SupernovaGem(int x, int y, int colour) {
        location = new int[] { x, y };
        this.colour = colour;

        recentSwap = false;
        recentFall = false;
        matchedHorizontal = false;
        matchedVertical = false;
        actionConducted = false;
        special = true;
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
    public void setColour(int colour) {
        this.colour = colour;
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
    public void setRecentSwap(boolean isRecentSwap) {
        recentSwap = isRecentSwap;
    }

    @Override
    public boolean getRecentFall() {
        return recentFall;
    }

    @Override
    public void setRecentFall(boolean isRecentFall) {
        recentFall = isRecentFall;
    }

    @Override
    public void setLocation(int x, int y) {
        location = new int[] { x, y };
    }

    @Override
    public int conductAction(GemField field, int[] args, ArrayList<ScoreEvent> eventList) {
        int ctr = 0;

        int[] offsets = { -1, 0, 1 };

        for (int offset : offsets) {
            for (int x = 0; x < field.getWidth(); x++) {
                int tX = location[0] + x;
                int tY = location[1] + offset;

                if (field.getCell(tX, tY) != null && tX != location[0]) {
                    if (field.getCell(tX, tY).getID() != GemField.GEMID_EMPTY &&
                        !field.getCell(tX, tY).getActionConducted()) {
                        ctr++;
                        field.getCell(tX, tY).conductAction(field, new int[] { colour }, eventList);
                        field.getCell(tX, tY).setDestroy(true);
                    }
                }
            }

            for (int y = 0; y < field.getHeight(); y++) {
                int tX = location[0] + offset;
                int tY = location[1] + y;

                if (field.getCell(tX, tY) != null && tY != location[1]) {
                    if (field.getCell(tX, tY).getID() != GemField.GEMID_EMPTY &&
                        !field.getCell(tX, tY).getActionConducted()) {
                        ctr++;
                        field.getCell(tX, tY).conductAction(field, new int[] { colour }, eventList);
                        field.getCell(tX, tY).setDestroy(true);
                    }
                }
            }
        }

        actionConducted = true;
        eventList.add(new ScoreEvent(ctr, location[0], location[1], ID, colour));

        return ctr;
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
    public void setMatchedHorizontal(boolean isMatched) {
        matchedHorizontal = isMatched;
    }

    @Override
    public boolean getMatchedVertical() {
        return matchedVertical;
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
        return new SupernovaGem(location[0], location[1], colour);
    }

    @Override
    public boolean getDestroy() {
        // TODO Auto-generated method stub
        return destroy;
    }

    @Override
    public void setDestroy(boolean shouldDestroy) {
        // TODO Auto-generated method stub
        destroy = shouldDestroy;
    }

}
