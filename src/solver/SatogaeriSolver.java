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

    public SatogaeriSolver(Board cellBoard) {
        this.cellBoard = cellBoard;
        this.boardCircles = cellBoard.getAllRegisteredCircles();
        this.inducer = new AssociationInducer(cellBoard);
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
        cellBoard.print();
        return null;
    }

    private void findSolution() {
        markZeroDistanceCellsInvariant();

        outputCircleList();

        moveCirclesWithSolelySolution();
    }

    private void outputCircleList() {
        for (FieldCircle circle : boardCircles) {
            System.out.println(inducer.getCellBy(circle));
        }
    }

    private void markZeroDistanceCellsInvariant() {
        for (FieldCircle circle : boardCircles) {
            if (circle != null && !inducer.getCellBy(circle).isInvariant() && circle.hasZeroDistance()) {
                inducer.getRegionBy(circle).setFinal(true);
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
                    if (originCell != null && !circle.hasAnyDistance()) {
                        List<MoveProposal> possibleMoves = new ArrayList<>(4);

                        System.out.println("Calculating moves for " + cellToString(originCell.getGridX(), originCell.getGridY()) + "\n");
                        for (MoveDirection direction : MoveDirection.values()) {
                            MoveProposal proposal = identifyPossibleMove(originCell, direction);
                            if (proposal != null) {
                                possibleMoves.add(proposal);
                            } else {
                                System.out.println("proposal is null");
                            }
                        }

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
            if (circle != null && !circle.hasAnyDistance()) {
                Cell originCell = inducer.getCellBy(circle);
                List<MoveProposal> proposals = new ArrayList<>(4);
                for (MoveDirection direction : MoveDirection.values()) {
                    MoveProposal move = identifyPossibleMove(originCell, direction);
                    if (move != null) {
                        proposals.add(move);
                    }
                }

                if (proposals.size() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private MoveProposal identifyPossibleMove(Cell origin, MoveDirection direction) {
        FieldCircle cellCircle = origin.getCircle();
        int circleDistance = cellCircle.getDistance().toIntValue();
        boolean movePossible = true;

        switch (direction) {
            case LEFT:
                //System.out.println("Checking if left move is possible");
                int moveLeftX = origin.getGridX() - circleDistance;
                //System.out.println("Left field would be (" + moveLeftX + ", " + origin.getGridY() + ") with distance " + circleDistance);
                //System.out.println("moveLeftX >= 0: " + moveLeftX + " >= 0");
                if (moveLeftX >= 0) {
                    for (int x = origin.getGridX() - 1; x > moveLeftX; x--) {
                        //System.out.println("Next cell on left is " + cellToString(x, origin.getGridY()));
                        Cell pathCell = cellBoard.get(x, origin.getGridY());
                        //System.out.println(pathCell);
                        if (pathCell.isVisited()) {
                            //System.out.println("Cell is visited -> move not possible");
                            movePossible = false;
                        }
                    }

                    Cell destinationCell = cellBoard.get(moveLeftX, origin.getGridY());
                    //System.out.println("Checking if last path cell " + cellToString(moveLeftX, origin.getGridY()) + " is invariant or visited");
                    if (destinationCell.isInvariant() || destinationCell.isVisited()) {
                        // System.out.println("last cell is invariant or visited -> move not possible");
                        movePossible = false;
                    }

                    if (movePossible) {
                        // System.out.println("move is possible !!");
                        return new MoveProposal(moveLeftX, origin.getGridY(), direction, new Distance(circleDistance));
                    }
                }
                break;

            case UP:
                // System.out.println("Checking if up move is possible");
                int moveUpY = origin.getGridY() - circleDistance;
                // System.out.println("Up field would be (" + origin.getGridX() + ", " + moveUpY + ") with distance " + circleDistance);
                // System.out.println("moveUpY >= 0: " + moveUpY + " >= 0");
                if (moveUpY >= 0) {
                    for (int y = origin.getGridY() - 1; y > moveUpY; y--) {
                        // System.out.println("Next cell on left is " + cellToString(origin.getGridX(), y));
                        Cell pathCell = cellBoard.get(origin.getGridX(), y);
                        // System.out.println(pathCell);
                        if (pathCell.isVisited()) {
                            // System.out.println("Cell is visited -> move not possible");
                            movePossible = false;
                        }
                    }

                    // System.out.println("Checking if last path cell " + cellToString(origin.getGridX(), moveUpY) + " is invariant or visited");
                    Cell destinationCell = cellBoard.get(origin.getGridX(), moveUpY);
                    if (destinationCell.isInvariant() || destinationCell.isVisited()) {
                        // System.out.println("last cell is invariant or visited -> move not possible");
                        movePossible = false;
                    }

                    if (movePossible) {
                        // System.out.println("move is possible !!");
                        return new MoveProposal(origin.getGridX(), moveUpY, direction, new Distance(circleDistance));
                    }
                }
                break;

            case RIGHT:
                System.out.println("Checking if right move is possible");
                int moveRightX = origin.getGridX() + circleDistance;
                System.out.println("Right field would be (" + moveRightX + ", " + origin.getGridY() + ") with distance " + circleDistance);
                System.out.println("moveRightX < cellBoard.getWidth(): " + moveRightX + " < " + cellBoard.getWidth());
                if (moveRightX < cellBoard.getWidth()) {
                    for (int x = origin.getGridX() + 1; x < moveRightX; x++) {
                        System.out.println("Next cell on right is " + cellToString(x, origin.getGridY()));
                        Cell pathCell = cellBoard.get(x, origin.getGridY());
                        System.out.println(pathCell);
                        if (pathCell.isVisited()) {
                            System.out.println("Cell is visited -> move not possible");
                            movePossible = false;
                        }
                    }

                    Cell destinationCell = cellBoard.get(moveRightX, origin.getGridY());
                    System.out.println("Checking if last path cell " + cellToString(moveRightX, origin.getGridY()) + " is invariant or visited");
                    if (destinationCell.isInvariant() || destinationCell.isVisited()) {
                        System.out.println("last cell is invariant or visited -> move not possible");
                        movePossible = false;
                    }

                    if (movePossible) {
                        System.out.println("move is possible !!");
                        return new MoveProposal(moveRightX, origin.getGridY(), direction, new Distance(circleDistance));
                    }
                }
                break;

            case DOWN:
                // System.out.println("Checking if down move is possible");
                int moveDownY = origin.getGridY() + circleDistance;
                // System.out.println("Down field would be (" + origin.getGridX() + ", " + moveDownY + ") with distance " + circleDistance);
                // System.out.println("moveDownY < cellBoard.getHeight(): " + moveDownY + " < " + cellBoard.getHeight());
                if (moveDownY < cellBoard.getHeight()) {
                    for (int y = origin.getGridY() + 1; y < moveDownY; y++) {
                        // System.out.println("Next cell on left is " + cellToString(origin.getGridX(), y));
                        Cell pathCell = cellBoard.get(origin.getGridX(), y);
                        // System.out.println(pathCell);
                        if (pathCell.isVisited()) {
                            // System.out.println("Cell is visited -> move not possible");
                            movePossible = false;
                        }
                    }

                    Cell destinationCell = cellBoard.get(origin.getGridX(), moveDownY);
                    // System.out.println("Checking if last path cell " + cellToString(origin.getGridX(), moveDownY) + " is invariant or visited");
                    if (destinationCell.isInvariant() || destinationCell.isVisited()) {
                        // System.out.println("last cell is invariant or visited -> move not possible");
                        movePossible = false;
                    }

                    if (movePossible) {
                        // System.out.println("move is possible !!");
                        return new MoveProposal(origin.getGridX(), moveDownY, direction, new Distance(circleDistance));
                    }
                }
                break;
        }
        //System.out.println();

        return null;
    }

    private String cellToString(int x, int y) {
        return "(" + x + ", " + y + ")";
    }

}
