package gruppe3.garderobekontrol.model.dto.response;

import gruppe3.garderobekontrol.model.Outfit;

import java.util.List;

public record OutfitResponse(
        Outfit recommendedOutfit,
        List<String> missingClothes,
        String explanation
) {
}
