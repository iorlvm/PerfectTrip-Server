package idv.tia201.g1.authentication.filter;
import idv.tia201.g1.authentication.service.TokenService;
import idv.tia201.g1.authentication.service.UserAuth;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class ChatHandshakeInterceptor implements HandshakeInterceptor {
    private final TokenService tokenService;

    public ChatHandshakeInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 握手時進行驗證
        String token = request.getURI().getQuery();
        if (token != null) {

            UserAuth userAuth = tokenService.validateToken(token);
            if (userAuth != null) {
                String role = userAuth.getRole();
                Integer id = userAuth.getId();
                attributes.put("id", id);
                attributes.put("role", role);
                return true;
            }
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 握手後處理
        // TODO: 預載入相關的聊天室到Redis中?
    }
}
