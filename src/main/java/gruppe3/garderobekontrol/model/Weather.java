package gruppe3.garderobekontrol.model;

public record Weather(
        double temp, WeatherCondition weatherCondition,
        double rainRate, double snowRate, // DMI giver os mærkelige mål, så skal ændres til at være menneske læseligt
        double windSpeed, double windGust, // DMI giver os m/s hastighed
        CloudCover cloudCover, Sunshine sunshine) {
}
