package gruppe3.garderobekontrol.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(Hourly hourly, Daily daily) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Hourly(
            @JsonProperty("temperature_2m") List<Double> temperature,
            @JsonProperty("relative_humidity_2m") List<Integer> humidity,
            @JsonProperty("wind_speed_10m") List<Double> windSpeed
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Daily(
            @JsonProperty("uv_index_max") List<Double> uvIndex,
            @JsonProperty("precipitation_sum") List<Double> precipitation,
            @JsonProperty("snowfall_sum") List<Double> snowfall,
            @JsonProperty("weather_code") List<Integer> weatherCode
    ) {}
}