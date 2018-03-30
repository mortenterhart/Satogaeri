package solver;

import gui.controller.GuiController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import logging.LogEngine;
import main.GUISettings;
import move.AnyDistanceCircleMover;
import move.DeterminedDistanceCircleMover;
import move.ICircleMover;
import move.MoveDirection;
import move.MoveProposal;
import puzzle.Board;
import puzzle.Cell;
import puzzle.Distance;
import puzzle.FieldCircle;

import java.util.ArrayList;
import java.util.List;

public class SatogaeriSolver extends Task<Board> {
    private Board cellBoard;
    private List<FieldCircle> boardCircles;
    private GuiController controller;

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
     * Invoked when the Task is executed, the call method must be overridden and
     * implemented by subclasses. The call method actually performs the
     * background thread logic. Only the updateProgress, updateMessage, updateValue and
     * updateTitle methods of Task may be called from code within this method.
     * Any other interaction with the Task from the background thread will result
     * in runtime exceptions.
     *
     * @return The result of the background work, if any.
     * @throws Exception an unhandled exception which occurred during the
     *                   background operation
     */
    @Override
    protected Board call() throws Exception {
        findSolution();

        this.exceptionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.printStackTrace();
            }
        });

        LogEngine.instance.close();
        return cellBoard;
    }

    private void findSolution() {
        markZeroDistanceCellsInvariant();

        while (hasCirclesWithSolelySolution()) {
            moveCirclesWithSolelySolution();
        }
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
        for (FieldCircle circle : boardCircles) {
            Cell originCell = inducer.getCellBy(circle);
            if (circle != null && circle.hasZeroDistance() && !originCell.isInvariant()) {
                inducer.getRegionBy(circle).setFinal(true);
                circle.setInvariant(true);
                highlightCircleAttempt(originCell);
                insertChronologicalMove(new MoveProposal(originCell.getGridX(), originCell.getGridY(),
                        circle, MoveDirection.LEFT, new Distance(0)));
            }
        }
    }

    private void moveCirclesWithSolelySolution() {
        for (FieldCircle circle : boardCircles) {
            Cell originCell = inducer.getCellBy(circle);
            LogEngine.instance.log("Cell " + cellToString(originCell) + " has next circle registered");
            if (originCell != null && !circle.isInvariant()) {
                LogEngine.instance.log("   Circle " + circle + " is not invariant");
                List<MoveProposal> possibleMoves = fetchCircleMoves(circle);

                LogEngine.instance.log("\n   Listing all possible moves for circle on cell " + cellToString(originCell));
                for (MoveProposal move : possibleMoves) {
                    LogEngine.instance.log("   " + move);
                }
                LogEngine.instance.newLine();

                if (isSolelySolution(originCell, possibleMoves)) {
                    LogEngine.instance.log("   --> VICTORY! Circle only has one possible move");
                    MoveProposal move = possibleMoves.get(0);
                    LogEngine.instance.log("   " + move);

                    highlightCircleAttempt(originCell);

                    Cell destinationCell = cellBoard.get(move.getMoveX(), move.getMoveY());
                    LogEngine.instance.log("   destinationCell: " + cellToString(destinationCell));
                    moveCircle(originCell, destinationCell);
                    markCellsVisited(originCell, move);

                    LogEngine.instance.log("   Marking region and circle as invariant");
                    inducer.getRegionBy(destinationCell).setFinal(true);
                    circle.setInvariant(true);

                    insertChronologicalMove(move);
                }
            }

            cellBoard.printCircles();
            LogEngine.instance.log("\n");
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

    private void highlightCircleAttempt(Cell cell) {
        if (!cell.isAnyCircleRegistered()) {
            throw new IllegalStateException("attempting to highlight circle line, but cell " +
                    cellToString(cell) + " does not contain any circle");
        }

        Platform.runLater(() -> {
            controller.highlightCircleLine(cell.getGridX(), cell.getGridY(), GUISettings.circleOutlineColor);
        });

        waitInterval(GUISettings.circleHighlightingTime);
    }

    private void insertChronologicalMove(MoveProposal move) {
        chronologicalMoves.add(move);
        refreshGUIBoard();
        waitInterval(GUISettings.sleepInterval);
    }

    private void refreshGUIBoard() {
        Platform.runLater(() -> {
            controller.drawBoard(cellBoard, chronologicalMoves);
        });
    }

    private void moveCircle(Cell origin, Cell destination) {
        checkOriginAndDestinationCells(origin, destination);

        LogEngine.instance.log("   ... Moving circle from " + cellToString(origin) + " to " + cellToString(destination));
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

            mapper.mapCellCoordinates(i, origin).setVisited(true);
        }
    }

    private void waitInterval(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
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
        return "(" + cell.getGridX() + ", " + cell.getGridY() + ")";
    }

}
