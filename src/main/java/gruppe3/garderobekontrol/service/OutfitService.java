package gruppe3.garderobekontrol.service;

import gruppe3.garderobekontrol.model.dto.request.OutfitRequest;
import gruppe3.garderobekontrol.model.dto.response.OpenMeteoResponse;
import gruppe3.garderobekontrol.model.dto.response.OutfitResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OutfitService {

    private final ChatClient chat;
    private final WeatherService weatherService;

    public OutfitService(ChatClient.Builder builder, WeatherService weatherService) {
        this.chat = builder.build();
        this.weatherService = weatherService;
    }

    public OutfitResponse recommend(OutfitRequest request) {

        OpenMeteoResponse w = weatherService.getWeather(
                request.latitude(),
                request.longitude()
        );

        return chat.prompt()

                .system("""
                        Du er en garderobeassistent, der hjælper forældre
                        med at vurdere, hvilket tøj deres barn mangler.

                        DIN OPGAVE:
                        Vurder barnets behov for beklædning ud fra vejret,
                        barnets motoriske udviklingsniveau, dagstypen
                        og familiens eksisterende garderobe.

                        MOTORISK UDVIKLINGSNIVEAU:
                        CRAWLING: Barnet kravler. Tag hensyn til bevægelsesfrihed
                        og kontakt med underlaget ud fra om det er indendørs eller udendørs.
                        
                        BEGINNER: Barnet er begyndt at gå. Tag hensyn til,
                        at barnet både kan kravle og gå.
                        
                        STABLE: Barnet går stabilt. Tag hensyn til aktiv
                        bevægelse og leg.

                        DAGSTYPE:
                        INDOOR: Barnet opholder sig primært indendørs.
                        OUTDOOR: Barnet opholder sig primært udendørs.
                        Vurder derfor beklædningsbehovet forskelligt.

                        BEKLÆDNINGSKATEGORIER:
                        HEADWEAR: Hovedbeklædning.
                        ACCESSORIES: Tilbehør.
                        BASE_LAYER: Inderste lag.
                        MIDT_LAYER: Mellemlag.
                        JACKET: Jakker og yderlag.
                        SHIRT: Trøjer.
                        PANTS: Bukser.
                        SHOES: Fodtøj.

                        TILGÆNGELIGE BEKLÆDNINGSDELE:
                        HEADWEAR: CAP, BEANIE, BUCKET_HAT,
                        SUN_HAT, BALACLAVA, HEADBAND.

                        ACCESSORIES: NECK_WARMER, MITTENS,
                        GLOVES, SUN_PROTECTION.

                        BASE_LAYER: BODY, UNDERWEAR, TIGHTS,
                        SOCKS, WOOL_BASE_LAYER.

                        MIDT_LAYER: SHIRT, SWEATER, FLEECE,
                        THERMAL_CLOTHING.

                        JACKET: RAIN_JACKET, WINTER_JACKET,
                        LIGHT_JACKET, THERMAL_JACKET.

                        SHIRT: T_SHIRT, LONG_SLEEVE.

                        PANTS: SHORTS, REGULAR_PANTS,
                        RAIN_PANTS, THERMAL_PANTS.

                        SHOES: SNEAKERS, RAIN_BOOTS, WINTER_BOOTS,
                        SANDALS, THERMAL_BOOTS, INDOOR_SHOES.

                        REGLER FOR ANBEFALINGER:
                        - Sammenlign altid barnets behov med familiens garderobe.
                        - Foreslå ikke beklædningsdele, familien allerede har.
                        - Foreslå kun beklædningsdele, der reelt er nødvendige.
                        - Du behøver ikke foreslå noget fra alle kategorier.
                        - Undgå unødvendige eller overdrevne anbefalinger.
                        - Hvis familien allerede har alt det nødvendige,
                          skal recommendedClothes være en tom liste.
                        - Brug kun de kategorier og beklædningsdele,
                          der er angivet ovenfor.
                        - Returnér enum-værdierne så de giver mening, så de ikke har underscores, men er i singularis og i ental. F.eks. "Beanie" i stedet for "BEANIE".

                        OUTPUT:
                        Returnér en struktureret OutfitResponse.
                        Hver anbefaling skal indeholde category, item og reason.
                        explanation skal kort forklare den samlede vurdering.
                        Svar på dansk.
                        """)

                .user("""
                        Vejrforhold:
                        Vejrbeskrivelse: %s
                        Temperatur: %.1f grader
                        Luftfugtighed: %d procent
                        Vindhastighed: %.1f m/s
                        Nedbør: %.1f mm
                        UV-indeks: %.1f
                        Snefald: %.1f cm

                        Barnets motoriske udviklingsniveau: %s
                        Dagstype: %s

                        Familiens eksisterende garderobe:
                        %s

                        Vurder hvilke beklædningsdele familien mangler.
                        """.formatted(
                        OpenMeteoResponse.describe(w.daily().weatherCode().getFirst()),
                        w.hourly().temperature().getFirst(),
                        w.hourly().humidity().getFirst(),
                        w.hourly().windSpeed().getFirst(),
                        w.daily().precipitation().getFirst(),
                        w.daily().uvIndex().getFirst(),
                        w.daily().snowfall().getFirst(),
                        request.activity(),
                        request.dayType(),
                        request.availableClothes()
                ))

                .call()
                .entity(OutfitResponse.class,
                        ChatClient.EntityParamSpec::useProviderStructuredOutput);
    }
}
