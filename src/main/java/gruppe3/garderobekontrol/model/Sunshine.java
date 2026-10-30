package gruppe3.garderobekontrol.model;

public enum Sunshine {

    VERY_LITTLE_SUNSHINE(0, 50),
    LITTLE_SUNSHINE(50, 200),
    MODERATE_SUNSHINE(200, 500),
    STRONG_SUNSHINE(500, 700),
    EXTREME_SUNSHINE(700, Double.MAX_VALUE);

    private final double minRadiation;
    private final double maxRadiation;

    Sunshine(double minRadiation, double maxRadiation) {
        this.minRadiation = minRadiation;
        this.maxRadiation = maxRadiation;
    }

    public double getMinRadiation() {
        return minRadiation;
    }

    public double getMaxRadiation() {
        return maxRadiation;
    }

    public static Sunshine fromRadiation(double radiation) {
        for (Sunshine sunshine : values()) {
            if (radiation >= sunshine.minRadiation && radiation < sunshine.maxRadiation) {
                return sunshine;
            }
        }
        throw new IllegalStateException("Unexpected value: " + radiation);
    }
}
