package idv.tia201.g1.core.config;
import idv.tia201.g1.core.filter.ChatHandshakeInterceptor;
import idv.tia201.g1.chat.ws.ChatEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatHandshakeInterceptor chatHandshakeInterceptor;
    private final ChatEndpoint chatEndpoint;

    public WebSocketConfig(ChatHandshakeInterceptor chatHandshakeInterceptor, ChatEndpoint chatEndpoint) {
        this.chatHandshakeInterceptor = chatHandshakeInterceptor;
        this.chatEndpoint = chatEndpoint;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatEndpoint, "/chat")
                .addInterceptors(chatHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}