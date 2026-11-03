package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.Location;
import gruppe3.garderobekontrol.model.dto.response.OpenMeteoResponse;
import gruppe3.garderobekontrol.model.enums.City;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    private final WeatherClient client;

    public WeatherService(WeatherClient client) {
        this.client = client;
    }

    // Skal ændres så lokation ikke er en hardkodet værdi
    public OpenMeteoResponse getWeather() {
        return client.fetch(new Location(City.COPENHAGEN));
    }
}