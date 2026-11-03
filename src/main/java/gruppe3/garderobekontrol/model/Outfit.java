package gruppe3.garderobekontrol.model;

import gruppe3.garderobekontrol.model.enums.*;

public record Outfit(
        Headwear headwear,
        Accessories accessories,
        BaseLayer baseLayer,
        MidtLayer midtLayer,
        Jacket jacket,
        Shirt shirt,
        Pants pants,
        Shoes shoes
) {
}
