package field;

public class Cell {
    private Circle placedCircle;

    public void registerCircle(Circle circle) {
        placedCircle = circle;
    }

    public void unregister() {
        placedCircle = null;
    }

    public boolean isAnyCircleRegistered() {
        return placedCircle != null;
    }

    public void addListener() {
    }
}
