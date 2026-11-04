package gruppe3.garderobekontrol.client;

import gruppe3.garderobekontrol.model.Location;
import gruppe3.garderobekontrol.model.dto.response.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class WeatherClient {
    private final RestClient client;
    private final List<String> HOURLY_PARAMS = List.of(
            "temperature_2m",
            "relative_humidity_2m",
            "wind_speed_10m"
    );
    private final List<String> DAILY_PARAMS = List.of(
            "uv_index_max",
            "precipitation_sum",
            "snowfall_sum",
            "weather_code"
    );

    public WeatherClient(RestClient.Builder builder) {
        client = builder.baseUrl("https://api.open-meteo.com").build();
    }

    public OpenMeteoResponse fetch(Location location) {
        return client.get()
                .uri(ub -> ub
                        .path("/v1/forecast")
                        .queryParam("latitude", location.getLatitude())
                        .queryParam("longitude", location.getLongitude())
                        .queryParam("hourly",
                                String.join(",", HOURLY_PARAMS))
                        .queryParam("daily",
                                String.join(",", DAILY_PARAMS))
                        .queryParam("timezone", "Europe/Copenhagen")
                        .queryParam("forecast_days", 1)
                        .queryParam("models", "dmi_seamless")
                        .queryParam("wind_speed_unit", "ms")
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);
    }
}