package field;

public class Cell {
    private MovableCircle placedCircle;

    public void registerCircle(MovableCircle circle) {
        placedCircle = circle;
    }

    public void detachCircle() {
        placedCircle = null;
    }

    public boolean isAnyCircleRegistered() {
        return placedCircle != null;
    }

    public void addListener() {
    }
}
