package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatParticipant;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface CustomChatParticipantDao {
    List<ChatParticipant> findByChatIdToDTO(Long chatId);

    List<Long> findChatIdByTypeAndRefId(String type, Integer refId, int size, Timestamp earliestTimestamp);

    Set<Long> findChatIdByTypeAndRefId(String type, Integer refId);
}
