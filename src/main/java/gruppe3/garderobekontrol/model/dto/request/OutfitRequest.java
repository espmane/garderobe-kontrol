package gruppe3.garderobekontrol.model.dto.request;

import gruppe3.garderobekontrol.model.Outfit;
import gruppe3.garderobekontrol.model.enums.DailyActivity;
import gruppe3.garderobekontrol.model.enums.MobilityLevel;
import gruppe3.garderobekontrol.model.enums.OutdoorTime;

import java.util.List;

public record OutfitRequest(
        double latitude,
        double longitude,
        MobilityLevel mobilityLevel,
        OutdoorTime outdoorTime,
        List<DailyActivity> dailyActivities,
        Outfit availableClothes
) {
}
