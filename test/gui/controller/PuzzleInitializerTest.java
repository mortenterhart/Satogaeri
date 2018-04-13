package gui.controller;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import main.AlgorithmParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import puzzle.Board;

public class PuzzleInitializerTest {

    private static GridPane gridPane;
    private static Board board;
    private static int boardDimension = AlgorithmParameters.instance.boardDimension;

    @BeforeAll
    public static void constructResources() {
        gridPane = new GridPane();
        buildRowAndColumnConstraints();

        board = new Board(boardDimension);

        PuzzleInitializer initializer = new PuzzleInitializer(gridPane, board);
        initializer.setup();
    }

    private static void buildRowAndColumnConstraints() {
        for (int i = 0; i < boardDimension; i++) {
            RowConstraints row = new RowConstraints();
            gridPane.getRowConstraints().add(row);
            ColumnConstraints column = new ColumnConstraints();
            gridPane.getColumnConstraints().add(column);
        }
    }

    @Test
    public void testGridPaneNotNull() {
        Assertions.assertNotNull(gridPane);
    }

    @Test
    public void testBoardNotNull() {
        Assertions.assertNotNull(board);
    }

    @Test
    public void testBoardRegionListNotEmpty() {
        Assertions.assertFalse(board.getRegions().isEmpty());
    }
}
