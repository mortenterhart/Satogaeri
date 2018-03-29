package move;

import puzzle.Board;
import puzzle.Cell;
import puzzle.Distance;
import puzzle.FieldCircle;
import solver.DirectionMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnyDistanceICircleMover implements ICircleMover {
    private Board cellBoard;

    public AnyDistanceICircleMover(Board cellBoard) {
        Objects.requireNonNull(cellBoard);
        this.cellBoard = cellBoard;
    }

    public List<MoveProposal> identifyAllPossibleMoves(Cell origin) {
        checkOriginCircle(origin);

        List<MoveProposal> possibleMoves = new ArrayList<>(4);

        // By standard, add the current position as move because all circles without
        // distance can also stay at their position (only if cell is not invariant!)
        if (!origin.isInvariant()) {
            possibleMoves.add(new MoveProposal(origin.getGridX(), origin.getGridY(), origin.getCircle(),
                    MoveDirection.LEFT, new Distance(0)));
        }

        for (MoveDirection direction : MoveDirection.values()) {
            MoveProposal proposal = identifyPossibleMove(origin, direction);
            if (proposal != null) {
                possibleMoves.add(proposal);
            }
        }

        return possibleMoves;
    }

    public MoveProposal identifyPossibleMove(Cell origin, MoveDirection direction) {
        DirectionMapper mapper = new DirectionMapper(direction, cellBoard);
        FieldCircle cellCircle = origin.getCircle();
        int foundCellIndex = -1;

        int indexOffset = mapper.mapRelatedCoordinateWithStep(origin);
        if (mapper.mapBoardBoundaries(indexOffset)) {
            Cell pathCell = mapper.mapCellCoordinates(indexOffset, origin);
            while (pathCell != null && !pathCell.isVisited() && mapper.mapBoardBoundaries(indexOffset)) {
                foundCellIndex = indexOffset;
                indexOffset += mapper.mapLoopStep();
                if (mapper.mapBoardBoundaries(indexOffset)) {
                    pathCell = mapper.mapCellCoordinates(indexOffset, origin);
                }
            }
        }

        if (foundCellIndex >= 0) {
            Cell destinationCell = mapper.mapCellCoordinates(foundCellIndex, origin);
            if (destinationCell.isInvariant()) {
                while (destinationCell.isInvariant() && !destinationCell.isVisited() && mapper.mapBoardBoundaries(foundCellIndex)) {
                    foundCellIndex -= mapper.mapLoopStep();
                    destinationCell = mapper.mapCellCoordinates(foundCellIndex, destinationCell);
                }

                if (destinationCell == origin) {
                    destinationCell = null;
                }
            }

            if (destinationCell != null) {
                return new MoveProposal(destinationCell.getGridX(), destinationCell.getGridY(),
                        cellCircle, direction, new Distance(Math.abs(foundCellIndex - mapper.mapRelatedCoordinate(origin))));
            }
        }

        return null;
    }

    public void checkOriginCircle(Cell origin) {
        if (!origin.isAnyCircleRegistered()) {
            throw new IllegalStateException("trying to identify possible moves for cell " + origin + " failed " +
                    "because no circle is registered at that cell");
        }

        if (!origin.getCircle().hasAnyDistance()) {
            throw new IllegalStateException("registered circle in cell " + origin + " has a determined distance");
        }
    }
}
