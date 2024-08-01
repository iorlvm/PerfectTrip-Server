package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserMappingDao extends JpaRepository<ChatUserMapping, Long> {
    ChatUserMapping findByUserTypeAndRefId(String userType, Integer refId);
}
