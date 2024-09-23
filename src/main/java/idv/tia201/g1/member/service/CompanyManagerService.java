package idv.tia201.g1.member.service;

import idv.tia201.g1.member.dto.CompanyEditDetailRequest;
import idv.tia201.g1.member.dto.CompanyUpdateRequest;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.member.entity.CompanyFacility;
import idv.tia201.g1.member.entity.CompanyPhotos;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.util.List;

public interface CompanyManagerService {
   //新增照片
    CompanyPhotos addCompanyphoto(Integer companyId, Integer photoId, String photoUrl);
    //刪除照片(單張)
     CompanyPhotos deleteCompanyPhoto(Integer photoId);
    //取得照片
    List<CompanyPhotos> getCompanyPhotos(Company companyId);



    //設施  TODO將所有的商店與設施關聯刪掉 DELET FACILITY BY COMPANY_ID  新增FACILITTY LIST
    List<CompanyFacility> updateCompanyFacility( List<CompanyFacility> facilityList);
    //取得設施
    List<CompanyFacility> getCompanyFacilities(Integer companyId);






}
