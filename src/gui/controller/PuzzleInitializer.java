package gui.controller;

import field.Board;
import field.Cell;
import field.Distance;
import field.MovableCircle;
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

        for (int x = 0; x < grid.getRowConstraints().size(); x++) {
            for (int y = 0; y < grid.getColumnConstraints().size(); y++) {
                StackPane basePane = new StackPane();
                stackPaneList.add(basePane);
                grid.add(basePane, y, x);
                GridPane.setValignment(basePane, VPos.CENTER);
                GridPane.setHalignment(basePane, HPos.CENTER);
                board.set(new Cell(), x, y);
            }
        }

        insertGridLines();
        insertCircles();
        insertRegionLines();

    }

    private void insertGridLines() {
        int gridWidth = grid.getRowConstraints().size();
        int gridHeight = grid.getColumnConstraints().size();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                addHorizontalLineToGridAt(x, y);
                addVerticalLineToGridAt(x, y);
            }
        }

        int outerIndex = gridWidth - 1;
        for (int i = 0; i < gridWidth; i++) {
            addHorizontalLineToGridAt(outerIndex, i, VPos.BOTTOM);
            addVerticalLineToGridAt(i, outerIndex, HPos.RIGHT);
        }
    }

    private void addVerticalLineToGridAt(int rowIndex, int columnIndex) {
        addVerticalLineToGridAt(rowIndex, columnIndex, HPos.LEFT);
    }

    private void addVerticalLineToGridAt(int rowIndex, int columnIndex, HPos pos) {
        Line newGridLine = new Line(0, -20, 0, 25);
        newGridLine.setStroke(Color.BLACK);
        newGridLine.setStrokeWidth(1);
        grid.add(newGridLine, columnIndex, rowIndex);
        GridPane.setHalignment(newGridLine, pos);
    }

    private void addHorizontalLineToGridAt(int rowIndex, int columnIndex) {
        addHorizontalLineToGridAt(rowIndex, columnIndex, VPos.TOP);
    }

    private void addHorizontalLineToGridAt(int rowIndex, int columnIndex, VPos pos) {
        Line newGridLine = new Line(-20, 0, 25, 0);
        newGridLine.setStroke(Color.BLACK);
        newGridLine.setStrokeWidth(1);
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

        board.get(rowIndex, columnIndex).registerCircle(new MovableCircle(distance));
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

    private void insertRegionLines() {

    }

}
