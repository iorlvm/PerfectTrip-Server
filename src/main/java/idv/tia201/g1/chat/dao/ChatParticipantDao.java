package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatParticipant;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

//@Repository
public interface ChatParticipantDao extends JpaRepository<ChatParticipant, Long>, CustomChatParticipantDao {
    ChatParticipant findByMappingUserIdAndChatId(Long mappingUserId, Long chatId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatParticipant cp " +
            "SET cp.lastReadingAt = :timestamp, cp.unreadMessages = 0 " +
            "WHERE cp.chatId = :chatId AND cp.mappingUserId = :userMappingId")
    void updateLastReadingAtByChatIdAndMappingUserId(
            @Param("chatId") Long chatId,
            @Param("userMappingId") Long userMappingId,
            @Param("timestamp") Timestamp timestamp
    );
}
