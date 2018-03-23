package field;

public class Cell {
    private int gridX = 0;
    private int gridY = 0;
    private MovableCircle placedCircle;

    public Cell(int x, int y) {
        gridX = x;
        gridY = y;
    }

    public void registerCircle(MovableCircle circle) {
        placedCircle = circle;
    }

    public void detachCircle() {
        placedCircle = null;
    }

    public MovableCircle getCircle() {
        return placedCircle;
    }

    public boolean isAnyCircleRegistered() {
        return placedCircle != null;
    }

    public boolean equals(int x, int y) {
        return gridX == x && gridY == y;
    }

    @Override
    public String toString() {
        return "Cell { x = " + gridX + ", y = " + gridY + ", circleRegistered = " +
                (isAnyCircleRegistered() ? "yes, distance = " + placedCircle.getDistance() : "no") + " }";
    }
}
