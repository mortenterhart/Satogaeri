package move;

import puzzle.Cell;

import java.util.List;

public interface ICircleMover {

    List<MoveProposal> identifyAllPossibleMoves(Cell origin);

    MoveProposal identifyPossibleMove(Cell origin, MoveDirection direction);

    void checkOriginCircle(Cell origin);

    boolean isDistinctMove(Cell originCell, MoveProposal proposal);

    void close();
}
