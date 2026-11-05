package gruppe3.garderobekontrol.model;

import gruppe3.garderobekontrol.model.enums.City;

public class Location {

    private final double LONGITUDE;
    private final double LATITUDE;

    public Location(double longitude, double latitude) {
        this.LONGITUDE = longitude;
        this.LATITUDE = latitude;
    }

    public double getLongitude() {
        return LONGITUDE;
    }

    public double getLatitude() {
        return LATITUDE;
    }
}
