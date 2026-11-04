package gruppe3.garderobekontrol.model;

import gruppe3.garderobekontrol.model.enums.City;

public class Location {
    private final double LONGITUDE;
    private final double LATITUDE;

    public Location(City city) {
        this.LONGITUDE = city.getLongitude();
        this.LATITUDE = city.getLatitude();
    }

    public double getLongitude() {
        return LONGITUDE;
    }

    public double getLatitude() {
        return LATITUDE;
    }
}
