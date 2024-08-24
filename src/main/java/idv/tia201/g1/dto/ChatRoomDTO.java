package idv.tia201.g1.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import idv.tia201.g1.entity.ChatParticipant;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatRoomDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatId;
    private String chatName;
    private Integer unreadMessages;
    private String lastMessage;
    private Timestamp lastMessageAt;
    private String photo;
    private List<ParticipantDTO> participants;
    private String notifySettings;  // 'on', 'off'
    private Boolean pinned;
    private Timestamp lastModifiedAt;
}

