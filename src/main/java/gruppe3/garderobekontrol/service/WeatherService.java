package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.Weather;
import gruppe3.garderobekontrol.model.dto.response.DmiResponse;
import gruppe3.garderobekontrol.model.dto.response.WeatherResponse;
import gruppe3.garderobekontrol.model.enums.City;
import gruppe3.garderobekontrol.model.Location;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    private final WeatherClient client;

    public WeatherService(WeatherClient client) {
        this.client = client;
    }

    public WeatherResponse getWeather() {
        DmiResponse.Properties p = client.fetch(new Location(City.COPENHAGEN))
                .features().get(0).properties();   // første tidspunkt = nu

        return new Weather(
                p.temperature() - 273.15,      // °C
                null,                          // vejrtype: tilføjes senere
                p.rainRate() * 3600,
                p.snowRate() * 3600,
                p.windSpeed(),
                p.windGust(),
                null,                          // skydække: tilføjes senere
                null);                         // sol: tilføjes senere
    }
}

