package idv.tia201.g1.chat.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId; // 會反饋到前端, 使用id生成器 (非自增)

    @Column(name = "chat_name", length = 255)
    private String chatName;

    @Column(name = "photo", length = 255)
    private String photo;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_at")
    private Timestamp lastMessageAt;

    @Column(name = "created_date", nullable = false, updatable = false, insertable = false)
    private Timestamp createdDate;

    @Column(name = "last_modified_date", nullable = false, updatable = false, insertable = false)
    private Timestamp lastModifiedDate;
}