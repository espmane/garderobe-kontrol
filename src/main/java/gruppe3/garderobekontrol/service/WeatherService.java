package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.Location;
import gruppe3.garderobekontrol.model.dto.response.OpenMeteoResponse;
import gruppe3.garderobekontrol.model.dto.response.WeatherResponse;
import gruppe3.garderobekontrol.model.enums.City;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class WeatherService {
    private final WeatherClient client;

    public WeatherService(WeatherClient client) {
        this.client = client;
    }

    // Skal ændres så lokation ikke er en hardkodet værdi
    public WeatherResponse getWeather() {
        OpenMeteoResponse r = client.fetch(new Location(City.COPENHAGEN));

        int hour = LocalTime.now(ZoneId.of("Europe/Copenhagen")).getHour();

        return new WeatherResponse(
                number(r.hourly().temperature(), hour),
                number(r.hourly().windSpeed(), hour),
                (int) number(r.hourly().humidity(), hour),
                number(r.daily().uvIndex(), 0),        // daily har kun én dag
                number(r.daily().precipitation(), 0),
                number(r.daily().snowfall(), 0),
                describe(r.daily().weatherCode().get(0)));
    }

    // Returnerer 0, hvis listen eller værdien mangler (null)
    private double number(List<? extends Number> list, int index) {
        if (list == null || index >= list.size() || list.get(index) == null) {
            return 0.0;
        }
        return list.get(index).doubleValue();
    }

    private String describe(Integer code) {
        if (code == null) return "Ukendt";
        return switch (code) {
            case 0 -> "Klart";
            case 1, 2, 3 -> "Let skyet til overskyet";
            case 45, 48 -> "Tåge";
            case 51, 53, 55, 56, 57 -> "Støvregn";
            case 61, 63, 65, 66, 67, 80, 81, 82 -> "Regn";
            case 71, 73, 75, 77, 85, 86 -> "Sne";
            case 95, 96, 99 -> "Tordenvejr";
            default -> "Ukendt";
        };
    }
}