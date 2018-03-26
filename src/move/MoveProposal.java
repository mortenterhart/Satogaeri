package move;

import puzzle.Distance;

public class MoveProposal {
    private int moveX;
    private int moveY;
    private MoveDirection direction;
    private Distance movedDistance;

    public MoveProposal(int moveX, int moveY, MoveDirection direction, Distance movedDistance) {
        this.moveX = moveX;
        this.moveY = moveY;
        this.direction = direction;
        this.movedDistance = movedDistance;
    }

    public int getMoveX() {
        return moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public MoveDirection getDirection() {
        return direction;
    }

    public Distance getMovedDistance() {
        return movedDistance;
    }

    @Override
    public String toString() {
        return "Move to (" + moveX + ", " + moveY + ") into direction " + direction.name() + " with distance " + movedDistance;
    }
}
