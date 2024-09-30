package idv.tia201.g1.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyReviewDTO {
    private String author;

    private Integer starRank;

    private String comment;

}
