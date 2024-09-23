package idv.tia201.g1.member.controller;


import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.member.entity.CompanyPhotos;
import idv.tia201.g1.member.service.CompanyManagerService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/store")
public class CompanyManageController {

    //新增照片
  @PostMapping
      public Result addCompanyphoto(@Validated @RequestBody CompanyPhotos companyPhoto) {
      return  null;
  }
        @Autowired
        private CompanyManagerService companyManagerService;
    //新增設施


    //新增文字


    //修改照片/修改設施/修改文字


    //查 獲得照片列表

    //查  獲得設施列表

    //查 用companyid 獲得商家資料(照片、設施、文字)




}
