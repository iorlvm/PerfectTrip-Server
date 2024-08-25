package idv.tia201.g1.chat.dao;

import idv.tia201.g1.chat.entity.ChatParticipant;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ChatParticipantDao extends JpaRepository<ChatParticipant, Long>, CustomChatParticipantDao {
    ChatParticipant findByMappingUserIdAndChatId(Long mappingUserId, Long chatId);

    @Modifying
    @Query("UPDATE ChatParticipant cp " +
            "SET cp.notify = :notify " +
            "WHERE cp.chatId = :chatId AND cp.mappingUserId = :userMappingId")
    void updateNotifyByChatIdAndMappingUserId(
            @Param("chatId") Long chatId,
            @Param("userMappingId") Long userMappingId,
            @Param("notify") String notify
    );

    @Modifying
    @Query("UPDATE ChatParticipant cp " +
            "SET cp.pinned = :pinned " +
            "WHERE cp.chatId = :chatId AND cp.mappingUserId = :userMappingId")
    void updatePinnedByChatIdAndMappingUserId(
            @Param("chatId") Long chatId,
            @Param("userMappingId") Long userMappingId,
            @Param("pinned") boolean pinned
    );

    @Query("SELECT cp.chatId FROM ChatParticipant cp " +
            "JOIN ChatUserMapping cm ON cm.mappingUserId = cp.mappingUserId " +
            "WHERE cm.refId = :refId AND cm.userType = :type")
    Set<Long> findChatIdByTypeAndRefId(
            @Param("type") String type,
            @Param("refId") Integer refId);


    @Query("SELECT SUM(cp.unreadMessages) FROM ChatParticipant cp WHERE cp.mappingUserId = :mappingUserId")
    Long getTotalUnreadMessagesNumberByMappingUserId(@Param("mappingUserId") Long mappingUserId);
}
