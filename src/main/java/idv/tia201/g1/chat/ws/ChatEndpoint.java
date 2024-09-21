package idv.tia201.g1.chat.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.tia201.g1.chat.event.UserUpdateEvent;
import idv.tia201.g1.chat.service.CacheService;
import idv.tia201.g1.chat.service.WebSocketService;
import idv.tia201.g1.chat.dto.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


@Component
public class ChatEndpoint extends TextWebSocketHandler {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private WebSocketService webSocketService;
    private static final Map<Long, Set<WebSocketSession>> SESSIONS_MAP = new ConcurrentHashMap<>();
    private static final Set<WebSocketSession> SESSIONS_BROADCAST = new CopyOnWriteArraySet<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer id = (Integer) session.getAttributes().get("id");
        String role = (String) session.getAttributes().get("role");

        // 參加廣播頻道 (系統通知用)
        SESSIONS_BROADCAST.add(session);

        // 連線時獲取所有參予中的聊天室
        Set<Long> chatRoomsIdByRoleAndId = cacheService.getChatRoomIdsByRoleAndId(role, id);
        for (Long chatId : chatRoomsIdByRoleAndId) {
            // 在所有參予中的聊天室建立連線
            Set<WebSocketSession> webSocketSessions = SESSIONS_MAP.get(chatId);
            if (webSocketSessions == null) {
                webSocketSessions = Collections.synchronizedSet(new HashSet<>());
            }
            webSocketSessions.add(session);
            SESSIONS_MAP.put(chatId, webSocketSessions);
        }

        // TODO: 開啟聊天室時還要做啥, 寫在這
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Integer id = (Integer) session.getAttributes().get("id");
        String role = (String) session.getAttributes().get("role");

        String payload = message.getPayload();
        PayloadDTO payloadDTO = objectMapper.readValue(payload, PayloadDTO.class);

        // 處理 payload
        payloadDTO = webSocketService.handlePayload(role, id, payloadDTO);

        Long chatId = payloadDTO.getChatId();
        Set<WebSocketSession> sessions = SESSIONS_MAP.get(chatId);
        if (sessions != null) {
            broadcast(payloadDTO, sessions);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer id = (Integer) session.getAttributes().get("id");
        String role = (String) session.getAttributes().get("role");

        // 退出廣播頻道 (系統通知用)
        SESSIONS_BROADCAST.remove(session);

        // 獲取所有參予中的聊天室
        Set<Long> chatRoomsIdByRoleAndId = cacheService.getChatRoomIdsByRoleAndId(role, id);
        // 移除所有參予中的聊天室連線
        for (Long chatId : chatRoomsIdByRoleAndId) {
            Set<WebSocketSession> webSocketSessions = SESSIONS_MAP.get(chatId);
            if (webSocketSessions != null) {
                webSocketSessions.remove(session);
                if (webSocketSessions.isEmpty()) {
                    SESSIONS_MAP.remove(chatId);
                }
            }
        }

        // TODO: 關閉聊天室時還要做啥, 寫在這
    }

    private void broadcast(PayloadDTO payloadDTO, Set<WebSocketSession> sessions) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(payloadDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        for (WebSocketSession session : sessions) {
            try {
                // 上鎖避免TEXT_PARTIAL_WRITING錯誤 (因為傳輸訊息後的秒讀操作, 會造成幾乎同時的多筆讀取請求)
                synchronized (session) {
                    session.sendMessage(new TextMessage(json));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventListener
    public void handleUserUpdateEvent(UserUpdateEvent event) {
        PayloadDTO payloadDTO = event.getPayloadDTO();

        broadcast(payloadDTO, SESSIONS_BROADCAST);
    }
}
