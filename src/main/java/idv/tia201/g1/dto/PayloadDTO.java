package idv.tia201.g1.dto;

import lombok.Data;

@Data
public class PayloadDTO {
    private Long authorId;
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