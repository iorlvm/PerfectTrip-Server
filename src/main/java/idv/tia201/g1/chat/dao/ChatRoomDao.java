package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomDao extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByChatId (Long chatId);
}
