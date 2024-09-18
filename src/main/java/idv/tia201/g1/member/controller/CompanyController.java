package idv.tia201.g1.member.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.core.service.TokenService;
import idv.tia201.g1.member.dto.CompanyLoginRequest;
import idv.tia201.g1.member.dto.CompanyQueryParams;
import idv.tia201.g1.member.dto.CompanyRegisterRequest;
import idv.tia201.g1.member.dto.CompanyUpdateRequest;
import idv.tia201.g1.member.service.CompanyService;
import idv.tia201.g1.member.entity.Company;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
@RequestMapping("/api/store")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private TokenService tokenService;

    // 註冊方法
    @PostMapping("/register")
    public Result register(@RequestBody @Valid CompanyRegisterRequest companyRegisterRequest) {
        try {
            Company company = companyService.register(companyRegisterRequest);
            return Result.ok(company);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    // 登錄方法
    @PostMapping("/login")
    public Result login(@RequestBody @Valid CompanyLoginRequest companyLoginRequest) {

        try {
            Company company = companyService.login(companyLoginRequest);
            String token = tokenService.createToken(company);
            company.setToken(token);
            company.setPassword(null);
            return Result.ok(company);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{companyId}")
    public Result getCompany(@PathVariable Integer companyId) {
        Company company = companyService.findByCompanyId(companyId);
        return Result.ok(company);
    }

    @GetMapping("/all")
    public Result getAllCompany(CompanyQueryParams companyQueryParams) {

        Page<Company> companyPage = companyService.findAll(companyQueryParams);

        return Result.ok(companyPage);

    }

    //修改資料
    @PutMapping("/{companyId}")
    public Result updateCompany(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer companyId, @RequestBody @Valid CompanyUpdateRequest companyUpdateRequest) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        Company company = companyService.UpdateCompany(companyId, companyUpdateRequest);
        company.setToken(token);
        company.setPassword(null);
        return Result.ok(company);

    }

    //刪除資料
    @DeleteMapping("/{companyId}")
    public Result deleteCompany(@PathVariable Integer companyId) {
        Company company = companyService.deleteCompany(companyId);
        return Result.ok(company);
    }

}