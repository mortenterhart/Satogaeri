package solver;

import javafx.concurrent.Task;
import move.AnyDistanceICircleMover;
import move.DeterminedDistanceCircleMover;
import move.ICircleMover;
import move.MoveProposal;
import puzzle.Board;
import puzzle.Cell;
import puzzle.FieldCircle;

import java.util.ArrayList;
import java.util.List;

public class SatogaeriSolver extends Task<Board> {
    private Board cellBoard;
    private List<FieldCircle> boardCircles;

    private AssociationInducer inducer;

    private ICircleMover determinedDistanceMover;
    private ICircleMover anyDistanceMover;

    private List<MoveProposal> recentMoves;

    public SatogaeriSolver(Board cellBoard) {
        this.cellBoard = cellBoard;
        this.boardCircles = cellBoard.getAllRegisteredCircles();
        this.inducer = new AssociationInducer(cellBoard);
        this.determinedDistanceMover = new DeterminedDistanceCircleMover(cellBoard);
        this.anyDistanceMover = new AnyDistanceICircleMover(cellBoard);
        this.recentMoves = new ArrayList<>();
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
        //cellBoard.print();
        return null;
    }

    private void findSolution() {
        markZeroDistanceCellsInvariant();

        moveCirclesWithSolelySolution();

        //while (!allCirclesAreInvariant()) {
        for (int j = 0; j < 1; j++) {
            // TODO: Start with all circles in invariant regions where a circle is already invariant
            // TODO: Use the Warnsdorf algorithm similar to the Knight's tour and move the circle preferably to
            //       fields with the least amount of free neighbour fields
            // TODO: Each iteration check if there are circles that have only one solution to move
            // TODO: Implement backtracking by saving all move proposals to the list recentMoves and add a reference to the circle
            //       to be able to know which circle moved
            // TODO: Each new move will be appended to that list and save the current move in an integer pointer
            // TODO: Iterate over cells and look if there are cells that can only be accessed by one circle
            //       Make these steps then and save them to the history


            moveCirclesWithSolelySolution();
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
            throw new IllegalStateException();
        }
    }

    private void markZeroDistanceCellsInvariant() {
        for (FieldCircle circle : boardCircles) {
            if (circle != null && !inducer.getCellBy(circle).isInvariant() && circle.hasZeroDistance()) {
                inducer.getRegionBy(circle).setFinal(true);
                circle.setInvariant(true);
            }
        }
    }

    private void markCellsVisited(Cell origin, MoveProposal move) {
        // Make move and mark all cells on the way as visited
        int distance = move.getMovedCircle().getDistance().toIntValue();
        switch (move.getDirection()) {
            case LEFT:
                for (int i = origin.getGridX(); i >= origin.getGridX() - distance; i--) {
                    cellBoard.get(i, origin.getGridY()).setVisited(true);
                }
                break;

            case UP:
                for (int i = origin.getGridY(); i >= origin.getGridY() - distance; i--) {
                    cellBoard.get(origin.getGridX(), i).setVisited(true);
                }
                break;

            case RIGHT:
                for (int i = origin.getGridX(); i <= origin.getGridX() + distance; i++) {
                    cellBoard.get(i, origin.getGridY()).setVisited(true);
                }
                break;

            case DOWN:
                for (int i = origin.getGridY(); i <= origin.getGridY() + distance; i++) {
                    cellBoard.get(origin.getGridX(), i).setVisited(true);
                }
                break;
        }
    }

    private void moveCirclesWithSolelySolution() {
        while (hasCirclesWithSolelySolution()) {
            for (FieldCircle circle : boardCircles) {
                Cell originCell = inducer.getCellBy(circle);
                if (originCell != null && !circle.hasAnyDistance() && !circle.isInvariant()) {
                    List<MoveProposal> possibleMoves = determinedDistanceMover.identifyAllPossibleMoves(originCell);

                    if (possibleMoves.size() == 1) {
                        moveCircle(originCell, possibleMoves.get(0), false);
                    }
                }
            }
        }
    }

    private void moveCircle(Cell origin, MoveProposal move, boolean setInvariant) {
        FieldCircle movedCircle = move.getMovedCircle();
        Cell destinationCell = cellBoard.get(move.getMoveX(), move.getMoveY());
        origin.detachCircle();
        destinationCell.registerCircle(movedCircle);
        inducer.getRegionBy(destinationCell).setFinal(true);

        if (setInvariant) {
            // New condition: circle becomes invariant
            movedCircle.setInvariant(true);
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

    private String cellToString(int x, int y) {
        return "(" + x + ", " + y + ")";
    }

}
