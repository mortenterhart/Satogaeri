package field;

public class MovableCircle {
    private Distance moveDistance;

    public MovableCircle() {
        moveDistance = Distance.ANY;
    }

    public MovableCircle(int distance) {
        moveDistance = new Distance(distance);
    }

    public MovableCircle(Distance distance) {
        moveDistance = distance;
    }

    public int getDistance() {
        return moveDistance.toIntValue();
    }
}
