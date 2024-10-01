package idv.tia201.g1.member.dto;


import idv.tia201.g1.member.entity.CompanyPhotos;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Data
@Validated
public class CompanyEditDetailRequest {
    // 照片
    private Integer companyId;
//    // 一張張加
//    private CompanyPhotos photos;
    //整包加
    private List<CompanyPhotos> photos;

    // 設施
    private List<Integer> facilityIds;

    // 公司介紹
    private String introduce;



}
