package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatRoom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Repository
public interface ChatRoomDao extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByChatId (Long chatId);
}
