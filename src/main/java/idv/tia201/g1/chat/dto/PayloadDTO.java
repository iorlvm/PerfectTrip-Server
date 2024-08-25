package idv.tia201.g1.chat.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class PayloadDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long authorId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatId;
    private String action;
    private String content;
    private String timestamp;

    @Data
    public static class UserInfo {
        private String name;
        private String avatar;
    }

    @Data
    public static class RoomInfo {
        private String chatName;
        private String photo;
    }
}