package gui;

import field.Distance;

public class CircleLocation {
    private int rowIndex;
    private int columnIndex;
    private Distance distance;

    public CircleLocation(int rowIndex, int columnIndex) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.distance = Distance.ANY;
    }

    public CircleLocation(int rowIndex, int columnIndex, int distance) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.distance = new Distance(distance);
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public Distance getDistance() {
        return distance;
    }
}
