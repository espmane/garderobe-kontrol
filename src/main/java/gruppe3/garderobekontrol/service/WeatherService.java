package gruppe3.garderobekontrol.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    private final ChatClient chat;

    public WeatherService(ChatClient.Builder builder) {
        chat = builder.build();
    }

    public String demo() {
        return chat.prompt()
                .system("Say something insanely cool and funny, like you're a real cool and funny guy.")
                .user("I am such a bored feller, if only someone super funny made a good joke...")
                .call()
                .content();
    }
}
