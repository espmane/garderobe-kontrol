package gruppe3.garderobekontrol.service;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {

        return weatherClient.fetch();
    }

}
