package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatMessage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageDao extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByChatId(Long chatId, Pageable pageable);

    @Query(value = "SELECT cm FROM ChatMessage cm WHERE cm.chatId = :chatId AND cm.messageId < :messageId ORDER BY cm.messageId DESC")
    List<ChatMessage> findByChatIdAndMessageIdLessThan(
            @Param("chatId") Long chatId,
            @Param("messageId") Long messageId,
            Pageable pageable
    );
}
