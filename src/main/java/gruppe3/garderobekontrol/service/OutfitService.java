package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.Outfit;
import gruppe3.garderobekontrol.model.dto.request.OutfitRequest;
import gruppe3.garderobekontrol.model.dto.response.OpenMeteoResponse;
import gruppe3.garderobekontrol.model.dto.response.OutfitResponse;
import gruppe3.garderobekontrol.model.enums.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutfitService {

    private final ChatClient chat;
    private final WeatherService weatherService;

    public OutfitService(ChatClient.Builder builder, WeatherService weatherService) {
        this.chat = builder.build();
        this.weatherService = weatherService;
    }

    public OutfitResponse recommend(OutfitRequest request) {
        OpenMeteoResponse w = weatherService.getWeather();

        boolean cold = w.hourly().temperature().getFirst() < 5;
        boolean rain = w.daily().precipitation().getFirst() > 1.0;   // mm på hele dagen

        Outfit outfit = new Outfit(
                cold ? Headwear.BEANIE : Headwear.CAP,
                cold ? Accessories.NECK_WARMER : Accessories.SUN_PROTECTION,
                BaseLayer.WOOL_BASE_LAYER,
                MidtLayer.FLEECE,
                cold ? Jacket.WINTER_JACKET : rain ? Jacket.RAIN_JACKET : Jacket.LIGHT_JACKET,
                cold ? Shirt.LONG_SLEEVE : Shirt.T_SHIRT,
                cold ? Pants.THERMAL_PANTS : rain ? Pants.RAIN_PANTS : Pants.REGULAR_PANTS,
                rain ? Shoes.RAIN_BOOTS : Shoes.SNEAKERS);

        return chat.prompt()
                .system("Du hjælper forældre med at klæde børn på. Svar på dansk i 2-3 korte sætninger.")
                .user("Vejret: %s, %.1f grader, vind %.1f m/s, nedbør %.1f mm. Barnets aktivitet er %s. Tøjet er %s. Forklar hvorfor det passer."
                        .formatted(OpenMeteoResponse.describe(w.daily().weatherCode().getFirst()), w.hourly().temperature().getFirst(), w.hourly().windSpeed().getFirst(),
                                w.daily().precipitation().getFirst(), request.activity(), outfit))
                .call()
                .entity(OutfitResponse.class, ChatClient.EntityParamSpec::useProviderStructuredOutput);
    }
}


