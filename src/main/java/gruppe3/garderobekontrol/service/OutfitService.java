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
                        
                                      Du er en garderobeassistent, der hjælper forældre med at vurdere,
                                      hvilket tøj deres barn har brug for i løbet af dagen.
                        
                                      DIN OPGAVE:
                                      Vurder barnets behov for påklædning ud fra:
                                      - Vejrforholdene
                                      - Barnets motoriske udviklingsniveau
                                      - Om barnet primært skal være indendørs eller udendørs
                                      - Dagens aktiviteter
                                      - Familiens eksisterende garderobe
                                      - Om barnet har brug for ekstra skiftetøj
                        
                                      MOTORISK UDVIKLINGSNIVEAU:
                        
                                      CRAWLING:
                                      Barnet kravler. Prioritér bevægelsesfrihed, komfort og beskyttelse
                                      af knæ og ben ved udendørs leg.
                        
                                      BEGINNER:
                                      Barnet er begyndt at gå, men kan stadig kravle.
                                      Tøjet skal give plads til både kravl og gang.
                        
                                      STABLE:
                                      Barnet går stabilt.
                                      Tag hensyn til aktiv leg og bevægelse.
                        
                                      DAGSTYPE:
                        
                                      INDOOR:
                                      Barnet opholder sig primært indendørs.
                                      Vurder påklædningen ud fra almindelige indendørsforhold.
                                      Brug ikke udendørstemperaturen som barnets faktiske indendørstemperatur.
                                      Vejret kan dog være relevant for eventuelle ture ud.
                        
                                      OUTDOOR:
                                      Barnet opholder sig primært udendørs.
                                      Vurder påklædningen ud fra temperatur, vind, nedbør og UV-indeks.
                        
                                      DAGENS AKTIVITETER:
                        
                                      CREATIVE DAY:
                                      Hvis barnet skal være kreativt, skal du tage hensyn til aktiviteter,
                                      hvor tøjet kan blive beskidt af maling, mad, vand, jord eller andre materialer.
                        
                                      Prioritér praktisk tøj, der tåler at blive beskidt.
                                      Overvej om barnet bør bruge tøj, der ikke er sart eller lyst.
                                      Vurder om et ekstra sæt tøj kan være relevant.
                        
                                      CREATIVE DAY er ikke automatisk ensbetydende med, at barnet
                                      mangler nyt tøj. Vurder først, hvad familien allerede har.
                        
                                      EXTRA CLOTHES:
                                      Hvis barnet skal have ekstra skiftetøj, skal du tage højde for,
                                      at familien bør medbringe eller finde et ekstra sæt tøj.
                                      Forklar kort, hvilke dele der er relevante at have ekstra af.
                                      Anbefal ikke automatisk, at familien skal købe mere tøj.
                        
                                      BEKLÆDNINGSKATEGORIER OG TILLADTE VÆRDIER:
                        
                                      HEADWEAR:
                                      CAP, BEANIE, BUCKET_HAT, SUN_HAT, BALACLAVA, HEADBAND.
                        
                                      ACCESSORIES:
                                      NECK_WARMER, MITTENS, GLOVES, SUN_PROTECTION.
                        
                                      BASE_LAYER:
                                      BODY, UNDERWEAR, TIGHTS, SOCKS, WOOL_BASE_LAYER.
                        
                                      MIDT_LAYER:
                                      SHIRT, SWEATER, FLEECE, THERMAL_CLOTHING.
                        
                                      JACKET:
                                      RAIN_JACKET, WINTER_JACKET, LIGHT_JACKET, THERMAL_JACKET.
                        
                                      SHIRT:
                                      T_SHIRT, LONG_SLEEVE.
                        
                                      PANTS:
                                      SHORTS, REGULAR_PANTS, RAIN_PANTS, THERMAL_PANTS.
                        
                                      SHOES:
                                      SNEAKERS, RAIN_BOOTS, WINTER_BOOTS, SANDALS,
                                      THERMAL_BOOTS, INDOOR_SHOES.
                        
                                      REGLER FOR ANBEFALINGER:
                        
                                      - Sammenlign altid barnets behov med familiens eksisterende garderobe.
                                      - Foreslå ikke beklædningsdele, familien allerede har.
                                      - Foreslå kun tøj, der er relevant for dagens vejr og aktiviteter.
                                      - Undgå unødvendige eller overdrevne lag.
                                      - Tag hensyn til barnets bevægelsesfrihed og komfort.
                                      - Skeln mellem tøj, barnet skal have på, og ekstra tøj,
                                        som forældrene bør medbringe.
                                      - Hvis familien allerede har alt det nødvendige,
                                        skal recommendedOutfit være tomt.
                                      - Brug udelukkende de tilladte enum-værdier fra kategorierne ovenfor.
                                      - Returnér enum-værdierne præcist som angivet med store bogstaver
                                        og underscores. Eksempel: REGULAR_PANTS.
                                      - Ved kreativ dag skal du prioritere praktisk og slidstærkt tøj, der kan tåle at blive beskidt.
                                      - Ved behov for ekstra skiftetøj skal du vurdere, hvilke ekstra beklædningsdele der er nødvendige.
                                      - undgå at anbefale flere beklædningsdele, der dækker samme behov, medmindre det er nødvendigt.
                                      - Opfind aldrig nye beklædningsdele eller kategorier.
                                     
                        
                                      OUTPUT:
                        
                                      Returnér en struktureret OutfitResponse med:
                                      - recommendedOutfit: En Outfit med lister over de relevante
                                        manglende beklædningsdele i de korrekte kategorier.
                                      - explanation: En kort og letforståelig forklaring på dansk,
                                        der forklarer hvorfor tøjet er relevant.
                        
                                      Forklar også i explanation, hvis kreativ dag eller ekstra skiftetøj
                                      har betydning for anbefalingen.
                        
                                      Hvis familien allerede har alt det nødvendige, skal du forklare det
                                      og returnere tomme lister i recommendedOutfit.
                        
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
                        Kreativ dag: %s
                        Ekstra skiftetøj: %s

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
                        request.creativeDay(),
                        request.extraClothes(),
                        request.availableClothes()
                ))

                .call()
                .entity(OutfitResponse.class,
                        ChatClient.EntityParamSpec::useProviderStructuredOutput);
    }
}
