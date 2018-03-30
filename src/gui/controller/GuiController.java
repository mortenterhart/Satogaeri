package gui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import main.AlgorithmParameters;
import main.GUISettings;
import move.MoveProposal;
import puzzle.Board;
import puzzle.Cell;
import puzzle.FieldCircle;
import solver.SatogaeriSolver;

import java.util.List;
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
        solver.setController(this);
        solver.valueProperty().addListener(new ChangeListener<Board>() {
            @Override
            public void changed(ObservableValue<? extends Board> observable, Board oldValue, Board newValue) {
                System.out.println("Value of board changed");
            }
        });

        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(solver);
    }

    public void drawBoard(Board cellBoard, List<MoveProposal> chronologicalMoves) {
        for (Node gridChild : gridPane.getChildren()) {
            if (gridChild instanceof StackPane) {
                StackPane stackPane = (StackPane) gridChild;
                stackPane.getChildren().removeIf(node -> node instanceof Circle || node instanceof Label);
            }
        }

        for (int y = 0; y < cellBoard.getWidth(); y++) {
            for (int x = 0; x < cellBoard.getHeight(); x++) {
                Cell currentCell = cellBoard.get(x, y);
                if (currentCell.isAnyCircleRegistered()) {
                    FieldCircle currentCircle = currentCell.getCircle();
                    StackPane circlePane = (StackPane) gridPane.getChildren().get(y * cellBoard.getWidth() + x);

                    Circle stackCircle = new Circle(GUISettings.circleRadius);
                    stackCircle.setFill(Color.WHITE);
                    stackCircle.setStroke(Color.BLACK);
                    stackCircle.setStrokeWidth(2);

                    Label distanceLabel = new Label(String.valueOf(currentCircle.getDistance().toIntValue()));
                    distanceLabel.setAlignment(Pos.CENTER);
                    distanceLabel.setTextAlignment(TextAlignment.CENTER);
                    distanceLabel.setFont(new Font(GUISettings.distanceLabelFontSize));

                    if (currentCircle.hasAnyDistance()) {
                        distanceLabel.setVisible(false);
                    }

                    circlePane.getChildren().add(stackCircle);
                    circlePane.getChildren().add(distanceLabel);
                }
            }
        }
    }
}
