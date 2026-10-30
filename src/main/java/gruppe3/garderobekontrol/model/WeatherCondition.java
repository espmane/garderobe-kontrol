package gruppe3.garderobekontrol.model;

public enum WeatherCondition {

    DRIZZLE(0),
    RAIN(1),
    SLEET(2),
    SNOW(3),
    FREEZING_DRIZZLE(4),
    FREEZING_RAIN(5),
    GRAUPEL(6),
    HAIL(7);

    private final int code;

    WeatherCondition(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static WeatherCondition fromCode(int code) {
        return switch (code) {
            case 0 -> DRIZZLE;
            case 1 -> RAIN;
            case 2 -> SLEET;
            case 3 -> SNOW;
            case 4 -> FREEZING_DRIZZLE;
            case 5 -> FREEZING_RAIN;
            case 6 -> GRAUPEL;
            case 7 -> HAIL;
            default -> throw new IllegalStateException("Unexpected value: " + code);
        };
    }
}
