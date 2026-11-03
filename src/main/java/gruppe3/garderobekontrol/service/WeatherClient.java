package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.Location;
import gruppe3.garderobekontrol.model.dto.response.DmiResponse;
import gruppe3.garderobekontrol.model.dto.response.WeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class WeatherClient {
    private final RestClient client;
    private final List<String> PARAMETERS;


    public WeatherClient(RestClient.Builder builder) {
        client = builder.baseUrl("https://opendataapi.dmi.dk").build();
        PARAMETERS = List.of(
                "temperature-2m",
                "precipitation-type",
                "rain-precipitation-rate",
                "total-snowfall-rate-water-equivalent",
                "fraction-of-cloud-cover",
                "low-cloud-cover",
                "global-radiation-flux",
                "wind-speed-10m",
                "gust-wind-speed-10m"
        );
    }

    public DmiResponse fetch(Location location) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecastedr/collections/harmonie_dini_sf/position")
                        .queryParam("coords", "{coords}")
                        .queryParam("crs", "crs84")
                        .queryParam("parameter-name", String.join(",", PARAMETERS))
                        .queryParam("f", "GeoJSON")
                        .build("POINT(" + location.getLongitude() + " " + location.getLatitude() + ")"))
                .retrieve()
                .body(DmiResponse.class);
    }
    }

