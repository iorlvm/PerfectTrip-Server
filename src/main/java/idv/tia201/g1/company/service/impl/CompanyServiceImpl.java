package idv.tia201.g1.company.service.impl;

import idv.tia201.g1.dto.CompanyLoginRequest;
import idv.tia201.g1.company.dao.CompanyDao;
import idv.tia201.g1.company.service.CompanyService;
import idv.tia201.g1.dto.CompanyRegisterRequest;
import idv.tia201.g1.entity.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyDao companyDao;

    @Override
    public Company login(CompanyLoginRequest companyLoginRequest) {
        return null;
    }

    @Override
    public Company register(CompanyRegisterRequest companyRegisterRequest) {
        //TODO檔重複COMPANYIDD,無重複才能繼續下去
        Company existingCompany = companyDao.findByUsernameOrVatNumberOrCompanyName(
                companyRegisterRequest.getUsername(),
                companyRegisterRequest.getVatNumber(),
                companyRegisterRequest.getCompanyName()
        );
        if (existingCompany != null) {
            throw new IllegalArgumentException("存在已註冊過資訊");
        }
        Company newcompany = new Company();
        newcompany.setUsername(companyRegisterRequest.getUsername());
        newcompany.setCompanyName(companyRegisterRequest.getCompanyName());
        newcompany.setPassword(companyRegisterRequest.getPassword());
        newcompany.setVatNumber(companyRegisterRequest.getVatNumber());
        newcompany.setAddress(companyRegisterRequest.getAddress());
        newcompany.setTelephone(companyRegisterRequest.getTelephone());

        companyDao.save(newcompany);
        return newcompany;
    }

    @Override
    public Company createCompany(CompanyLoginRequest companyLoginRequest) {
        return null;
    }


    @Override
    public Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName) {
        return companyDao.findByUsernameOrVatNumberOrCompanyName(username, vatNumber, companyName);
    }


}
