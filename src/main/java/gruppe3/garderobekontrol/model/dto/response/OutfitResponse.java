package gruppe3.garderobekontrol.model.dto.response;

import gruppe3.garderobekontrol.model.Outfit;

public record OutfitResponse(
        Outfit recommendedOutfit,
        String explanation
) {
}
