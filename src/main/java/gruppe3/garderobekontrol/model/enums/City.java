package gruppe3.garderobekontrol.model.enums;

public enum City {
    COPENHAGEN(12.568337, 55.676098),
    AARHUS(10.203921, 56.162939),
    ODENSE(10.388310, 55.403756),
    AALBORG(9.921747, 57.048819),
    ESBJERG(8.451870, 55.476467),
    RANDERS(10.036390, 56.460710),
    KOLDING(9.472160, 55.490400),
    HORSENS(9.850340, 55.860660),
    VEJLE(9.535700, 55.711310),
    ROSKILDE(12.080350, 55.641520),
    HERNINGE(8.971000, 56.136150),
    SILKEBORG(9.545080, 56.169700);

    private final double longitude;
    private final double latitude;

    City(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
