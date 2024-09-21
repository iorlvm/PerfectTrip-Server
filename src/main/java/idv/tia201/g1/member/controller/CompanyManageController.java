package idv.tia201.g1.member.controller;


import idv.tia201.g1.core.service.TokenService;
import idv.tia201.g1.member.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/store")
public class CompanyManageController {


    @Autowired
    private TokenService tokenService;


}
