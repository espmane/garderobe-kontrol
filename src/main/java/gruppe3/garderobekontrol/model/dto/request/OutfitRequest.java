package gruppe3.garderobekontrol.model.dto.request;

import gruppe3.garderobekontrol.model.ClothingItem;
import gruppe3.garderobekontrol.model.enums.Activity;
import gruppe3.garderobekontrol.model.enums.DayType;

import java.time.LocalDate;
import java.util.List;

public record OutfitRequest(
        double latitude,
        double longitude,
        LocalDate date,
        Activity activity,
        DayType dayType,
        List<ClothingItem> availableClothes
) {
}
