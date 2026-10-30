package gruppe3.garderobekontrol.model;

public record Weather(
        double temp, WeatherCondition weatherCondition,
        double rainRate, double snowRate, // regn og sne skal fikses op
        double windSpeed, double windGust, // vind skal ogs fikses
        CloudCover cloudCover, Sunshine sunshine) {
}
