package idv.tia201.g1.company.controller;

import idv.tia201.g1.authentication.service.TokenService;
import idv.tia201.g1.dto.Result;
import idv.tia201.g1.dto.CompanyLoginRequest;
import idv.tia201.g1.dto.CompanyRegisterRequest;
import idv.tia201.g1.company.service.CompanyService;
import idv.tia201.g1.entity.Company;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Validated
@RestController
@RequestMapping("/store")
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
            return Result.ok(company);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }

    }
}