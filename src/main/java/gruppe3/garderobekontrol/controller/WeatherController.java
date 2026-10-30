package gruppe3.garderobekontrol.controller;

import gruppe3.garderobekontrol.service.WeatherService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
    private final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @PostMapping("/api/weather/demo")
    public String demo() {
        return sevice.demo();
    }
}
