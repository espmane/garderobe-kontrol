package gruppe3.garderobekontrol.model;

import gruppe3.garderobekontrol.model.enums.wardrobe.*;

import java.util.List;

public record Outfit(
        List<Headwear> headwear,
        List<Accessories> accessories,
        List<BaseLayer> baseLayer,
        List<MidLayer> midLayer,
        List<OuterLayer> outerLayer,
        List<Pants> pants,
        List<Shoes> shoes
) {
}
