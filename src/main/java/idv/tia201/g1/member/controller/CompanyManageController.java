package idv.tia201.g1.member.controller;


import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.member.dto.CompanyEditDetailRequest;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.member.entity.CompanyPhotos;
import idv.tia201.g1.member.service.CompanyManagerService;
import jakarta.validation.Valid;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/store")
public class CompanyManageController {
    @Autowired
    private CompanyManagerService companyManagerService;


//    @PostMapping("/{companyId}")
//    public Result addCompanyEditDetail(@RequestBody @Valid CompanyEditDetailRequest companyEditDetailRequest){
//        CompanyEditDetailRequest editDetail = companyManagerService.handleEditCompanyInfo(companyEditDetailRequest);
//    }

    //新增設施


    //新增文字


    //修改照片/修改設施/修改文字


    //查 獲得照片列表

    //查  獲得設施列表

    //查 用companyid 獲得商家資料(照片、設施、文字)


}
