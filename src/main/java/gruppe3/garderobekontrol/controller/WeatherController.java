package gruppe3.garderobekontrol.controller;

import gruppe3.garderobekontrol.model.dto.response.OpenMeteoResponse;
import gruppe3.garderobekontrol.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService service;

    public WeatherController(WeatherService service) {

        this.service = service;
    }

    @GetMapping("/api/weather")
    public OpenMeteoResponse getWeather(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        return service.getWeather(latitude, longitude);
    }
}
