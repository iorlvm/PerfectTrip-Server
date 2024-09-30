package idv.tia201.g1.search.dto;

import idv.tia201.g1.member.entity.CompanyPhotos;
import lombok.Data;

import java.util.List;

@Data
public class RecommendCompanyDTO {
    private Integer companyId;
    private String companyName;

    private Integer rateCount;
    private Float score;

    private String country;
    private String city;

    private List<CompanyPhotos> photos;
}
