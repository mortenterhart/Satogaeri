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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import main.AlgorithmParameters;
import main.GUISettings;
import move.MoveProposal;
import puzzle.Board;
import puzzle.Cell;
import puzzle.FieldCircle;
import solver.DirectionMapper;
import solver.SatogaeriSolver;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuiController {
    private Board cellBoard;
    private IndexExpander expander;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button solveButton;

    @FXML
    private void initialize() {
        gridPane.setGridLinesVisible(false);
        solveButton.setOnAction(event -> launchSolver());

        cellBoard = new Board(AlgorithmParameters.instance.boardDimension);
        expander = new IndexExpander(cellBoard.getWidth());
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
        removeChildrenFromStackPanes();
        markInvariantRegions(cellBoard);
        paintCirclePath(cellBoard, chronologicalMoves);
        drawAllCircles(cellBoard);
    }

    private void drawAllCircles(Board cellBoard) {
        for (int y = 0; y < cellBoard.getWidth(); y++) {
            for (int x = 0; x < cellBoard.getHeight(); x++) {
                Cell currentCell = cellBoard.get(x, y);
                StackPane circlePane = getStackPaneAt(x, y);

                if (currentCell.isAnyCircleRegistered()) {
                    FieldCircle currentCircle = currentCell.getCircle();

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

    private void paintCirclePath(Board cellBoard, List<MoveProposal> chronologicalMoves) {
        for (MoveProposal move : chronologicalMoves) {
            DirectionMapper mapper = new DirectionMapper(move.getDirection(), cellBoard);
            Cell destinationCell = cellBoard.get(move.getMoveX(), move.getMoveY());
            Cell originCell = mapper.mapCellCoordinates(mapper.mapRelatedCoordinate(destinationCell) - move.getDistance().toIntValue() * mapper.mapLoopStep(), destinationCell);

            if (originCell != destinationCell) {
                StackPane originStackPane = getStackPaneAt(originCell.getGridX(), originCell.getGridY());
                double rectangleRotation = mapper.mapRectangleRotation();
                Pos rectangleOrientation = mapper.mapRectangleOrientation();
                Pos invertedOrientation = mapper.mapRectangleOrientationInverted();

                Rectangle visitedOriginRectangle = new Rectangle(25, 20, Color.LIGHTGRAY);
                visitedOriginRectangle.setStrokeWidth(2);
                visitedOriginRectangle.setStroke(Color.web("#a1a1a1"));
                visitedOriginRectangle.setRotate(rectangleRotation);

                originStackPane.getChildren().add(visitedOriginRectangle);
                StackPane.setAlignment(visitedOriginRectangle, rectangleOrientation);

                Circle originMarkerCircle = new Circle(GUISettings.circleRadius, Color.LIGHTGRAY);
                originMarkerCircle.setStrokeWidth(2);
                originMarkerCircle.setStroke(Color.web("#a1a1a1"));

                Label distanceLabel = new Label(String.valueOf(move.getDistance().toIntValue()));
                distanceLabel.setTextFill(Color.WHITE);
                distanceLabel.setAlignment(Pos.CENTER);
                distanceLabel.setTextAlignment(TextAlignment.CENTER);
                distanceLabel.setFont(new Font(GUISettings.distanceLabelFontSize));

                originStackPane.getChildren().add(originMarkerCircle);
                originStackPane.getChildren().add(distanceLabel);

                int relatedOriginCoordinate = mapper.mapRelatedCoordinateWithStep(originCell);
                int relatedDestinationCoordinate = mapper.mapRelatedCoordinate(destinationCell);
                for (int i = relatedOriginCoordinate;
                     mapper.mapMoveConditionExclusive(i, relatedDestinationCoordinate);
                     i += mapper.mapLoopStep()) {

                    StackPane pathPane;
                    switch (move.getDirection()) {
                        case LEFT:
                        case RIGHT:
                            pathPane = getStackPaneAt(i, originCell.getGridY());
                            break;

                        case UP:
                        case DOWN:
                            pathPane = getStackPaneAt(originCell.getGridX(), i);
                            break;

                        default:
                            throw new DirectionMapper.InvalidDirectionException("illegal direction " +
                                    move.getDirection() + " detected");
                    }

                    Rectangle visitedPathRectangle = new Rectangle(GUISettings.gridCellWidth, 20, Color.LIGHTGRAY);
                    visitedPathRectangle.setStrokeWidth(2);
                    visitedPathRectangle.setStroke(Color.web("#a1a1a1"));
                    visitedPathRectangle.setRotate(rectangleRotation);

                    pathPane.getChildren().add(visitedPathRectangle);
                }

                Rectangle visitedDestinationRectangle = new Rectangle(25, 20, Color.LIGHTGRAY);
                visitedDestinationRectangle.setStrokeWidth(2);
                visitedDestinationRectangle.setStroke(Color.web("#a1a1a1"));
                visitedDestinationRectangle.setRotate(rectangleRotation);

                StackPane destinationStackPane = getStackPaneAt(destinationCell.getGridX(), destinationCell.getGridY());
                destinationStackPane.getChildren().add(visitedDestinationRectangle);

                StackPane.setAlignment(visitedDestinationRectangle, invertedOrientation);
            }
        }
    }

    private void markInvariantRegions(Board cellBoard) {
        for (int y = 0; y < cellBoard.getWidth(); y++) {
            for (int x = 0; x < cellBoard.getHeight(); x++) {
                Cell currentCell = cellBoard.get(x, y);
                StackPane cellPane = getStackPaneAt(x, y);

                if (currentCell.isInvariant()) {
                    Rectangle invariantColorLayer = new Rectangle(GUISettings.gridCellWidth, GUISettings.gridCellHeight);
                    invariantColorLayer.setFill(Color.web("#1eff3e"));

                    cellPane.getChildren().add(invariantColorLayer);
                }
            }
        }
    }

    private void removeChildrenFromStackPanes() {
        for (Node gridChild : gridPane.getChildren()) {
            if (gridChild instanceof StackPane) {
                StackPane stackPane = (StackPane) gridChild;
                stackPane.getChildren().removeIf(node -> node instanceof Circle || node instanceof Label ||
                        node instanceof Rectangle);
            }
        }
    }

    private StackPane getStackPaneAt(int x, int y) {
        return (StackPane) gridPane.getChildren().get(expander.expand(x, y));
    }
}
