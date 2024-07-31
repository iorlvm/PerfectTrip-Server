package idv.tia201.g1.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "mapping_user_id")
    private Long mappingUserId;

    @Column(name = "content")
    private String content;

    @Column(name = "img", length = 255)
    private String img;

    @Column(name = "created_date", nullable = false, updatable = false, insertable = false)
    private Timestamp createdDate;

    @Column(name = "last_modified_date", nullable = false, updatable = false, insertable = false)
    private Timestamp lastModifiedDate;
}