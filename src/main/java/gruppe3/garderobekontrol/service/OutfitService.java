package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.Outfit;
import gruppe3.garderobekontrol.model.Weather;
import gruppe3.garderobekontrol.model.dto.request.OutfitRequest;
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
        Weather w = weatherService.getWeather();

        boolean cold = w.temp() < 5;
        boolean rain = w.rainRate() > 0.1;

    Outfit outfit = new Outfit(
            cold ? Headwear.HUE : Headwear.KASKET,
            cold ? Jacket.WINTER_JACKET : rain ? Jacket.RAIN_JACKET : Jacket.LIGHT_JACKET,
            cold ? Shirt.SWEATER : Shirt.LONG_SLEEVE,
            cold ? Pants.WINTER_PANTS : rain ? Pants.RAIN_PANTS : Pants.REGULAR_PANTS,
            rain ? Shoes.RAIN_BOOTS : Shoes.SNEAKERS);


    String demo = chat.prompt()
                .system("Say something insanely cool and funny, like you're a real cool and funny guy.")
                .user("I am such a bored feller, if only someone super funny made a good joke..."
                        .formatted(w.temp(), w.windSpeed(), w.rainRate(), request.activity(), outfit))
                .call()
                .content();
    return new OutfitResponse(outfit, List.of(), demo);
    }
}
