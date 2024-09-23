package idv.tia201.g1.member.dto;

import idv.tia201.g1.member.entity.CompanyFacility;
import idv.tia201.g1.member.entity.CompanyPhotos;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class CompanyEditDetailRequest {
    //照片
    private String companyId;

    private List<CompanyPhotos> photos;


    //設施
    private String facilityId;

    private String facilityName;

    private String companyFacilityId;

    private List<CompanyFacility> facilities;

    //公司介紹
    private String introduce;

}
