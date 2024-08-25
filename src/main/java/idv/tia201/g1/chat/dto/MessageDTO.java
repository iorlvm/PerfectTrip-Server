package idv.tia201.g1.chat.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class MessageDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderId;
    private ImageDTO img;
    private String content;
    private String timestamp;

    @Data
    public static class ImageDTO {
        private String src;
        private String alt;
    }
}
