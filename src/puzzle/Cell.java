package puzzle;

public class Cell {

    private final int gridX;
    private final int gridY;
    private FieldCircle attachedCircle;
    private boolean isInvariant = false;
    private boolean isVisited = false;

    public Cell(int rowIndex, int columnIndex) {
        gridX = rowIndex;
        gridY = columnIndex;
    }

    public Cell(int rowIndex, int columnIndex, FieldCircle circle) {
        this(rowIndex, columnIndex);
        attachedCircle = circle;
    }

    public void registerCircle(FieldCircle circle) {
        attachedCircle = circle;
    }

    public void detachCircle() {
        attachedCircle = null;
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
        return attachedCircle;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public boolean hasAnyCircleRegistered() {
        return attachedCircle != null;
    }

    public boolean equals(int x, int y) {
        return gridX == x && gridY == y;
    }

    @Override
    public String toString() {
        return "Cell { x = " + gridX + ", y = " + gridY + ", visited = " + isVisited + ", invariant = " + isInvariant +
               ", circleRegistered = " + (hasAnyCircleRegistered() ? "yes, circle = " + attachedCircle : "no") + " }";
    }
}
