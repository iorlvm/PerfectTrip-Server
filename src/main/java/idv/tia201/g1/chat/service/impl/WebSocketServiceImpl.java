package idv.tia201.g1.chat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.tia201.g1.chat.service.CacheService;
import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.chat.service.WebSocketService;
import idv.tia201.g1.dto.MessageDTO;
import idv.tia201.g1.dto.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

import static idv.tia201.g1.chat.utils.Utils.isImageEmpty;
import static idv.tia201.g1.utils.Constants.*;

@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChatService chatService;
    @Autowired
    private CacheService cacheService;

    @Override
    public PayloadDTO handlePayload(String role, Integer id, PayloadDTO payloadDTO) {
        String action = payloadDTO.getAction();
        // 驗證action值是否合法
        switch (action) {
            case CHAT_ACTION_SEND_MESSAGE:
            case CHAT_ACTION_READ_MESSAGE:
            case CHAT_ACTION_UPDATE_ROOM_INFO:
                break;
            default:
                throw new IllegalArgumentException("參數異常: 未定義的操作");
        }

        Long chatId = payloadDTO.getChatId();
        // 驗證chatId是否存在
        if (cacheService.isChatRoomInvalid(chatId)) {
            throw new IllegalArgumentException("參數異常: 聊天室不存在");
        }

        Long authorId = chatService.getOrCreateMappingUserId(role, id);
        // 驗證authorId是否是這個chatId的參與者
        if (cacheService.isParticipantNotFound(authorId, chatId)) {
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

        // 將兇手(?)以及目的地寫入dto中
        payloadDTO.setAuthorId(authorId);
        payloadDTO.setChatId(chatId);

        // 根據action執行對應的操作
        switch (action) {
            case CHAT_ACTION_SEND_MESSAGE:
                processPayloadToSendMessage(payloadDTO);
                break;
            case CHAT_ACTION_READ_MESSAGE:
                processPayloadToReadMessage(payloadDTO);
                break;
            case CHAT_ACTION_UPDATE_ROOM_INFO:
                processPayloadToUpdateRoomInfo(payloadDTO);
                break;
        }

        return payloadDTO;
    }


    private void processPayloadToSendMessage(PayloadDTO payloadDTO) {
        String content = payloadDTO.getContent();
        MessageDTO messageDTO = null;
        try {
            messageDTO = objectMapper.readValue(content, MessageDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 檢查是否有發送任何訊息
        if (messageDTO.getContent() == null && isImageEmpty(messageDTO.getImg())) {
            throw new IllegalArgumentException("請求異常：沒有發送任何內容");
        }

        cacheService.saveMessage(payloadDTO.getAuthorId(), payloadDTO.getChatId(), messageDTO);
        try {
            payloadDTO.setContent(objectMapper.writeValueAsString(messageDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        payloadDTO.setTimestamp(messageDTO.getTimestamp());
    }

    private void processPayloadToReadMessage(PayloadDTO payloadDTO) {
        Timestamp now = Timestamp.from(Instant.now());
        payloadDTO.setTimestamp(now.toString());
        Long chatId = payloadDTO.getChatId();
        Long authorId = payloadDTO.getAuthorId();
        cacheService.updateLastReadingAt(chatId, authorId, now);
    }

    private void processPayloadToUpdateRoomInfo(PayloadDTO payloadDTO) {
        Timestamp now = Timestamp.from(Instant.now());
        Long chatId = payloadDTO.getChatId();
        String content = payloadDTO.getContent();

        PayloadDTO.RoomInfo roomInfo = null;
        try {
            roomInfo = objectMapper.readValue(content, PayloadDTO.RoomInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        cacheService.updateChatInfo(chatId, roomInfo.getChatName(), roomInfo.getPhoto());
        payloadDTO.setTimestamp(now.toString());
    }

}
