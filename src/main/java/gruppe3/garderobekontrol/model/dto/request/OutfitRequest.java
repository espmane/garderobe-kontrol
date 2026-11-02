package gruppe3.garderobekontrol.model.dto.request;

import gruppe3.garderobekontrol.model.Outfit;
import gruppe3.garderobekontrol.model.enums.Activity;
import gruppe3.garderobekontrol.model.enums.DayType;

import java.time.LocalDate;

public record OutfitRequest(
        String city,
        LocalDate date,
        Activity activity,
        DayType dayType,
        Outfit availableClothes
) {
}
