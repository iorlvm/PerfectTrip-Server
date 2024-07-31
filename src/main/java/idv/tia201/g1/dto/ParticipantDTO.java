package idv.tia201.g1.dto;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class ParticipantDTO {
        private String userId;
        private String name;
        private String type;
        private String avatar;
        private Timestamp lastReadingAt;
        private Boolean online;
}
