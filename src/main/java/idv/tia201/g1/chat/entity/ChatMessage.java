package idv.tia201.g1.chat.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
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

    @Column(name = "created_date", nullable = false, updatable = false)
    private Timestamp createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private Timestamp lastModifiedDate;


    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = Timestamp.from(Instant.now());
        }
        lastModifiedDate = createdDate;
    }

    @PreUpdate
    protected void onUpdate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = Timestamp.from(Instant.now());
        }
    }
}