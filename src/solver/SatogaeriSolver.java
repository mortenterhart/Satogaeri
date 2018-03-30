package solver;

import gui.controller.GuiController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import move.*;
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
    private int movePointer = 0;

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

        exceptionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.printStackTrace();
            }
        });

        //cellBoard.print();
        return cellBoard;
    }

    private void findSolution() {
        markZeroDistanceCellsInvariant();

        moveCirclesWithSolelySolution();

        cellBoard.printCircles();

        //while (!allCirclesAreInvariant()) {
        for (int j = 0; j < 1; j++) {
            // TODO: Start with all circles in invariant regions where a circle is already invariant
            // TODO: Use the Warnsdorf algorithm similar to the Knight's tour and move the circle preferably to
            //       fields with the least amount of free neighbour fields
            // TODO: Each iteration check if there are circles that have only one solution to move
            // TODO: Implement backtracking by saving all move proposals to the list chronologicalMoves and add a reference to the circle
            //       to be able to know which circle moved
            // TODO: Each new move will be appended to that list and save the current move in an integer pointer
            // TODO: Iterate over cells and look if there are cells that can only be accessed by one circle
            //       Make these steps then and save them to the history


            /*for (int i = 0; i < boardCircles.size(); i++) {
                FieldCircle circle = boardCircles.get(i);
                if (!circle.isInvariant()) {
                    Cell originCell = inducer.getCellBy(circle);
                    List<MoveProposal> possibleMoves;
                    if (circle.hasAnyDistance()) {
                        possibleMoves = anyDistanceMover.identifyAllPossibleMoves(originCell);
                    } else {
                        possibleMoves = determinedDistanceMover.identifyAllPossibleMoves(originCell);
                    }

                    if (possibleMoves.isEmpty()) {
                        MoveProposal lastCircleMove = null;
                        for (int k = chronologicalMoves.size() - 1; k >= 0; k--) {
                            MoveProposal historyProposal = chronologicalMoves.get(k);
                            if (historyProposal.getMovedCircle() == circle) {
                                lastCircleMove = historyProposal;
                                break;
                            }
                        }

                        if (lastCircleMove == null) {
                            if (possibleMoves.size() > 0) {
                                MoveProposal initialMove = possibleMoves.get(0);
                                Cell destinationCell = cellBoard.get(initialMove.getMoveX(), initialMove.getMoveY());
                                moveCircle(originCell, destinationCell);
                                markCellsVisited(originCell, initialMove);
                            }
                        } else {
                            revertMove(lastCircleMove);
                        }
                    } else {

                    }
                }
                cellBoard.printCircles();
            }*/
        }
    }

    private boolean allCirclesAreInvariant() {
        for (FieldCircle circle : boardCircles) {
            if (!circle.isInvariant()) {
                return false;
            }
        }

        return true;
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
                insertChronologicalMove(new MoveProposal(originCell.getGridX(), originCell.getGridY(),
                        circle, MoveDirection.LEFT, new Distance(0)));
            }
        }
    }

    private void moveCirclesWithSolelySolution() {
        while (hasCirclesWithSolelySolution()) {
            for (FieldCircle circle : boardCircles) {
                Cell originCell = inducer.getCellBy(circle);
                System.out.println("Cell " + cellToString(originCell) + " has next circle registered");
                if (originCell != null && !circle.isInvariant()) {
                    boolean foundSolelySolution = false;
                    System.out.println("   Circle " + circle + " is not invariant");
                    List<MoveProposal> possibleMoves;
                    if (circle.hasAnyDistance()) {
                        System.out.println("   Calculating moves for any distance circle");
                        possibleMoves = anyDistanceMover.identifyAllPossibleMoves(originCell);
                        if (possibleMoves.size() == 1 && anyDistanceMover.isDistinctMove(originCell, possibleMoves.get(0))) {
                            foundSolelySolution = true;
                        }
                    } else {
                        System.out.println("   Calculating moves for determined distance circle");
                        possibleMoves = determinedDistanceMover.identifyAllPossibleMoves(originCell);

                        if (possibleMoves.size() == 1 && determinedDistanceMover.isDistinctMove(originCell, possibleMoves.get(0))) {
                            foundSolelySolution = true;
                        }
                    }

                    System.out.println("\n   Listing all possible moves for circle on cell " + cellToString(originCell));
                    for (MoveProposal move : possibleMoves) {
                        System.out.println("   " + move);
                    }
                    System.out.println();

                    if (foundSolelySolution) {
                        System.out.println("   --> VICTORY! Circle only has one possible move");
                        MoveProposal move = possibleMoves.get(0);
                        System.out.println("   " + move);

                        Cell destinationCell = cellBoard.get(move.getMoveX(), move.getMoveY());
                        System.out.println("   destinationCell: " + cellToString(destinationCell));
                        moveCircle(originCell, destinationCell);
                        markCellsVisited(originCell, move);
                        insertChronologicalMove(move);
                        waitInterval(500);

                        System.out.println("   Marking region and circle as invariant");
                        inducer.getRegionBy(destinationCell).setFinal(true);
                        circle.setInvariant(true);
                    }
                }

                cellBoard.printCircles();
                System.out.println("\n");
            }
        }
    }

    private boolean hasCirclesWithSolelySolution() {
        for (FieldCircle circle : boardCircles) {
            if (circle != null && !circle.hasAnyDistance() && !circle.isInvariant()) {
                Cell originCell = inducer.getCellBy(circle);
                List<MoveProposal> proposals = determinedDistanceMover.identifyAllPossibleMoves(originCell);

                if (proposals.size() == 1) {
                    return true;
                }
            }
        }

        return false;
    }

    private void insertChronologicalMove(MoveProposal move) {
        chronologicalMoves.add(move);
        movePointer++;
        refreshGUIBoard();
    }

    private void refreshGUIBoard() {
        Platform.runLater(() -> {
            controller.drawBoard(cellBoard, chronologicalMoves);
        });
    }

    private void moveCircle(Cell origin, Cell destination) {
        checkOriginAndDestinationCells(origin, destination);

        System.out.println("   ... Moving circle from " + cellToString(origin) + " to " + cellToString(destination));
        FieldCircle movedCircle = origin.getCircle();
        origin.detachCircle();
        destination.registerCircle(movedCircle);
    }

    private void markCellsVisited(Cell origin, MoveProposal move) {
        // Make move and mark all cells on the way as visited
        int distance = move.getMovedCircle().getDistance().toIntValue();
        switch (move.getDirection()) {
            case LEFT:
                System.out.println("   Marking cells to left as visited");
                for (int i = origin.getGridX(); i >= origin.getGridX() - distance; i--) {
                    cellBoard.get(i, origin.getGridY()).setVisited(true);
                    System.out.println("   " + cellToString(cellBoard.get(i, origin.getGridY())));
                }
                break;

            case UP:
                System.out.println("   Marking cells to up as visited");
                for (int i = origin.getGridY(); i >= origin.getGridY() - distance; i--) {
                    cellBoard.get(origin.getGridX(), i).setVisited(true);
                    System.out.println("   " + cellToString(cellBoard.get(origin.getGridX(), i)));
                }
                break;

            case RIGHT:
                System.out.println("   Marking cells to right as visited");
                for (int i = origin.getGridX(); i <= origin.getGridX() + distance; i++) {
                    cellBoard.get(i, origin.getGridY()).setVisited(true);
                    System.out.println("   " + cellToString(cellBoard.get(i, origin.getGridY())));
                }
                break;

            case DOWN:
                System.out.println("   Marking cells to down as visited");
                for (int i = origin.getGridY(); i <= origin.getGridY() + distance; i++) {
                    cellBoard.get(origin.getGridX(), i).setVisited(true);
                    System.out.println("   " + cellToString(cellBoard.get(origin.getGridX(), i)));
                }
                break;
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
