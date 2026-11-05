package gruppe3.garderobekontrol.service

import gruppe3.garderobekontrol.websocket.WebSocketHandler
import org.springframework.stereotype.Service

@Service
final class WebSocketService(private val handler: WebSocketHandler) {
    final fun broadcast(event: String) {
        handler.broadcast(event)
    }
}