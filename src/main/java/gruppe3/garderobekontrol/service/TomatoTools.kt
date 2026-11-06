package gruppe3.garderobekontrol.service

import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service

@Service
final class TomatoTools(private val webSocketService: WebSocketService) {
    @Tool(description = "Kast en tomat som en humoristisk reaktion på virkelig dårligt vejr.")
    final fun throwTomato() {
        webSocketService.broadcast("tomato")
    }
}
