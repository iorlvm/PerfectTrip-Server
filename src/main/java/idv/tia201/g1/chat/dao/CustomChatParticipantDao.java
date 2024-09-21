package idv.tia201.g1.chat.dao;

import idv.tia201.g1.chat.entity.ChatParticipant;

import java.sql.Timestamp;
import java.util.List;

public interface CustomChatParticipantDao {
    List<ChatParticipant> findByChatId(Long chatId);

    List<Long> findChatIdByTypeAndRefId(String type, Integer refId, int size, Timestamp earliestTimestamp);

    Long findChatIdByTwoUserIds (Long userId1, Long userId2);
}