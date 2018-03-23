package field;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private List<Cell> cells;

    public Region() {
        cells = new ArrayList<>();
    }

    public void addCell(Cell cell) {
        cells.add(cell);
    }

    public int getNumberOfCircles() {
        return (int) cells.stream().filter(Cell::isAnyCircleRegistered).count();
    }

    public boolean containsCell(int[] cellCoordinates) {
        for (Cell checkCell : cells) {
            if (checkCell.equals(cellCoordinates[0], cellCoordinates[1])) {
                return true;
            }
        }
        return false;
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
}
