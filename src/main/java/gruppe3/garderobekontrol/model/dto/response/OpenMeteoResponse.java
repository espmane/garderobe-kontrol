package gruppe3.garderobekontrol.model.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(Hourly hourly, Daily daily) {

    @JsonGetter("description")
    public String description() {
        return daily.weatherCode().stream()
                .findFirst()
                .map(OpenMeteoResponse::describe)
                .orElse("Ukendt");
    }

    public record Hourly(
            @JsonAlias("temperature_2m") List<Double> temperature,
            @JsonAlias("relative_humidity_2m") List<Integer> humidity,
            @JsonAlias("wind_speed_10m") List<Double> windSpeed
    ) {
    }

    public record Daily(
            @JsonAlias("uv_index_max") List<Double> uvIndex,
            @JsonAlias("precipitation_sum") List<Double> precipitation,
            @JsonAlias("snowfall_sum") List<Double> snowfall,
            @JsonAlias("weather_code") List<Integer> weatherCode
    ) {
    }

    public static String describe(int code) {
        System.out.println(code);
        return switch (code) {
            case 0 -> "Klart";
            case 1 -> "Overvejende klart";
            case 2 -> "Delvist skyet";
            case 3 -> "Overskyet";
            case 45 -> "Tåge";
            case 48 -> "Rimtåge";
            case 51 -> "Let støvregn";
            case 53 -> "Moderat støvregn";
            case 55 -> "Kraftig støvregn";
            case 56 -> "Let underafkølet støvregn";
            case 57 -> "Kraftig underafkølet støvregn";
            case 61 -> "Let regn";
            case 63 -> "Moderat regn";
            case 65 -> "Kraftig regn";
            case 66 -> "Let underafkølet regn";
            case 67 -> "Kraftig underafkølet regn";
            case 71 -> "Let snefald";
            case 73 -> "Moderat snefald";
            case 75 -> "Kraftigt snefald";
            case 77 -> "Snefnug";
            case 80 -> "Lette regnbyger";
            case 81 -> "Moderate regnbyger";
            case 82 -> "Kraftige regnbyger";
            case 85 -> "Lette snebyger";
            case 86 -> "Kraftige snebyger";
            case 95 -> "Tordenvejr";
            case 96 -> "Tordenvejr med let hagl";
            case 97 -> "Kraftigt tordenvejr";
            case 99 -> "Tordenvejr med kraftigt hagl";
            default -> "Ukendt";
        };
    }
}