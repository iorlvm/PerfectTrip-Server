package idv.tia201.g1.member.service.Impl;

import idv.tia201.g1.member.dao.CompanyFacilityDao;
import idv.tia201.g1.member.dao.CompanyPhotosDao;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.member.entity.CompanyFacility;
import idv.tia201.g1.member.entity.CompanyPhotos;
import idv.tia201.g1.member.service.CompanyManagerService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@CommonsLog//，Lombok 會自動為你生成一個名為 log 的日誌對象，方便在類中進行日誌記錄，而不需要自己手動聲明。使用這個註解，你可以使用 log 對象記錄各種日誌信息，例如 log.info() 或 log.error()。
@Service
public class CompanyManagerServiceImpl implements CompanyManagerService {

    @Override
    public CompanyPhotos addCompanyphoto(Integer companyId, Integer photoId, String photoUrl) {
        return null;
    }

    @Override
    public CompanyPhotos deleteCompanyPhoto(Integer photoId) {
        return null;
    }

    @Override
    public List<CompanyPhotos> getCompanyPhotos(Company companyId) {
        return List.of();
    }

    @Override
    public List<CompanyFacility> updateCompanyFacility(List<CompanyFacility> facilityList) {
        return List.of();
    }

    @Override
    public List<CompanyFacility> getCompanyFacilities(Integer companyId) {
        return List.of();
    }
}
