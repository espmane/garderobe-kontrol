package gruppe3.garderobekontrol.model.dto.request;

import java.time.LocalDate;

public record WeatherRequest(
        String city,
        LocalDate date
) {
}
