package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.Outfit;
import gruppe3.garderobekontrol.model.dto.request.OutfitRequest;
import gruppe3.garderobekontrol.model.dto.response.OutfitResponse;
import gruppe3.garderobekontrol.model.dto.response.WeatherResponse;
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
        WeatherResponse w = weatherService.getWeather();

        boolean cold = w.temperature() < 5;
        boolean rain = w.precipitationAmount() > 1.0;   // mm på hele dagen

        Outfit outfit = new Outfit(
                cold ? Headwear.HUE : Headwear.KASKET,
                cold ? Jacket.WINTER_JACKET : rain ? Jacket.RAIN_JACKET : Jacket.LIGHT_JACKET,
                cold ? Shirt.SWEATER : Shirt.LONG_SLEEVE,
                cold ? Pants.WINTER_PANTS : rain ? Pants.RAIN_PANTS : Pants.REGULAR_PANTS,
                rain ? Shoes.RAIN_BOOTS : Shoes.SNEAKERS);

        String explanation = chat.prompt()
                .system("Du hjælper forældre med at klæde børn på. Svar på dansk i 2-3 korte sætninger.")
                .user("Vejret: %s, %.1f grader, vind %.1f m/s, nedbør %.1f mm. Barnets aktivitet er %s. Tøjet er %s. Forklar hvorfor det passer."
                        .formatted(w.weatherDescription(), w.temperature(), w.windSpeed(),
                                w.precipitationAmount(), request.activity(), outfit))
                .call()
                .content();

        return new OutfitResponse(outfit, List.of(), explanation);
    }
}


