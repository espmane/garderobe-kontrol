package gruppe3.garderobekontrol.model.dto.request;

import gruppe3.garderobekontrol.model.Outfit;
import gruppe3.garderobekontrol.model.enums.Activity;
import gruppe3.garderobekontrol.model.enums.DayType;

public record OutfitRequest(
        double latitude,
        double longitude,
        Activity activity,
        DayType dayType,
        boolean creativeDay,
        boolean extraClothes,
        Outfit availableClothes
) {
}
