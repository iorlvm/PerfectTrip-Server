package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageDao extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByChatId(Long chatId, Pageable pageable);
}
