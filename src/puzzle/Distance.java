package puzzle;

public class Distance {

    public static final Distance ANY = new Distance(-1);

    private int distance = 0;

    public Distance(int distance) {
        this.distance = distance;
    }

    public Distance(Distance otherDistance) {
        this.distance = otherDistance.toIntValue();
    }

    public int toIntValue() {
        return distance;
    }

    public boolean isAny() {
        return distance < 0;
    }

    @Override
    public boolean equals(Object object) {
        return object != null && object instanceof Distance &&
                this.distance == ((Distance) object).distance;
    }

    @Override
    public String toString() {
        return isAny() ? "any" : String.valueOf(distance);
    }
}
