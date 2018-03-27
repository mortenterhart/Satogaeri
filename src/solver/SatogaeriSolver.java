package solver;

import javafx.concurrent.Task;
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

    private AssociationInducer inducer;

    private List<MoveProposal> recentMoves;

    public SatogaeriSolver(Board cellBoard) {
        this.cellBoard = cellBoard;
        this.boardCircles = cellBoard.getAllRegisteredCircles();
        this.inducer = new AssociationInducer(cellBoard);
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

        long start = System.currentTimeMillis();
        moveCirclesWithSolelySolution();
        System.out.println("Runtime solely solutions: " + (System.currentTimeMillis() - start));

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

            System.out.println("Circles in invariant regions:");
            for (FieldCircle circle : boardCircles) {
                Cell circleCell = inducer.getCellBy(circle);
                if (!circle.isInvariant() && circleCell.isInvariant()) {

                }
            }
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

    private void markZeroDistanceCellsInvariant() {
        for (FieldCircle circle : boardCircles) {
            if (circle != null && !inducer.getCellBy(circle).isInvariant() && circle.hasZeroDistance()) {
                inducer.getRegionBy(circle).setFinal(true);
                circle.setInvariant(true);
            }
        }
    }

    private void moveCirclesWithSolelySolution() {
        int iterationCounter = 0;
        while (hasCirclesWithSolelySolution()) {
            for (FieldCircle circle : boardCircles) {
                if (circle != null) {
                    Cell originCell = inducer.getCellBy(circle);
                    System.out.println("Origin Cell: " + originCell);
                    if (originCell != null && !circle.hasAnyDistance() && !circle.isInvariant()) {
                        System.out.println("Calculating moves for " + cellToString(originCell.getGridX(), originCell.getGridY()) + "\n");
                        List<MoveProposal> possibleMoves = identifyAllPossibleMovesWithDeterminedDistance(originCell);

                        System.out.println("\n --- \n");

                        System.out.println("Listing all possible moves for circle on cell (" + originCell.getGridX() + ", " + originCell.getGridY() + ")");
                        for (MoveProposal proposal : possibleMoves) {
                            System.out.println(proposal);
                        }

                        if (possibleMoves.size() == 1) {
                            System.out.println("Circle on cell (" + originCell.getGridX() + ", " + originCell.getGridY() + ") has only one possible move");
                            MoveProposal move = possibleMoves.get(0);
                            Cell destinationCell = cellBoard.get(move.getMoveX(), move.getMoveY());
                            originCell.detachCircle();
                            destinationCell.registerCircle(circle);
                            inducer.getRegionBy(destinationCell).setFinal(true);

                            // New condition: circle becomes invariant
                            circle.setInvariant(true);

                            // Make move and mark all cells on the way as visited
                            int distance = circle.getDistance().toIntValue();
                            switch (move.getDirection()) {
                                case LEFT:
                                    for (int i = originCell.getGridX(); i > originCell.getGridX() - distance; i--) {
                                        cellBoard.get(i, originCell.getGridY()).setVisited(true);
                                    }
                                    break;

                                case UP:
                                    for (int i = originCell.getGridY(); i > originCell.getGridY() - distance; i--) {
                                        cellBoard.get(originCell.getGridX(), i).setVisited(true);
                                    }
                                    break;

                                case RIGHT:
                                    for (int i = originCell.getGridX(); i < originCell.getGridX() + distance; i++) {
                                        cellBoard.get(i, originCell.getGridY()).setVisited(true);
                                    }
                                    break;

                                case DOWN:
                                    for (int i = originCell.getGridY(); i < originCell.getGridY() + distance; i++) {
                                        cellBoard.get(originCell.getGridX(), i).setVisited(true);
                                    }
                                    break;
                            }
                        }
                    }
                }
                System.out.println("\n");
                cellBoard.printCircles();
            }
            System.out.println("new iteration: " + iterationCounter);
            iterationCounter++;
        }
    }

    private boolean hasCirclesWithSolelySolution() {
        for (FieldCircle circle : boardCircles) {
            if (circle != null && !circle.hasAnyDistance() && !circle.isInvariant()) {
                Cell originCell = inducer.getCellBy(circle);
                List<MoveProposal> proposals = identifyAllPossibleMovesWithDeterminedDistance(originCell);

                if (proposals.size() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<MoveProposal> identifyAllPossibleMovesWithDeterminedDistance(Cell origin) {
        List<MoveProposal> possibleMoves = new ArrayList<>(4);
        for (MoveDirection direction : MoveDirection.values()) {
            MoveProposal proposal = identifyPossibleMoveWithDeterminedDistance(origin, direction);
            if (proposal != null) {
                possibleMoves.add(proposal);
            }
        }

        return possibleMoves;
    }

    private MoveProposal identifyPossibleMoveWithDeterminedDistance(Cell origin, MoveDirection direction) {
        checkOriginCircle(origin);

        DirectionMapper mapper = new DirectionMapper(direction, cellBoard);
        FieldCircle cellCircle = origin.getCircle();
        boolean movePossible = true;
        int nextMovePosition = mapper.mapNextMovePosition(origin);

        if (mapper.isMoveInsideBoundaries(nextMovePosition, cellBoard.getWidth(), cellBoard.getHeight())) {
            for (int i = mapper.mapRelatedCoordinateWithStep(origin);
                 mapper.mapMoveCondition(i, nextMovePosition);
                 i += mapper.mapLoopStep()) {

                Cell pathCell = mapper.mapCellCoordinates(i, origin);
                if (pathCell.isVisited()) {
                    movePossible = false;
                }
            }

            Cell destinationCell = mapper.mapCellCoordinates(nextMovePosition, origin);
            if (destinationCell.isInvariant() || destinationCell.isVisited()) {
                movePossible = false;
            }

            if (movePossible) {
                return new MoveProposal(destinationCell.getGridX(), destinationCell.getGridY(),
                        cellCircle, direction, new Distance(cellCircle.getDistance()));
            }
        }

        return null;
    }

    private void checkOriginCircle(Cell origin) {
        if (!origin.isAnyCircleRegistered()) {
            throw new IllegalStateException("trying to identify possible moves for cell " + origin + " failed " +
                    "because no circle is registered at that cell");
        }

        if (origin.getCircle().hasAnyDistance()) {
            throw new IllegalStateException("registered circle in cell " + origin + " has any distance. Use this " +
                    "method for determined distances!");
        }
    }

    private MoveProposal identifyPossibleMove(Cell origin, MoveDirection direction) {
        FieldCircle cellCircle = origin.getCircle();
        int circleDistance = cellCircle.getDistance().toIntValue();
        boolean movePossible = true;

        switch (direction) {
            case LEFT:
                int moveLeftX = origin.getGridX() - circleDistance;
                if (moveLeftX >= 0) {
                    for (int x = origin.getGridX() - 1; x > moveLeftX; x--) {
                        Cell pathCell = cellBoard.get(x, origin.getGridY());
                        if (pathCell.isVisited()) {
                            movePossible = false;
                        }
                    }

                    Cell destinationCell = cellBoard.get(moveLeftX, origin.getGridY());
                    if (destinationCell.isInvariant() || destinationCell.isVisited()) {
                        movePossible = false;
                    }

                    if (movePossible) {
                        return new MoveProposal(moveLeftX, origin.getGridY(), cellCircle, direction, new Distance(circleDistance));
                    }
                }
                break;

            case UP:
                int moveUpY = origin.getGridY() - circleDistance;
                if (moveUpY >= 0) {
                    for (int y = origin.getGridY() - 1; y > moveUpY; y--) {
                        Cell pathCell = cellBoard.get(origin.getGridX(), y);
                        if (pathCell.isVisited()) {
                            movePossible = false;
                        }
                    }

                    Cell destinationCell = cellBoard.get(origin.getGridX(), moveUpY);
                    if (destinationCell.isInvariant() || destinationCell.isVisited()) {
                        movePossible = false;
                    }

                    if (movePossible) {
                        return new MoveProposal(origin.getGridX(), moveUpY, cellCircle, direction, new Distance(circleDistance));
                    }
                }
                break;

            case RIGHT:
                int moveRightX = origin.getGridX() + circleDistance;
                if (moveRightX < cellBoard.getWidth()) {
                    for (int x = origin.getGridX() + 1; x < moveRightX; x++) {
                        Cell pathCell = cellBoard.get(x, origin.getGridY());
                        if (pathCell.isVisited()) {
                            movePossible = false;
                        }
                    }

                    Cell destinationCell = cellBoard.get(moveRightX, origin.getGridY());
                    System.out.println(destinationCell);
                    if (destinationCell.isInvariant() || destinationCell.isVisited()) {
                        movePossible = false;
                    }

                    if (movePossible) {
                        System.out.println("move is possible");
                        return new MoveProposal(moveRightX, origin.getGridY(), cellCircle, direction, new Distance(circleDistance));
                    }
                }
                break;

            case DOWN:
                int moveDownY = origin.getGridY() + circleDistance;
                if (moveDownY < cellBoard.getHeight()) {
                    for (int y = origin.getGridY() + 1; y < moveDownY; y++) {
                        Cell pathCell = cellBoard.get(origin.getGridX(), y);
                        if (pathCell.isVisited()) {
                            movePossible = false;
                        }
                    }

                    Cell destinationCell = cellBoard.get(origin.getGridX(), moveDownY);
                    if (destinationCell.isInvariant() || destinationCell.isVisited()) {
                        movePossible = false;
                    }

                    if (movePossible) {
                        return new MoveProposal(origin.getGridX(), moveDownY, cellCircle, direction, new Distance(circleDistance));
                    }
                }
                break;
        }

        return null;
    }

    private MoveProposal identifyPossibleMoveAnyDistance(Cell origin, MoveDirection direction) {
        FieldCircle cellCircle = origin.getCircle();
        int lastPossiblePosition = -1;
        boolean movePossible = true;

        switch (direction) {
            case LEFT:
                if (origin.getGridX() > 0) {
                    for (int x = origin.getGridX() - 1; x >= 0; x--) {
                        Cell pathCell = cellBoard.get(x, origin.getGridY());
                        if (pathCell.isVisited()) {
                            lastPossiblePosition = x - 1;
                        }
                    }
                }
                break;

            case UP:
                break;

            case RIGHT:
                break;

            case DOWN:
                break;
        }

        return null;
    }

    private String cellToString(int x, int y) {
        return "(" + x + ", " + y + ")";
    }

}
