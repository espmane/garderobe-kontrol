package gruppe3.garderobekontrol.model;

import gruppe3.garderobekontrol.model.enums.*;

import java.util.List;

public record Outfit(
        List<Headwear> headwear,
        List<Accessories> accessories,
        List<BaseLayer> baseLayer,
        List<MidtLayer> midtLayer,
        List<Jacket> jacket,
        List<Shirt> shirt,
        List<Pants> pants,
        List<Shoes> shoes
) {
}
