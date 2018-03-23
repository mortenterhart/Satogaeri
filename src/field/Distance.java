package field;

public class Distance {

    public static final Distance ANY = new Distance(-1);

    private int distance = 0;

    public Distance(int distance) {
        this.distance = distance;
    }

    public int toIntValue() {
        return distance;
    }

    public boolean isAny() {
        return distance < 0;
    }
}
