package puzzle;

public class FieldCircle {
    private Distance moveDistance;

    public FieldCircle() {
        moveDistance = Distance.ANY;
    }

    public FieldCircle(int distance) {
        moveDistance = new Distance(distance);
    }

    public FieldCircle(Distance distance) {
        moveDistance = distance;
    }

    public Distance getDistance() {
        return moveDistance;
    }

    public boolean hasZeroDistance() {
        return getDistance().toIntValue() == 0;
    }

    public boolean hasAnyDistance() {
        return getDistance() == Distance.ANY;
    }
}
