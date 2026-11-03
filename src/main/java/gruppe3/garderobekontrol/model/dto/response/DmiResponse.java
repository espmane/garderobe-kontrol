package gruppe3.garderobekontrol.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DmiResponse(List<Feature> features) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Feature(Properties properties) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Properties(
                @JsonProperty("temperature-2m") double temperature,
                @JsonProperty("precipitation-type") int precipitationType,
                @JsonProperty("rain-precipitation-rate") double rainRate,
                @JsonProperty("total-snowfall-rate-water-equivalent") double snowRate,
                @JsonProperty("fraction-of-cloud-cover") double cloudCover,
                @JsonProperty("global-radiation-flux") double radiation,
                @JsonProperty("wind-speed-10m") double windSpeed,
                @JsonProperty("gust-wind-speed-10m") double windGust
        ) {
        }}