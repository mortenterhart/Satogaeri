package puzzle;

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

    public List<FieldCircle> getAllRegisteredCircles() {
        List<FieldCircle> boardCircles = new ArrayList<>();

        for (int y = 0; y < getWidth(); y++) {
            for (int x = 0; x < getHeight(); x++) {
                Cell currentCell = cellBoard[x][y];
                if (currentCell != null && currentCell.isAnyCircleRegistered()) {
                    boardCircles.add(currentCell.getCircle());
                }
            }
        }

        return boardCircles;
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

    public void printCircles() {
        System.out.println("Cell Board with circle movements:");
        System.out.print("    ");
        for (int y = 0; y < getWidth(); y++) {
            System.out.print(String.valueOf(y).concat(" "));
        }
        System.out.print("\n  +-");
        for (int y = 0; y < getWidth(); y++){
            System.out.print("--");
        }
        System.out.println();

        for (int y = 0; y < getWidth(); y++) {
            System.out.print(String.valueOf(y).concat(" | "));
            for (int x = 0; x < getHeight(); x++) {
                Cell currentCell = get(x, y);
                if (currentCell.isAnyCircleRegistered()) {
                    if (currentCell.getCircle().getDistance().toIntValue() >= 0) {
                        System.out.print(currentCell.getCircle().getDistance().toIntValue() + " ");
                    } else {
                        System.out.print("O ");
                    }
                } else if (currentCell.isVisited()) {
                    System.out.print("- ");
                } else if (currentCell.isInvariant()) {
                    System.out.print("X ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println("\n\n");
    }

    public void print() {
        for (int y = 0; y < getWidth(); y++) {
            for (int x = 0; x < getHeight(); x++) {
                System.out.println("Indices           (" + x + ", " + y + ")");
                System.out.println("Cell Coordinates: (" + get(x, y).getGridX() + ", " + get(x, y).getGridY() + ")");
            }
        }
    }
}
