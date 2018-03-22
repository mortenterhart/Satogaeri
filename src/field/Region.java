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
}
