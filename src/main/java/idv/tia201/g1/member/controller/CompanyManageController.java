package idv.tia201.g1.member.controller;


import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.member.dto.CompanyEditDetailRequest;
import idv.tia201.g1.member.dto.CompanyEditDetailResponse;
import idv.tia201.g1.member.service.CompanyManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/storeEdit")
public class CompanyManageController {
    @Autowired
    private CompanyManagerService companyManagerService;


    //更新編輯資訊
    @PutMapping("/{companyId}")
    public Result handleEditCompanyInfo(@RequestBody @Valid CompanyEditDetailRequest companyEditDetailRequest) {
        // 調用 service 來處理公司信息更新
        companyManagerService.handleEditCompanyInfo(companyEditDetailRequest);
        return Result.ok("公司信息已成功更新");
    }

    //取得編輯資訊
    @GetMapping("/{companyId}")
    public Result getCompanyDetail( @PathVariable Integer companyId,@RequestParam @Valid CompanyEditDetailResponse companyEditDetailResponse){
        CompanyEditDetailResponse companyDetail = companyManagerService.getCompanyDetail(companyId);
        return Result.ok(companyDetail);
    }





}
