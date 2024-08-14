package idv.tia201.g1.chat.service;

import idv.tia201.g1.dto.MessageDTO;
import idv.tia201.g1.entity.ChatParticipant;
import idv.tia201.g1.entity.ChatRoom;
import idv.tia201.g1.entity.ChatUserMapping;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface CacheService {

    Long findMappingUserId(String role, Integer id);

    ChatUserMapping createUserMapping(String type, Integer id);

    boolean isChatRoomInvalid(Long chatId);

    ChatRoom findChatRoomByChatId(Long chatId);

    List<ChatParticipant> getChatParticipantsByChatId(Long chatId);

    boolean isParticipantNotFound(Long mappingUserId, Long chatId);

    Set<Long> getChatRoomsIdByRoleAndId(String role, Integer id);

    void updateLastReadingAt(Long chatId, Long mappingUserId, Timestamp now);

    void updateChatInfo(Long chatId, String chatName, String chatPhoto);

    MessageDTO saveMessage(Long senderId, Long chatId, MessageDTO messageDTO);
}
