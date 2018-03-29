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

    public DeterminedDistanceCircleMover(Board cellBoard) {
        this.cellBoard = cellBoard;
    }

    public List<MoveProposal> identifyAllPossibleMoves(Cell origin) {
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
        DirectionMapper mapper = new DirectionMapper(direction, cellBoard);
        FieldCircle cellCircle = origin.getCircle();
        boolean movePossible = true;
        int nextMovePosition = mapper.mapNextMovePosition(origin);

        if (mapper.mapBoardBoundaries(nextMovePosition)) {
            for (int i = mapper.mapRelatedCoordinateWithStep(origin);
                 mapper.mapMoveCondition(i, nextMovePosition) && movePossible;
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

    public void checkOriginCircle(Cell origin) {
        if (!origin.isAnyCircleRegistered()) {
            throw new IllegalStateException("trying to identify possible moves for cell " + origin + " failed " +
                    "because no circle is registered at that cell");
        }

        if (origin.getCircle().hasAnyDistance()) {
            throw new IllegalStateException("registered circle in cell " + origin + " has any distance. Use this " +
                    "method for determined distances!");
        }
    }
}
