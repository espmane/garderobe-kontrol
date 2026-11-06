package gruppe3.garderobekontrol.service

import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service

@Service
final class TomatoTools(private val webSocketService: WebSocketService) {
    @Tool(description = "Throws a tomato at the user")
    final fun throwTomato() {
        webSocketService.broadcast("tomato")
    }
}
