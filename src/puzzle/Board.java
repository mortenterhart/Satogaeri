package puzzle;

import logging.LogEngine;

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
                if (currentCell != null && currentCell.hasAnyCircleRegistered()) {
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

    public void dumpCellBoard() {
        LogEngine.instance.logln("Cell Board with circle movements:");

        printHeader();
        for (int y = 0; y < getWidth(); y++) {
            LogEngine.instance.log(String.valueOf(y).concat(" | "));
            for (int x = 0; x < getHeight(); x++) {
                Cell currentCell = get(x, y);
                if (currentCell.hasAnyCircleRegistered()) {
                    if (!currentCell.getCircle().hasAnyDistance()) {
                        LogEngine.instance.log(currentCell.getCircle().getDistance().toIntValue() + " ", false);
                    } else {
                        LogEngine.instance.log("@ ", false);
                    }
                } else if (currentCell.isVisited()) {
                    LogEngine.instance.log("- ", false);
                } else if (currentCell.isInvariant()) {
                    LogEngine.instance.log("X ", false);
                } else {
                    LogEngine.instance.log(". ", false);
                }
            }
            LogEngine.instance.newLine(false);
        }
        LogEngine.instance.newLine(true);
        LogEngine.instance.logln("[0-9]  cell with determined distance");
        LogEngine.instance.logln("@      cell with any distance");
        LogEngine.instance.logln("-      visited cell");
        LogEngine.instance.logln("X      invariant cell");
        LogEngine.instance.logln(".      free cell");
        LogEngine.instance.newLine(true);
    }

    private void printHeader() {
        LogEngine.instance.log("    ");
        for (int y = 0; y < getWidth(); y++) {
            LogEngine.instance.log(String.valueOf(y).concat(" "), false);
        }

        LogEngine.instance.newLine(false);
        LogEngine.instance.log("  +-");
        for (int y = 0; y < getWidth(); y++) {
            LogEngine.instance.log("--", false);
        }
        LogEngine.instance.newLine(false);
    }
}
