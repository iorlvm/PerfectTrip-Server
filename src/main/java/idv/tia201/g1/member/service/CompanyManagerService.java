package idv.tia201.g1.member.service;
import idv.tia201.g1.member.dto.CompanyEditDetailRequest;
import idv.tia201.g1.member.dto.CompanyEditDetailResponse;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.member.entity.CompanyFacility;
import idv.tia201.g1.member.entity.CompanyPhotos;



import java.util.List;

public interface CompanyManagerService {

    //刪除照片(單張)
     CompanyPhotos deleteCompanyPhoto(Integer photoId);

    //TODO將所有的商店與設施關聯刪掉 DELET FACILITY BY COMPANY_ID  新增FACILITTY LIST
    void handleEditCompanyInfo(CompanyEditDetailRequest companyEditDetailRequest);

    //取得照片,查詢(大家都可查)
    List<CompanyPhotos> getCompanyPhotos(Company companyId);

    //取得設施
    List<CompanyFacility> getCompanyFacilities(Integer companyId);

    CompanyEditDetailResponse getCompanyDetail (Integer companyId);

    //刪除公司資訊
    void deleteCompanyDetail(Integer companyId);

}
