package idv.tia201.g1.member.controller;


import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.member.dto.CompanyEditDetailRequest;
import idv.tia201.g1.member.dto.CompanyEditDetailResponse;
import idv.tia201.g1.member.entity.CompanyPhotos;
import idv.tia201.g1.member.service.CompanyManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/storeDetail")
public class CompanyManageController {
    @Autowired
    private CompanyManagerService companyManagerService;


    //更新編輯資訊
    @PutMapping("/{companyId}")
    public Result handleEditCompanyInfo(@RequestBody @Valid CompanyEditDetailRequest companyEditDetailRequest) {
        // 調用 service 來處理公司信息更新
        companyManagerService.handleEditCompanyInfo(companyEditDetailRequest);
        return Result.ok(companyEditDetailRequest);
    }

    //取得編輯資訊//查詢
    @GetMapping("/{companyId}")
    public Result getCompanyDetail( @PathVariable Integer companyId){
        CompanyEditDetailResponse companyDetail = companyManagerService.getCompanyDetail(companyId);
        return Result.ok(companyDetail);
    }

    //刪除公司資訊
    @DeleteMapping("/{companyId}")
    public Result deleteCompanyDetail(@PathVariable Integer companyId) {
        companyManagerService.deleteCompanyDetail(companyId);
        return Result.ok("公司資訊已刪除");
    }

    //刪除單張照片  先寫著  還不知道怎麼用??
    @DeleteMapping("/photo/{photoId}")
    public Result deleteCompanyPhoto(@PathVariable Integer photoId) {
        CompanyPhotos photo = companyManagerService.deleteCompanyPhoto(photoId);
        return Result.ok("照片已刪除: " +photo.getPhotoUrl());
    }


}
