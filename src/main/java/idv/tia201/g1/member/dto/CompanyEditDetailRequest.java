package idv.tia201.g1.member.dto;

import idv.tia201.g1.image.dto.ImageUploadRequest;
import idv.tia201.g1.member.entity.CompanyFacility;
import idv.tia201.g1.member.entity.CompanyPhotos;
import idv.tia201.g1.product.entity.ProductDetails;
import idv.tia201.g1.product.entity.ProductFacilities;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Data
@Validated
public class CompanyEditDetailRequest {
    //照片
    private Integer companyId;
    //一張張加
    private CompanyPhotos photos;

//    //設施
//    private Integer facilityId;

    // 使用空列表避免返回 null
    private List<Integer> facilityIds;


    //公司介紹
    private String introduce;



}
