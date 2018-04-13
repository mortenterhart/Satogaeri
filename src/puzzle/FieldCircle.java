package puzzle;

public class FieldCircle {

    private Distance moveDistance;
    private boolean isInvariant = false;

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

    public boolean isInvariant() {
        return isInvariant;
    }

    public void setInvariant(boolean invariant) {
        isInvariant = invariant;
    }

    public boolean hasZeroDistance() {
        return getDistance().toIntValue() == 0;
    }

    public boolean hasAnyDistance() {
        return getDistance() == Distance.ANY;
    }

    @Override
    public String toString() {
        return "FieldCircle { distance = " + moveDistance + ", invariant = " + isInvariant + " }";
    }
}
