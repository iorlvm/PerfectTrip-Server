package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomChatParticipantDao {
    List<ChatParticipant> findByChatId(Long chatId);

    Page<Long> findChatIdByTypeAndRefId(String type, Integer refId, Pageable pageable);
}
