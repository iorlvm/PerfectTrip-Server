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
    public Integer register(CompanyRegisterRequest CompanyRegisterRequest) {
        return 0;
    }

    @Override
    public Integer createCompany(CompanyLoginRequest CompanyLoginRequest) {
        return 0;
    }

    @Override
    public Company findByCompanyId(Integer CompanyId) {
        return companyDao.findByCompanyId(CompanyId);
    }
    @Override
    public Company findByCompanyName(String CompanyName){
        return companyDao.findByCompanyName(CompanyName);
    }


}
