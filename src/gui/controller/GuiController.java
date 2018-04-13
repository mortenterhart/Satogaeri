package gui.controller;

import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logging.LogEngine;
import main.AlgorithmParameters;
import main.Configuration;
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

    private SatogaeriSolver solverTask;

    private Stage mainStage;
    private HostServices hostServices;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button solveButton;

    @FXML
    private Button resetSimulationButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button resetMoveSliderButton;

    @FXML
    private Button resetTriggerTimeButton;

    @FXML
    private Button resetColorsButton;

    @FXML
    private Slider triggerTimeSlider;

    @FXML
    private Slider moveTimeSlider;

    @FXML
    private ColorPicker invariantColorSelector;

    @FXML
    private ColorPicker triggeringColorSelector;

    @FXML
    private Rectangle invariantCellColorReference;

    @FXML
    private Circle triggeringCellColorReference;

    @FXML
    private Label statusLabel;

    @FXML
    private Hyperlink informationLink;

    @FXML
    private void initialize() {
        gridPane.setGridLinesVisible(false);
        solveButton.setOnAction(event -> launchSolver());
        resetSimulationButton.setOnAction(event -> resetBoard());
        closeButton.setOnAction(event -> mainStage.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        resetSimulationButton.setDisable(true);

        cellBoard = new Board(AlgorithmParameters.instance.boardDimension);
        expander = new IndexExpander(cellBoard.getWidth());
        PuzzleInitializer initializer = new PuzzleInitializer(gridPane, cellBoard);
        initializer.setup();

        observeColorSelectors();
        observeTimeSliders();
        addActionsToResetButtons();
        addHyperlinkEvent();
    }

    private void observeColorSelectors() {
        invariantCellColorReference.setFill(GUISettings.invariantRegionColor);
        invariantColorSelector.setValue(GUISettings.invariantRegionColor);
        invariantColorSelector.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                GUISettings.setInvariantRegionColor(newValue);
                invariantCellColorReference.setFill(newValue);
            }
        });

        triggeringCellColorReference.setStroke(GUISettings.circleOutlineColor);
        triggeringColorSelector.setValue(GUISettings.circleOutlineColor);
        triggeringColorSelector.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                GUISettings.setCircleOutlineColor(newValue);
                triggeringCellColorReference.setStroke(newValue);
            }
        });
    }

    private void observeTimeSliders() {
        moveTimeSlider.setValue(GUISettings.sleepInterval);
        moveTimeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                GUISettings.setSleepInterval(newValue.longValue());
            }
        });

        triggerTimeSlider.setValue(GUISettings.circleHighlightingTime);
        triggerTimeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                GUISettings.setCircleHighlightingTime(newValue.longValue());
            }
        });
    }

    private void addActionsToResetButtons() {
        resetMoveSliderButton.setOnAction(event -> {
            GUISettings.resetSleepInterval();
            moveTimeSlider.setValue(GUISettings.sleepInterval);
        });

        resetTriggerTimeButton.setOnAction(event -> {
            GUISettings.resetCircleHighlightingTime();
            triggerTimeSlider.setValue(GUISettings.circleHighlightingTime);
        });

        resetColorsButton.setOnAction(event -> {
            GUISettings.resetInvariantRegionColor();
            invariantColorSelector.setValue(GUISettings.invariantRegionColor);
            GUISettings.resetCircleOutlineColor();
            triggeringColorSelector.setValue(GUISettings.circleOutlineColor);
        });
    }

    private void addHyperlinkEvent() {
        informationLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hostServices.showDocument(Configuration.instance.informationHyperlinkURL);
            }
        });
    }

    public void enableTaskCancelling() {
        mainStage.setOnCloseRequest(event -> {
            mainStage.close();
            if (solverTask != null && !solverTask.isCancelled()) {
                LogEngine.instance.logln("--> Cancelling task SatogaeriSolver");
                solverTask.enableCancelling();
            }
            LogEngine.instance.close();
        });
    }

    @FXML
    private void launchSolver() {
        solveButton.setDisable(true);

        solverTask = new SatogaeriSolver(cellBoard);
        solverTask.setController(this);
        solverTask.setResetButton(resetSimulationButton);
        solverTask.setStatusLabel(statusLabel);

        ExecutorService solverService = Executors.newCachedThreadPool();
        solverService.submit(solverTask);
    }

    @FXML
    private void resetBoard() {
        solveButton.setDisable(false);
        gridPane.getChildren().clear();

        initialize();
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

                if (currentCell.hasAnyCircleRegistered()) {
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
                    invariantColorLayer.setFill(GUISettings.invariantRegionColor);

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

    public void highlightCircleLine(int x, int y, Color color) {
        StackPane circlePane = getStackPaneAt(x, y);

        Circle registeredCircle = getCircleFromStackPane(circlePane);
        if (registeredCircle != null) {
            registeredCircle.setStroke(color);
        }
    }

    private Circle getCircleFromStackPane(StackPane pane) {
        for (Node child : pane.getChildren()) {
            if (child instanceof Circle) {
                return (Circle) child;
            }
        }

        return null;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
