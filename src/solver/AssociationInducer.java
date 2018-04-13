package solver;

import puzzle.Board;
import puzzle.Cell;
import puzzle.FieldCircle;
import puzzle.Region;

import java.util.Objects;

public class AssociationInducer {

    private Board board;

    public AssociationInducer(Board board) {
        this.board = board;
    }

    public Region getRegionBy(FieldCircle circle) {
        Objects.requireNonNull(circle);
        for (Region boardRegion : board.getRegions()) {
            if (boardRegion.contains(circle)) {
                return boardRegion;
            }
        }

        return null;
    }

    public Region getRegionBy(Cell destinationCell) {
        return getRegionBy(destinationCell.getCircle());
    }

    public Cell getCellBy(FieldCircle circle) {
        Objects.requireNonNull(circle);
        for (int y = 0; y < board.getWidth(); y++) {
            for (int x = 0; x < board.getHeight(); x++) {
                Cell currentCell = board.get(x, y);
                if (currentCell != null && currentCell.hasAnyCircleRegistered() &&
                    currentCell.getCircle() == circle) {
                    return currentCell;
                }
            }
        }

        return null;
    }
}
