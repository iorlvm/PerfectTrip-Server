package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatParticipantDao extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByChatId(Long chatId);

    Page<ChatParticipant> findByMappingUserId(Long mappingUserId, Pageable pageable);
}
