package idv.tia201.g1.chat.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "chat_participants")
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "mapping_user_id", nullable = false)
    private Long mappingUserId;

    @Column(name = "pinned")
    private Boolean pinned;

    @Column(name = "notify")
    private String notify;

    @Column(name = "unread_messages")
    private Integer unreadMessages;

    @Column(name = "last_reading_at")
    private Timestamp lastReadingAt;

    @Column(name = "created_date", nullable = false, updatable = false, insertable = false)
    private Timestamp createdDate;

    @Column(name = "last_modified_date", nullable = false, updatable = false, insertable = false)
    private Timestamp lastModifiedDate;

    @Transient
    private String name;

    @Transient
    private String avatar;

    @Transient
    private String type;
}