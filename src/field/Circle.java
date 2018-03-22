package field;

public class Circle {
    private Distance moveDistance;

    public Circle() {
        moveDistance = Distance.ANY;
    }

    public Circle(int distance) {
        moveDistance = new Distance(distance);
    }
}
