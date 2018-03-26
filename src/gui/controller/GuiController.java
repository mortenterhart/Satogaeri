package gui.controller;

import puzzle.Board;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import main.AlgorithmParameters;
import solver.SatogaeriSolver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        SatogaeriSolver solver = new SatogaeriSolver(cellBoard);
        solver.valueProperty().addListener(new ChangeListener<Board>() {
            @Override
            public void changed(ObservableValue<? extends Board> observable, Board oldValue, Board newValue) {

            }
        });

        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(solver);
    }
}
