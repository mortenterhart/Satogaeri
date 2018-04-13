package move;

import puzzle.Board;
import puzzle.Cell;
import puzzle.Distance;
import puzzle.FieldCircle;
import solver.DirectionMapper;

import java.util.ArrayList;
import java.util.List;

public class DeterminedDistanceCircleMover implements ICircleMover {

    private Board cellBoard;
    private boolean isClosed = false;

    public DeterminedDistanceCircleMover(Board cellBoard) {
        this.cellBoard = cellBoard;
    }

    public List<MoveProposal> identifyAllPossibleMoves(Cell origin) {
        if (isClosed) {
            return null;
        }

        checkOriginCircle(origin);

        List<MoveProposal> possibleMoves = new ArrayList<>(4);
        for (MoveDirection direction : MoveDirection.values()) {
            MoveProposal proposal = this.identifyPossibleMove(origin, direction);
            if (proposal != null) {
                possibleMoves.add(proposal);
            }
        }

        return possibleMoves;
    }

    public MoveProposal identifyPossibleMove(Cell origin, MoveDirection direction) {
        if (isClosed) {
            return null;
        }

        DirectionMapper mapper = new DirectionMapper(direction, cellBoard);
        FieldCircle cellCircle = origin.getCircle();
        boolean movePossible = true;
        int nextMovePosition = mapper.mapNextMovePosition(origin);

        if (mapper.mapBoardBoundaries(nextMovePosition)) {
            for (int i = mapper.mapRelatedCoordinateWithStep(origin);
                 mapper.mapMoveConditionExclusive(i, nextMovePosition) && movePossible;
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

    @Override
    public boolean isDistinctMove(Cell originCell, MoveProposal proposal) {
        return true;
    }

    public void checkOriginCircle(Cell origin) {
        if (!origin.hasAnyCircleRegistered()) {
            throw new IllegalStateException("trying to identify possible moves for cell " + origin + " failed " +
                                            "because no circle is registered at that cell");
        }

        if (origin.getCircle().hasAnyDistance()) {
            throw new IllegalStateException("registered circle in cell " + origin + " has any distance. Use this " +
                                            "method for determined distances!");
        }
    }

    public void close() {
        isClosed = true;
    }
}
