package idv.tia201.g1.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class ParticipantDTO {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long userId;
        private String name;
        private String type;
        private String avatar;
        private Timestamp lastReadingAt;
        private Boolean online;
}
