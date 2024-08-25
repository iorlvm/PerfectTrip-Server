package idv.tia201.g1.chat.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_user_mappings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_type", "ref_id"})
})
public class ChatUserMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_user_id", nullable = false)
    private Long mappingUserId;

    @Column(name = "ref_id", nullable = false)
    private Integer refId;

    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;
}
