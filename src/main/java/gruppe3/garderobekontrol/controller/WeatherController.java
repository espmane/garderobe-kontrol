package gruppe3.garderobekontrol.controller;

import gruppe3.garderobekontrol.model.dto.request.WeatherRequest;
import gruppe3.garderobekontrol.model.dto.response.WeatherResponse;
import gruppe3.garderobekontrol.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public WeatherResponse getWeather(@ModelAttribute WeatherRequest request) { //ModelAttribute samler parametre fra URL'en og binder dem til WeatherRequest objektet.
        return weatherService.getWeather();
    }
}
