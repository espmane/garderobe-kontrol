package gruppe3.garderobekontrol.model.dto.response;

public record WeatherResponse(
        double temperature,
        double windSpeed,
        int humidity,
        double uvIndex,
        double precipitationAmount,
        double snowFall
)

{
}
