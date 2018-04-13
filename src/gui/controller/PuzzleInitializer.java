package gui.controller;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import main.AlgorithmParameters;
import main.GUISettings;
import puzzle.Board;
import puzzle.Cell;
import puzzle.Distance;
import puzzle.FieldCircle;
import puzzle.Region;

import java.util.ArrayList;
import java.util.List;

public class PuzzleInitializer {

    private Board board;
    private GridPane grid;
    private List<StackPane> stackPaneList;

    public PuzzleInitializer(GridPane grid, Board board) {
        this.grid = grid;
        this.board = board;
        this.stackPaneList = new ArrayList<>(grid.getChildren().size());
    }

    public void setup() {
        if (grid == null || board == null) {
            throw new IllegalArgumentException("grid or board is null");
        }

        if (grid.getRowConstraints().size() != board.getWidth() ||
            grid.getColumnConstraints().size() != board.getHeight()) {
            throw new IllegalStateException("graphical grid pane has different dimensions " +
                                            "than board data structure");
        }

        prepareGridWithStackPanes();
        insertGridLines();
        insertCircles();
        constructRegions();
        insertRegionLines();
    }

    private void prepareGridWithStackPanes() {
        for (int row = 0; row < grid.getRowConstraints().size(); row++) {
            for (int column = 0; column < grid.getColumnConstraints().size(); column++) {
                StackPane basePane = new StackPane();
                stackPaneList.add(basePane);
                grid.add(basePane, column, row);
                GridPane.setValignment(basePane, VPos.CENTER);
                GridPane.setHalignment(basePane, HPos.CENTER);
                board.set(new Cell(row, column), row, column);
            }
        }
    }

    private void insertGridLines() {
        // Add thin borders to the top and left of each cell
        // so that each grid line is exactly processed once.
        // This would look like this:
        //     ____
        //    |
        //    |
        //
        int gridWidth = grid.getRowConstraints().size();
        int gridHeight = grid.getColumnConstraints().size();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                addHorizontalLineToGridAt(x, y);
                addVerticalLineToGridAt(x, y);
            }
        }

        // Connect all corners on the very right and bottom
        // line of the grid pane that were not visited yet
        int outerIndex = gridWidth - 1;
        for (int i = 0; i < gridWidth; i++) {
            addHorizontalLineToGridAt(outerIndex, i, VPos.BOTTOM, true);
            addVerticalLineToGridAt(i, outerIndex, HPos.RIGHT, true);
        }
    }

    private void addVerticalLineToGridAt(int rowIndex, int columnIndex) {
        addVerticalLineToGridAt(rowIndex, columnIndex, HPos.LEFT, false);
    }

    private void addVerticalLineToGridAt(int rowIndex, int columnIndex, HPos pos, boolean setTranslate) {
        Line newGridLine = new Line(0, -20, 0, 25);
        newGridLine.setStroke(Color.BLACK);
        newGridLine.setStrokeWidth(1);
        if (setTranslate) {
            newGridLine.setTranslateX(2);
        }

        grid.add(newGridLine, columnIndex, rowIndex);
        GridPane.setHalignment(newGridLine, pos);
    }

    private void addHorizontalLineToGridAt(int rowIndex, int columnIndex) {
        addHorizontalLineToGridAt(rowIndex, columnIndex, VPos.TOP, false);
    }

    private void addHorizontalLineToGridAt(int rowIndex, int columnIndex, VPos pos, boolean setTranslate) {
        Line newGridLine = new Line(-20, 0, 25, 0);
        newGridLine.setStroke(Color.BLACK);
        newGridLine.setStrokeWidth(1);
        if (setTranslate) {
            newGridLine.setTranslateY(2);
        }

        grid.add(newGridLine, columnIndex, rowIndex);
        GridPane.setValignment(newGridLine, pos);
    }

    private void insertCircles() {
        if (!haveCorrectDimensions()) {
            throw new IllegalStateException("either the grid or the board provides other dimensions " +
                                            "than " + AlgorithmParameters.instance.boardDimension);
        }

        addCircleToStackPaneAt(0, 5, 1);
        addCircleToStackPaneAt(0, 8);
        addCircleToStackPaneAt(1, 0);
        addCircleToStackPaneAt(1, 2, 0);
        addCircleToStackPaneAt(1, 3, 1);
        addCircleToStackPaneAt(2, 3);
        addCircleToStackPaneAt(2, 4, 2);
        addCircleToStackPaneAt(3, 0, 2);
        addCircleToStackPaneAt(3, 2, 1);
        addCircleToStackPaneAt(3, 6, 3);
        addCircleToStackPaneAt(3, 9, 2);
        addCircleToStackPaneAt(4, 3, 2);
        addCircleToStackPaneAt(4, 6, 0);
        addCircleToStackPaneAt(5, 3, 1);
        addCircleToStackPaneAt(5, 4);
        addCircleToStackPaneAt(5, 5, 2);
        addCircleToStackPaneAt(5, 7, 2);
        addCircleToStackPaneAt(6, 0, 2);
        addCircleToStackPaneAt(6, 6, 1);
        addCircleToStackPaneAt(6, 9, 2);
        addCircleToStackPaneAt(7, 4, 0);
        addCircleToStackPaneAt(7, 6);
        addCircleToStackPaneAt(8, 2, 0);
        addCircleToStackPaneAt(8, 3, 5);
        addCircleToStackPaneAt(9, 4, 3);
        addCircleToStackPaneAt(9, 5, 2);
    }

    private void addCircleToStackPaneAt(int rowIndex, int columnIndex) {
        addCircleToStackPaneAt(rowIndex, columnIndex, Distance.ANY);
    }

    private void addCircleToStackPaneAt(int rowIndex, int columnIndex, int distance) {
        addCircleToStackPaneAt(rowIndex, columnIndex, new Distance(distance));
    }

    private void addCircleToStackPaneAt(int rowIndex, int columnIndex, Distance distance) {
        Circle gridCircle = new Circle(GUISettings.circleRadius);
        gridCircle.setFill(Color.WHITE);
        gridCircle.setStroke(Color.BLACK);
        gridCircle.setStrokeWidth(2);

        Label distanceLabel = new Label(String.valueOf(distance.toIntValue()));
        distanceLabel.setAlignment(Pos.CENTER);
        distanceLabel.setTextAlignment(TextAlignment.CENTER);
        distanceLabel.setFont(new Font(GUISettings.distanceLabelFontSize));

        if (distance.isAny()) {
            distanceLabel.setVisible(false);
        }

        int listIndex = convertToConsecutiveIndex(rowIndex, columnIndex);
        StackPane gridStackPane = stackPaneList.get(listIndex);
        gridStackPane.getChildren().add(gridCircle);
        gridStackPane.getChildren().add(distanceLabel);

        Cell circleCell = board.get(columnIndex, rowIndex);
        circleCell.registerCircle(new FieldCircle(distance));
        circleCell.setVisited(true);
    }

    private int convertToConsecutiveIndex(int rowIndex, int columnIndex) {
        return rowIndex * AlgorithmParameters.instance.boardDimension + columnIndex;
    }

    private boolean haveCorrectDimensions() {
        return grid.getRowConstraints().size() == AlgorithmParameters.instance.boardDimension &&
               grid.getColumnConstraints().size() == AlgorithmParameters.instance.boardDimension &&
               board.getWidth() == AlgorithmParameters.instance.boardDimension &&
               board.getHeight() == AlgorithmParameters.instance.boardDimension;
    }

    private void constructRegions() {
        if (!haveCorrectDimensions()) {
            throw new IllegalStateException("either the grid or the board provides other dimensions " +
                                            "than " + AlgorithmParameters.instance.boardDimension);
        }

        int[][] region1 = {
            {0, 0},
            {1, 0},
            {0, 1},
            {1, 1},
            {2, 1},
            {2, 2}
        };

        int[][] region2 = {
            {2, 0},
            {3, 0},
            {3, 1}
        };

        int[][] region3 = {
            {4, 0},
            {5, 0},
            {6, 0},
            {7, 0},
            {8, 0},
            {4, 1},
            {6, 1}
        };

        int[][] region4 = {
            {9, 0},
            {7, 1},
            {8, 1},
            {9, 1},
            {8, 2},
            {9, 2}
        };

        int[][] region5 = {
            {5, 1},
            {5, 2},
            {5, 3},
            {6, 3}
        };

        int[][] region6 = {
            {0, 2},
            {0, 3},
            {1, 3}
        };

        int[][] region7 = {
            {1, 2}
        };

        int[][] region8 = {
            {3, 2},
            {4, 2},
            {3, 3}
        };

        int[][] region9 = {
            {6, 2},
            {7, 2},
            {7, 3},
            {8, 3},
            {9, 3}
        };

        int[][] region10 = {
            {2, 3},
            {2, 4}
        };

        int[][] region11 = {
            {4, 3},
            {3, 4},
            {4, 4},
            {5, 4}
        };

        int[][] region12 = {
            {0, 4},
            {1, 4},
            {0, 5}
        };

        int[][] region13 = {
            {6, 4},
            {7, 4},
            {8, 4}
        };

        int[][] region14 = {
            {9, 4},
            {8, 5},
            {9, 5}
        };

        int[][] region15 = {
            {1, 5},
            {2, 5},
            {3, 5}
        };

        int[][] region16 = {
            {4, 5},
            {5, 5},
            {6, 5},
            {5, 6}
        };

        int[][] region17 = {
            {7, 5},
            {7, 6}
        };

        int[][] region18 = {
            {0, 6},
            {1, 6},
            {2, 6},
            {2, 7},
            {3, 7}
        };

        int[][] region19 = {
            {3, 6},
            {4, 6},
            {4, 7},
            {4, 8}
        };

        int[][] region20 = {
            {6, 6},
            {5, 7},
            {6, 7}
        };

        int[][] region21 = {
            {8, 6},
            {9, 6},
            {9, 7}
        };

        int[][] region22 = {
            {0, 7},
            {1, 7},
            {0, 8},
            {1, 8},
            {2, 8},
            {0, 9}
        };

        int[][] region23 = {
            {7, 7},
            {7, 8},
            {8, 8},
            {9, 8},
            {8, 9},
            {9, 9}
        };

        int[][] region24 = {
            {8, 7}
        };

        int[][] region25 = {
            {3, 8},
            {5, 8},
            {1, 9},
            {2, 9},
            {3, 9},
            {4, 9},
            {5, 9}
        };

        int[][] region26 = {
            {6, 8},
            {6, 9},
            {7, 9}
        };

        addRegionToCellBoard(region1);
        addRegionToCellBoard(region2);
        addRegionToCellBoard(region3);
        addRegionToCellBoard(region4);
        addRegionToCellBoard(region5);
        addRegionToCellBoard(region6);
        addRegionToCellBoard(region7);
        addRegionToCellBoard(region8);
        addRegionToCellBoard(region9);
        addRegionToCellBoard(region10);
        addRegionToCellBoard(region11);
        addRegionToCellBoard(region12);
        addRegionToCellBoard(region13);
        addRegionToCellBoard(region14);
        addRegionToCellBoard(region15);
        addRegionToCellBoard(region16);
        addRegionToCellBoard(region17);
        addRegionToCellBoard(region18);
        addRegionToCellBoard(region19);
        addRegionToCellBoard(region20);
        addRegionToCellBoard(region21);
        addRegionToCellBoard(region22);
        addRegionToCellBoard(region23);
        addRegionToCellBoard(region24);
        addRegionToCellBoard(region25);
        addRegionToCellBoard(region26);
    }

    private void addRegionToCellBoard(int[][] region) {
        checkRegionAlreadyRegisteredCells(region);

        Region boardRegion = new Region();
        for (int[] regionCell : region) {
            boardRegion.addCell(board.get(regionCell[0], regionCell[1]));
        }

        board.addRegion(boardRegion);
    }

    private void checkRegionAlreadyRegisteredCells(int[][] region) {
        // First check if the region contains cells owned by
        // other regions. In that case throw an exception.
        for (Region registeredRegion : board.getRegions()) {
            for (int[] newRegionCell : region) {
                if (registeredRegion.containsCell(newRegionCell)) {
                    throw new IllegalStateException(String.format("trying to add cell (%d, %d) " +
                                                                  "to new region while this cell is already registered in region %s", newRegionCell[0],
                                                                  newRegionCell[1], registeredRegion.toString()));
                }
            }
        }
    }

    private void insertRegionLines() {
        if (!haveCorrectDimensions()) {
            throw new IllegalStateException("either the grid or the board provides other dimensions " +
                                            "than " + AlgorithmParameters.instance.boardDimension);
        }

        int[][] verticalLineIndices = {
            {0, 2},
            {0, 4},
            {0, 9},
            {1, 3},
            {1, 4},
            {1, 5},
            {1, 6},
            {1, 7},
            {2, 1},
            {2, 2},
            {2, 3},
            {2, 5},
            {2, 6},
            {2, 8},
            {3, 2},
            {3, 3},
            {3, 4},
            {3, 5},
            {3, 7},
            {4, 2},
            {4, 3},
            {4, 6},
            {4, 9},
            {5, 1},
            {5, 4},
            {5, 7},
            {5, 8},
            {6, 3},
            {6, 5},
            {6, 6},
            {6, 7},
            {6, 8},
            {7, 2},
            {7, 4},
            {7, 5},
            {7, 7},
            {7, 8},
            {7, 9},
            {8, 3},
            {8, 4},
            {8, 5},
            {8, 6},
            {8, 7},
            {9, 1},
            {9, 6},
            {9, 8}};

        for (int[] verticalCoordinates : verticalLineIndices) {
            addVerticalRegionLineToGrid(verticalCoordinates[0], verticalCoordinates[1]);
        }

        int[][] horizontalLineIndices = {
            {1, 2},
            {1, 5},
            {1, 7},
            {1, 8},
            {2, 0},
            {2, 1},
            {2, 3},
            {2, 4},
            {2, 6},
            {2, 7},
            {3, 1},
            {3, 2},
            {3, 4},
            {3, 6},
            {3, 8},
            {3, 9},
            {4, 0},
            {4, 1},
            {4, 3},
            {4, 5},
            {4, 6},
            {4, 7},
            {4, 8},
            {4, 9},
            {5, 1},
            {5, 2},
            {5, 3},
            {5, 4},
            {5, 5},
            {5, 6},
            {5, 7},
            {5, 8},
            {6, 0},
            {6, 1},
            {6, 2},
            {6, 3},
            {6, 4},
            {6, 6},
            {6, 8},
            {6, 9},
            {7, 0},
            {7, 1},
            {7, 5},
            {7, 3},
            {7, 7},
            {7, 8},
            {8, 2},
            {8, 3},
            {8, 5},
            {8, 6},
            {8, 8},
            {8, 9},
            {9, 1},
            {9, 2},
            {9, 4},
            {9, 7}};

        for (int[] horizontalCoordinates : horizontalLineIndices) {
            addHorizontalRegionLineToGrid(horizontalCoordinates[0], horizontalCoordinates[1]);
        }
    }

    private void addHorizontalRegionLineToGrid(int rowIndex, int columnIndex) {
        Line regionLine = new Line(-20, 0, 24, 0);
        regionLine.setStroke(Color.BLACK);
        regionLine.setStrokeWidth(4);
        regionLine.setTranslateY(-1.5);
        grid.add(regionLine, columnIndex, rowIndex);
        GridPane.setValignment(regionLine, VPos.TOP);
    }

    private void addVerticalRegionLineToGrid(int rowIndex, int columnIndex) {
        Line regionLine = new Line(0, -20, 0, 24);
        regionLine.setStroke(Color.BLACK);
        regionLine.setStrokeWidth(4);
        regionLine.setTranslateX(-1.5);
        grid.add(regionLine, columnIndex, rowIndex);
        GridPane.setHalignment(regionLine, HPos.LEFT);
    }
}
