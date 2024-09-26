package idv.tia201.g1.member.dto;

import idv.tia201.g1.member.entity.Company;

import idv.tia201.g1.member.entity.CompanyPhotos;
import idv.tia201.g1.product.entity.Facility;
import lombok.Data;

import java.util.List;


@Data
public class CompanyEditDetailResponse {


    private List<Facility> facilities;
    private List<CompanyPhotos> photos;
    private Company company;

}
