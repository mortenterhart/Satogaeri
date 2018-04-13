package puzzle;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private List<Cell> cells;
    private boolean isFinal = false;

    public Region() {
        cells = new ArrayList<>();
    }

    public void addCell(Cell cell) {
        cells.add(cell);
    }

    public int getNumberOfCircles() {
        return (int) cells.stream().filter(Cell::hasAnyCircleRegistered).count();
    }

    public boolean containsCell(int[] cellCoordinates) {
        for (Cell checkCell : cells) {
            if (checkCell.equals(cellCoordinates[0], cellCoordinates[1])) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Cell cell) {
        return cells.contains(cell);
    }

    public boolean contains(FieldCircle circle) {
        for (Cell regionCell : cells) {
            if (regionCell != null && regionCell.hasAnyCircleRegistered() &&
                regionCell.getCircle() == circle) {
                return true;
            }
        }

        return false;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;

        for (Cell regionCell : cells) {
            regionCell.setInvariant(isFinal);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Region { ");
        for (Cell cell : cells) {
            builder.append(cell).append(", ");
        }
        builder.append(" }");

        return builder.toString();
    }

    public List<Cell> getCells() {
        return cells;
    }
}
