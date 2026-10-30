package gruppe3.garderobekontrol.model;

public enum CloudCover {

    MOSTLY_CLEAR(0.0, 0.2),
    PARTLY_CLOUDY(0.2, 0.4),
    CLOUDY(0.4, 0.7),
    MOSTLY_OVERCAST(0.7, 1.01);

    private final double min;
    private final double max;

    CloudCover(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public static CloudCover fromFraction(double fraction) {
        if (Double.isInfinite(fraction) || Double.isNaN(fraction)) {
            throw new IllegalStateException("Unexpected value");
        }

        for (CloudCover cloudCover : values()) {
            if (fraction >= cloudCover.min && fraction < cloudCover.max) {
                return cloudCover;
            }
        }
        throw new IllegalStateException("Unexpected value: " + fraction);
    }
}
