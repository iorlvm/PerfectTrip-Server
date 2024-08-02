package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//@Repository
public interface ChatParticipantDao extends JpaRepository<ChatParticipant, Long>, CustomChatParticipantDao {
    ChatParticipant findByMappingUserIdAndChatId(Long mappingUserId, Long chatId);
}
