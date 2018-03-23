package gui.controller;

import field.Board;
import field.Cell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import main.AlgorithmParameters;

public class GuiController {
    private Board cellBoard;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button solveButton;

    @FXML
    private void initialize() {
        gridPane.setGridLinesVisible(false);
        solveButton.setOnAction(event -> launchSolver());

        cellBoard = new Board(AlgorithmParameters.instance.boardDimension);
        PuzzleInitializer initializer = new PuzzleInitializer(gridPane, cellBoard);
        initializer.setup();
    }

    @FXML
    private void launchSolver() {
        for (int x = 0; x < cellBoard.getWidth(); x++) {
            System.out.print(String.format("%2d ", x));
            for (int y = 0; y < cellBoard.getHeight(); y++) {
                Cell cell = cellBoard.get(x, y);
                if (cell.isAnyCircleRegistered()) {
                    int distance = cell.getCircle().getDistance();
                    if (distance >= 0) {
                        System.out.print(distance + " ");
                    } else {
                        System.out.print("O ");
                    }
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
}
