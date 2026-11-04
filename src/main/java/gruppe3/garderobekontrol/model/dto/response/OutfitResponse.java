package gruppe3.garderobekontrol.model.dto.response;

import gruppe3.garderobekontrol.model.dto.ClothingSuggestion;

import java.util.List;

public record OutfitResponse(
        List<ClothingSuggestion> recommendedClothes,
        String explanation
) {
}
