package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.Location;
import gruppe3.garderobekontrol.model.dto.response.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class WeatherClient {
    private final RestClient client;

    public WeatherClient(RestClient.Builder builder) {
        client = builder.baseUrl("https://api.open-meteo.com").build();
    }

    public OpenMeteoResponse fetch(Location location) {
        return client.get()
                .uri(ub -> ub
                        .path("/v1/forecast")
                        .queryParam("latitude", location.getLatitude())
                        .queryParam("longitude", location.getLongitude())
                        .queryParam("hourly", "temperature_2m,relative_humidity_2m,wind_speed_10m")
                        .queryParam("daily", "uv_index_max,precipitation_sum,snowfall_sum,weather_code")
                        .queryParam("timezone", "Europe/Copenhagen")
                        .queryParam("forecast_days", 1)
                        .queryParam("models", "dmi_seamless")
                        .queryParam("wind_speed_unit", "ms")
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);
    }
}


