package solver;

import move.MoveDirection;
import puzzle.Board;
import puzzle.Cell;

public class DirectionMapper {
    private MoveDirection direction;
    private Board cellBoard;

    public DirectionMapper(MoveDirection direction, Board cellBoard) {
        this.direction = direction;
        this.cellBoard = cellBoard;
    }

    public int mapNextMovePosition(Cell movedCell) {
        int sourceLocation = mapRelatedCoordinate(movedCell);
        int circleDistance = movedCell.getCircle().getDistance().toIntValue();

        switch (direction) {
            case LEFT:
            case UP:
                return sourceLocation - circleDistance;

            case RIGHT:
            case DOWN:
                return sourceLocation + circleDistance;
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    private int mapRelatedCoordinate(Cell cell) {
        switch (direction) {
            case LEFT:
            case RIGHT:
                return cell.getGridX();

            case UP:
            case DOWN:
                return cell.getGridY();
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    public int mapRelatedCoordinateWithStep(Cell cell) {
        return mapRelatedCoordinate(cell) + mapLoopStep();
    }

    public boolean isMoveInsideBoundaries(int nextMovePosition, int boardWidth, int boardHeight) {
        switch (direction) {
            case LEFT:
            case RIGHT:
                return nextMovePosition >= 0 && nextMovePosition < boardWidth;

            case UP:
            case DOWN:
                return nextMovePosition >= 0 && nextMovePosition < boardHeight;
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    public boolean mapMoveCondition(int loopCoordinate, int nextMovePosition) {
        switch (direction) {
            case LEFT:
            case UP:
                return loopCoordinate > nextMovePosition;

            case RIGHT:
            case DOWN:
                return loopCoordinate < nextMovePosition;
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    public int mapLoopStep() {
        switch (direction) {
            case LEFT:
            case UP:
                return -1;

            case RIGHT:
            case DOWN:
                return 1;
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    public Cell mapCellCoordinates(int loopCoordinate, Cell origin) {
        switch (direction) {
            case LEFT:
            case RIGHT:
                return cellBoard.get(loopCoordinate, origin.getGridY());

            case UP:
            case DOWN:
                return cellBoard.get(origin.getGridX(), loopCoordinate);
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    private static class InvalidDirectionException extends RuntimeException {

        public InvalidDirectionException(String message) {
            super(message);
        }

        @Override
        public StackTraceElement[] getStackTrace() {
            return super.getStackTrace();
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return super.fillInStackTrace();
        }
    }
}
