package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface CustomChatParticipantDao {
    List<ChatParticipant> findByChatId(Long chatId);

    Page<Long> findChatIdByTypeAndRefId(String type, Integer refId, Pageable pageable);

    Set<Long> findChatIdByTypeAndRefId(String type, Integer refId);
}
