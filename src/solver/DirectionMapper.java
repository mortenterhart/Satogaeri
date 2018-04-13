package solver;

import javafx.geometry.Pos;
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

    public int mapRelatedCoordinate(Cell cell) {
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

    public boolean mapMoveConditionExclusive(int loopCoordinate, int nextMovePosition) {
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

    public boolean mapMoveConditionInclusive(int loopCoordinate, int nextMovePosition) {
        switch (direction) {
            case LEFT:
            case UP:
                return loopCoordinate >= nextMovePosition;

            case RIGHT:
            case DOWN:
                return loopCoordinate <= nextMovePosition;
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

    public Cell mapCellCoordinates(int loopCoordinate, Cell cell) {
        switch (direction) {
            case LEFT:
            case RIGHT:
                return cellBoard.get(loopCoordinate, cell.getGridY());

            case UP:
            case DOWN:
                return cellBoard.get(cell.getGridX(), loopCoordinate);
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    public boolean mapBoardBoundaries(int loopCoordinate) {
        switch (direction) {
            case LEFT:
            case UP:
                return loopCoordinate >= 0;

            case RIGHT:
                return loopCoordinate < cellBoard.getWidth();

            case DOWN:
                return loopCoordinate < cellBoard.getHeight();
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    public Pos mapRectangleOrientation() {
        switch (direction) {
            case LEFT:
                return Pos.CENTER_LEFT;

            case UP:
                return Pos.TOP_CENTER;

            case RIGHT:
                return Pos.CENTER_RIGHT;

            case DOWN:
                return Pos.BOTTOM_CENTER;
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    public Pos mapRectangleOrientationInverted() {
        switch (direction) {
            case LEFT:
                return Pos.CENTER_RIGHT;

            case UP:
                return Pos.BOTTOM_CENTER;

            case RIGHT:
                return Pos.CENTER_LEFT;

            case DOWN:
                return Pos.TOP_CENTER;
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    public double mapRectangleRotation() {
        switch (direction) {
            case LEFT:
            case RIGHT:
                return 0.0;

            case UP:
            case DOWN:
                return 90.0;
        }

        throw new InvalidDirectionException("direction " + direction + " is not recognized");
    }

    public static class InvalidDirectionException extends RuntimeException {

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
