package puzzle;

public class Cell {
    private final int gridX;
    private final int gridY;
    private FieldCircle placedCircle;
    private boolean isInvariant = false;
    private boolean isVisited = false;

    public Cell(int rowIndex, int columnIndex) {
        gridX = rowIndex;
        gridY = columnIndex;
    }

    public void registerCircle(FieldCircle circle) {
        placedCircle = circle;
    }

    public void detachCircle() {
        placedCircle = null;
    }

    public boolean isInvariant() {
        return isInvariant;
    }

    public void setInvariant(boolean invariant) {
        this.isInvariant = invariant;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public FieldCircle getCircle() {
        return placedCircle;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
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
