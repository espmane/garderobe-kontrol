package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.client.WeatherClient;
import gruppe3.garderobekontrol.model.dto.response.WeatherResponse;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;

    }

    public WeatherResponse getWeather() {
        return weatherClient.fetch();
    }

}
