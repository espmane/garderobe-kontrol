package gruppe3.garderobekontrol.model.dto.response;

import gruppe3.garderobekontrol.model.Outfit;

public record OutfitRecommendationResponse(
        Outfit recommendedOutfit,
        String explanation
) {
}
