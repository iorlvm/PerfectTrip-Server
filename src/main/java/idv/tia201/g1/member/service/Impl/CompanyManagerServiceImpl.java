package idv.tia201.g1.member.service.Impl;

import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.image.service.ImageService;
import idv.tia201.g1.member.dao.CompanyDao;
import idv.tia201.g1.member.dao.CompanyFacilityDao;
import idv.tia201.g1.member.dao.CompanyPhotosDao;
import idv.tia201.g1.member.dto.CompanyEditDetailRequest;
import idv.tia201.g1.member.dto.CompanyEditDetailResponse;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.member.entity.CompanyFacility;
import idv.tia201.g1.member.entity.CompanyPhotos;
import idv.tia201.g1.member.service.CompanyManagerService;
import idv.tia201.g1.product.dao.FacilityDao;
import idv.tia201.g1.product.entity.Facility;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static idv.tia201.g1.core.utils.Constants.ROLE_COMPANY;

@CommonsLog//，Lombok 會自動為你生成一個名為 log 的日誌對象，方便在類中進行日誌記錄，而不需要自己手動聲明。使用這個註解，你可以使用 log 對象記錄各種日誌信息，例如 log.info() 或 log.error()。
@Service
public class CompanyManagerServiceImpl implements CompanyManagerService {
    @Autowired
    ImageService imageService;
    @Autowired
    CompanyFacilityDao companyFacilityDao;
    @Autowired
    CompanyPhotosDao companyPhotosDao;
    @Autowired
    CompanyDao companyDao;
    @Autowired
    FacilityDao facilityDao;




    @Override
    public CompanyPhotos deleteCompanyPhoto(Integer photoId) {
        CompanyPhotos photo = companyPhotosDao.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("照片不存在，ID: " + photoId));
        companyPhotosDao.delete(photo);
        return photo;
    }


    @Override
    public List<CompanyPhotos> getCompanyPhotos(Company companyId) {
        return List.of();
    }
    @Transactional//+
    @Override
    public void handleEditCompanyInfo(CompanyEditDetailRequest companyEditDetailRequest) {

        if(companyEditDetailRequest == null){
            throw new IllegalArgumentException("參數異常：請求對象為 null");
        }

//        if(companyEditDetailRequest.getIntroduce() == null){
//            throw new IllegalArgumentException("參數異常：商家介紹未填");
//        }
        UserAuth loginUser = UserHolder.getUser();

        //驗證登入狀態
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：使用者未登入或身份不屬於商家");
        }
        // 打印登入的公司 ID 和變更 ID
        System.out.println("公司ID: " + loginUser.getId() + ", 變更ID: " + loginUser.getId());

        //將文字保存入資料庫
        Company company = companyDao.findByCompanyId(loginUser.getId()) ;
        company.setChangeId(loginUser.getId());
        company.setIntroduce(companyEditDetailRequest.getIntroduce());
        companyDao.save(company);


        //取得剛剛存入的商品ID
        Integer companyId = company.getCompanyId();

        List<Integer> facilityIds = companyEditDetailRequest.getFacilityIds();//快捷:companyEditDetailRequest.getFacilityIds().var
        //有傳值才做
        if(facilityIds != null && !facilityIds.isEmpty()){
            // 處理 CompanyFacility（商家設施訊息） //更新 全刪除 全新增
            companyFacilityDao.deleteByCompanyId(companyId);


            //快捷:facilityIds.for
            for (Integer facilityId : facilityIds) {
                CompanyFacility companyFacility = new CompanyFacility();
                companyFacility.setCompanyId(companyId);
                companyFacility.setFacilityId(facilityId);
                companyFacilityDao.save(companyFacility);
            }

        }


        // 整包存入圖片
        List<CompanyPhotos> photos = companyEditDetailRequest.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            for (CompanyPhotos photo : photos) {
                if (photo.getPhotoUrl() != null) {
                    photo.setCompanyId(companyId);
                    photo.setDescription("商家圖片:" + company.getCompanyName());
                    photo.setIsMain(false);
                }
            }
            // 使用 saveAll 保存列表
            companyPhotosDao.saveAll(photos);
        }

//        // 存入圖片
//        List<CompanyPhotos> photos = companyEditDetailRequest.getPhotos();
//        if (photos != null && !photos.isEmpty()) {  // 確保列表不為空
//            for (CompanyPhotos photo : photos) {
//                if (photo.getPhotoUrl() != null) {  // 確保每個照片的 URL 不為空
//                    photo.setCompanyId(companyId);  // 設置公司 ID
//                    photo.setDescription("商家圖片:" + company.getCompanyName()); // 設置描述
//                    photo.setIsMain(false); // 設置為非主圖
//
//                    // 保存每個 photo
//                    companyPhotosDao.save(photo);
//                }
//            }
//        }
    }

    @Override
    public List<CompanyFacility> getCompanyFacilities(Integer companyId) {
        return List.of();
    }

    @Override
    public CompanyEditDetailResponse getCompanyDetail(Integer companyId) {
        CompanyEditDetailResponse res = new CompanyEditDetailResponse();
        Company company = companyDao.findByCompanyId(companyId);
        List<CompanyPhotos> photos = companyPhotosDao.findByCompanyId(companyId);
        List<Facility> facilities = facilityDao.findByCompanyId(companyId);
        res.setFacilities(facilities);
        res.setPhotos(photos);
        res.setCompany(company);
        return res;
    }

    @Override
    @Transactional
    public void deleteCompanyDetail(Integer companyId) {
        companyFacilityDao.deleteByCompanyId(companyId);
        companyPhotosDao.deleteAll(companyPhotosDao.findByCompanyId(companyId));
        companyDao.deleteById(companyId);
    }


}
