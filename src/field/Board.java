package field;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Region> containedRegions;
    private Cell[][] cellBoard;

    public Board(int dimension) {
        containedRegions = new ArrayList<>();
        cellBoard = new Cell[dimension][dimension];
    }

    public int getWidth() {
        return cellBoard.length;
    }

    public int getHeight() {
        if (cellBoard.length > 0) {
            return cellBoard[0].length;
        }

        return 0;
    }

    public Cell get(int x, int y) {
        checkBoundaries(x, y);
        return cellBoard[x][y];
    }

    public void set(Cell cell, int x, int y) {
        checkBoundaries(x, y);
        cellBoard[x][y] = cell;
    }

    public void addRegion(Region region) {
        containedRegions.add(region);
    }

    private void checkBoundaries(int posX, int posY) {
        if (posX < 0 || posX >= cellBoard.length ||
            posY < 0 || posY >= cellBoard[posX].length) {

            throw new IndexOutOfBoundsException("tried to access position (" + posX + ", " +
                    posY + ") with board dimension of " + getWidth());
        }
    }

    public List<Region> getRegions() {
        return containedRegions;
    }
}
