package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.Outfit;
import gruppe3.garderobekontrol.model.dto.request.OutfitRequest;
import gruppe3.garderobekontrol.model.dto.response.OutfitResponse;
import gruppe3.garderobekontrol.model.dto.response.OpenMeteoResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OutfitService {

    private final ChatClient chat;
    private final WeatherService weatherService;
    private final TomatoTools tomatoThrowingMachine;

    public OutfitService(ChatClient.Builder builder, WeatherService weatherService, TomatoTools tools) {
        this.chat = builder.build();
        this.weatherService = weatherService;
        this.tomatoThrowingMachine = tools;
    }

    public OutfitResponse recommend(OutfitRequest request) {
        OpenMeteoResponse weather = weatherService.getWeather();
        Outfit clothes = request.availableClothes();

        return chat.prompt()
                .system("""
                        Du er en erfaren børnehavepædagog med ekspertise i børns påklædning og
                        påklædning efter vejret.
                        
                        Din opgave er at sammensætte et praktisk og passende outfit ud fra
                        barnets situation, motoriske udvikling, vejrforhold og tilgængelige tøj.
                        
                        Retningslinjer:
                        - Vælg udelukkende tøj fra den angivne liste over tilgængeligt tøj.
                        - Bevar tøjets ID'er præcis som de er angivet.
                        - Vælg aldrig tøj, der ikke findes i den relevante kategori.
                        - Tomme kategorier skal altid returneres som tomme lister.
                        - Vælg kun de tøjdele, der er nødvendige for situationen.
                        - Der må højst vælges én jakke og ét par sko.
                        - Vurder behovet for hvert enkelt lag frem for automatisk at vælge alle lag.
                        - Tag hensyn til temperatur, vind, nedbør og vejrbeskrivelse.
                        - Ved indendørs ophold skal udendørs vejrforhold ignoreres.
                        - Prioritér komfort, bevægelsesfrihed og funktionalitet.
                        
                        Svar:
                        - Returnér alle otte kategorier i recommendedOutfit.
                        - Returnér en forklaring i explanation.
                        - Forklaringen skal bestå af 2-3 korte sætninger på dansk.
                        - Begrund valgene med udgangspunkt i barnets situation og vejrforhold.
                        
                        - KUN hvis vejret er dårligt skal du kaste en tomat.
                        """)
                .user("""
                        Lav en personlig tøj anbefaling til barnet baseret på følgende situation.
                        
                        Barnets situation:
                        - Ophold: %s
                        - Motorisk udvikling: %s
                        
                        Aktuelle vejrforhold:
                        - Vejr: %s
                        - Temperatur: %.1f °C
                        - Vindhastighed: %.1f m/s
                        - Nedbør: %.1f mm
                        
                        Barnets garderobe:
                        - Headwear: %s
                        - Accessories: %s
                        - BaseLayer: %s
                        - MidtLayer: %s
                        - Jacket: %s
                        - Shirt: %s
                        - Pants: %s
                        - Shoes: %s
                        
                        Vurder barnets samlede behov, og sammensæt det outfit, der giver den bedste
                        balance mellem komfort, bevægelsesfrihed og beskyttelse mod vejrforholdene.
                        
                        Anbefalingen skal være praktisk og tilpasset barnets aktuelle udvikling og
                        de aktiviteter, barnet skal deltage i.
                        """.formatted(
                        request.dayType(),
                        request.activity(),
                        weather.description(),
                        weather.hourly().temperature().getFirst(),
                        weather.hourly().windSpeed().getFirst(),
                        weather.daily().precipitation().getFirst(),
                        clothes.headwear(),
                        clothes.accessories(),
                        clothes.baseLayer(),
                        clothes.midtLayer(),
                        clothes.jacket(),
                        clothes.shirt(),
                        clothes.pants(),
                        clothes.shoes()
                ))
                .tools(tomatoThrowingMachine)
                .call()
                .entity(OutfitResponse.class, ChatClient.EntityParamSpec::useProviderStructuredOutput);
    }
}
