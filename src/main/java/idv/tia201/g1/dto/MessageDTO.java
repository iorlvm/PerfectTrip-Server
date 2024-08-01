package idv.tia201.g1.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private Long messageId;
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
