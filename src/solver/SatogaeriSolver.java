package solver;

import gui.controller.GuiController;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import logging.LogEngine;
import main.GUISettings;
import move.*;
import puzzle.Board;
import puzzle.Cell;
import puzzle.Distance;
import puzzle.FieldCircle;

import java.util.ArrayList;
import java.util.List;

public class SatogaeriSolver implements Runnable {
    private Board cellBoard;
    private List<FieldCircle> boardCircles;
    private GuiController controller;
    private volatile boolean isCancelled = false;

    private Button resetSimulationButton;
    private Label statusLabel;

    private AssociationInducer inducer;

    private ICircleMover determinedDistanceMover;
    private ICircleMover anyDistanceMover;

    private List<MoveProposal> chronologicalMoves;

    public SatogaeriSolver(Board cellBoard) {
        this.cellBoard = cellBoard;
        this.boardCircles = cellBoard.getAllRegisteredCircles();
        this.inducer = new AssociationInducer(cellBoard);
        this.determinedDistanceMover = new DeterminedDistanceCircleMover(cellBoard);
        this.anyDistanceMover = new AnyDistanceCircleMover(cellBoard);
        this.chronologicalMoves = new ArrayList<>();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        LogEngine.instance.logln("### Starting thread SatogaeriSolver");
        findSolution();

        activateResetButton();
        updateStatus("Simulation found solution and terminated successfully", true);
        LogEngine.instance.logln("### Thread SatogaeriSolver terminates here");
    }

    private void findSolution() {
        markZeroDistanceCellsInvariant();

        if (isCancelled) {
            return;
        }

        while (hasCirclesWithSolelySolution()) {
            moveCirclesWithSolelySolution();

            if (isCancelled) {
                LogEngine.instance.logln("!! ######## CAUGHT INTERRUPT ######### !!");
                LogEngine.instance.logln("  --> Process will terminate");
                return;
            }
        }
    }

    public void enableCancelling() {
        isCancelled = true;
    }

    private List<MoveProposal> fetchCircleMoves(FieldCircle circle) {
        checkCircleInvariant(circle);

        Cell circleCell = inducer.getCellBy(circle);
        if (circle.hasAnyDistance()) {
            return anyDistanceMover.identifyAllPossibleMoves(circleCell);
        }

        return determinedDistanceMover.identifyAllPossibleMoves(circleCell);
    }

    private void checkCircleInvariant(FieldCircle circle) {
        if (circle.isInvariant()) {
            throw new IllegalStateException("circle " + circle + " is invariant and cannot move");
        }
    }

    private void markZeroDistanceCellsInvariant() {
        LogEngine.instance.logln("--- Marking all circles with zero distance as invariant");
        for (FieldCircle circle : boardCircles) {
            Cell originCell = inducer.getCellBy(circle);
            if (circle != null && circle.hasZeroDistance() && !originCell.isInvariant()) {
                LogEngine.instance.logln("  > Circle " + circle + " on position " + cellToString(originCell) + " cannot move and gets invariant");
                inducer.getRegionBy(circle).setFinal(true);
                circle.setInvariant(true);
                updateStatus("Marking zero distance circle " + cellToString(originCell) + " and its region as invariant", false);
                highlightCircleAttempt(originCell);
                insertChronologicalMove(new MoveProposal(originCell.getGridX(), originCell.getGridY(),
                        circle, MoveDirection.LEFT, new Distance(0)));
            }

            if (isCancelled) {
                return;
            }
        }
    }

    private void moveCirclesWithSolelySolution() {
        LogEngine.instance.logln("Moving circles with solely solution (= only one possible move)");
        for (FieldCircle circle : boardCircles) {
            Cell originCell = inducer.getCellBy(circle);
            if (originCell != null && !circle.isInvariant()) {
                LogEngine.instance.logln("  > Circle " + circle + " is not invariant");
                List<MoveProposal> possibleMoves = fetchCircleMoves(circle);

                LogEngine.instance.newLine(true);
                LogEngine.instance.logln("  > Listing all possible moves for circle on cell " + cellToString(originCell));
                for (MoveProposal move : possibleMoves) {
                    LogEngine.instance.logln("   " + move);
                }
                LogEngine.instance.newLine(true);

                if (isSolelySolution(originCell, possibleMoves)) {
                    LogEngine.instance.logln("   ==> SUCCESS! Circle only has one possible move:");
                    MoveProposal move = possibleMoves.get(0);
                    LogEngine.instance.logln("       " + move);

                    highlightCircleAttempt(originCell);

                    Cell destinationCell = cellBoard.get(move.getMoveX(), move.getMoveY());
                    LogEngine.instance.logln("   destinationCell is: " + cellToString(destinationCell));
                    moveCircle(originCell, destinationCell);
                    markCellsVisited(originCell, move);
                    updateStatus("Moving circle from " + cellToString(originCell) + " to " + cellToString(destinationCell)
                            + " with distance '" + circle.getDistance().toString() + "' and marking path as visited", false);

                    LogEngine.instance.logln("   > Marking region and circle as invariant");
                    inducer.getRegionBy(destinationCell).setFinal(true);
                    circle.setInvariant(true);

                    insertChronologicalMove(move);
                }
            }

            if (isCancelled) {
                return;
            }

            cellBoard.dumpCellBoard();
        }
    }

    private boolean isSolelySolution(Cell originCell, List<MoveProposal> possibleMoves) {
        FieldCircle circle = originCell.getCircle();
        if (circle.hasAnyDistance()) {
            return possibleMoves.size() == 1 && anyDistanceMover.isDistinctMove(originCell, possibleMoves.get(0));
        }

        return possibleMoves.size() == 1 && determinedDistanceMover.isDistinctMove(originCell, possibleMoves.get(0));
    }

    private boolean hasCirclesWithSolelySolution() {
        for (FieldCircle circle : boardCircles) {
            if (circle != null && !circle.hasAnyDistance() && !circle.isInvariant()) {
                Cell originCell = inducer.getCellBy(circle);
                List<MoveProposal> proposals = fetchCircleMoves(circle);

                if (isSolelySolution(originCell, proposals)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void insertChronologicalMove(MoveProposal move) {
        LogEngine.instance.logln("  Inserting move into chronological moves and refreshing GUI");
        chronologicalMoves.add(move);
        refreshGUIBoard();
        waitInterval(GUISettings.sleepInterval);
    }

    private void highlightCircleAttempt(Cell cell) {
        if (!cell.hasAnyCircleRegistered()) {
            throw new IllegalStateException("attempting to highlight circle line, but cell " +
                    cellToString(cell) + " does not contain any circle");
        }

        refreshGUI(() ->
                controller.highlightCircleLine(cell.getGridX(), cell.getGridY(), GUISettings.circleOutlineColor)
        );

        waitInterval(GUISettings.circleHighlightingTime);
    }

    private void updateStatus(String message, boolean markGreen) {
        refreshGUI(() -> {
            statusLabel.setText("Status: " + message);
            statusLabel.setTextFill(Color.BLACK);
            if (markGreen) {
                statusLabel.setTextFill(Color.GREEN);
            }
        });
    }

    private void refreshGUIBoard() {
        refreshGUI(() -> controller.drawBoard(cellBoard, chronologicalMoves));
    }

    private void activateResetButton() {
        refreshGUI(() -> resetSimulationButton.setDisable(false));
    }

    private void refreshGUI(Runnable runnable) {
        Platform.runLater(runnable);
    }

    private void moveCircle(Cell origin, Cell destination) {
        checkOriginAndDestinationCells(origin, destination);

        LogEngine.instance.logln("   ... Moving circle from " + cellToString(origin) + " to " + cellToString(destination));
        FieldCircle movedCircle = origin.getCircle();
        origin.detachCircle();
        destination.registerCircle(movedCircle);
    }

    private void markCellsVisited(Cell origin, MoveProposal move) {
        // Make move and mark all cells on the way as visited
        DirectionMapper mapper = new DirectionMapper(move.getDirection(), cellBoard);
        int distance = move.getMovedCircle().getDistance().toIntValue();
        int destinationIndex = mapper.mapRelatedCoordinate(origin) + distance * mapper.mapLoopStep();

        for (int i = mapper.mapRelatedCoordinate(origin);
             mapper.mapMoveConditionInclusive(i, destinationIndex);
             i += mapper.mapLoopStep()) {

            Cell visitedCell = mapper.mapCellCoordinates(i, origin);
            visitedCell.setVisited(true);
            LogEngine.instance.logln("  Marking cell " + cellToString(visitedCell) + " as visited");
            LogEngine.instance.logln("    -> " + visitedCell);
        }
    }

    private void waitInterval(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            //if (isCancelled()) {
            //    cancellingRoutine();
            // }
        }
    }

    private void checkOriginAndDestinationCells(Cell origin, Cell destination) {
        if (origin == null || destination == null) {
            throw new NullPointerException("either origin or destination is null");
        }
    }

    public void setController(GuiController controller) {
        this.controller = controller;
    }

    private String cellToString(Cell cell) {
        if (cell != null) {
            return "[" + cell.getGridX() + ", " + cell.getGridY() + "]";
        }

        return null;
    }

    public void setResetButton(Button resetSimulationButton) {
        this.resetSimulationButton = resetSimulationButton;
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
