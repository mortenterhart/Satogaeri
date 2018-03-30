package move;

import puzzle.Distance;
import puzzle.FieldCircle;

public class MoveProposal {
    private int moveX;
    private int moveY;
    private FieldCircle movedCircle;
    private MoveDirection direction;
    private Distance distance;

    public MoveProposal(int moveX, int moveY, FieldCircle movedCircle, MoveDirection direction, Distance distance) {
        this.moveX = moveX;
        this.moveY = moveY;
        this.movedCircle = movedCircle;
        this.direction = direction;
        this.distance = distance;
    }

    public int getMoveX() {
        return moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public FieldCircle getMovedCircle() {
        return movedCircle;
    }

    public MoveDirection getDirection() {
        return direction;
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Move to (" + moveX + ", " + moveY + ") into direction " + direction.name() + " with distance " + distance;
    }
}
