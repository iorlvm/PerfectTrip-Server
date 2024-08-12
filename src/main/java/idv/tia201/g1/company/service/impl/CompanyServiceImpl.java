package idv.tia201.g1.company.service.impl;

import idv.tia201.g1.dto.CompanyLoginRequest;
import idv.tia201.g1.company.dao.CompanyDao;
import idv.tia201.g1.company.service.CompanyService;
import idv.tia201.g1.dto.CompanyRegisterRequest;
import idv.tia201.g1.dto.CompanyUpdateRequest;
import idv.tia201.g1.entity.Company;
import idv.tia201.g1.entity.User;
import idv.tia201.g1.user.dao.UserDao;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.LocalDateTime;

@CommonsLog
@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private UserDao userDao;

    @Override
    public Company login(CompanyLoginRequest companyLoginRequest) {
        Company company = companyDao.findByUsername(companyLoginRequest.getUsername());
       //檢查帳號
        if (company == null) {
            throw new IllegalArgumentException("帳號或密碼有誤");
        }
        //使用MD5生成加密
        String hashedPassword = DigestUtils.md5DigestAsHex(companyLoginRequest.getPassword().getBytes());

        //檢查密碼
        if (!hashedPassword.equals(company.getPassword())) {
            throw new IllegalArgumentException("帳號或密碼有誤");
        }
        return company;
    }

    @Override
    public Company register(CompanyRegisterRequest companyRegisterRequest) {
        //// 檢查註冊
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

            //使用MD5生成加密
            String hashedPassword = DigestUtils.md5DigestAsHex(companyRegisterRequest.getPassword().getBytes());
            newcompany.setPassword(hashedPassword);
            // 創建帳號
            companyDao.save(newcompany);
            return newcompany;

    }

    @Override
    public User findCompanyId(Integer companyId) {
        return userDao.findByUserId(companyId);
    }

    @Override
    public Company UpdateCompany(Integer companyId, CompanyUpdateRequest companyUpdateRequest) {
        Company company = companyDao.findByCompanyId(companyId);

        if (company == null) {
            throw new IllegalArgumentException("不存在此商家");
        }

        if ( companyUpdateRequest.getUsername()!=null && !companyUpdateRequest.getUsername().equals(company.getUsername())) {
            company.setUsername(companyUpdateRequest.getUsername());
        }

        if (companyUpdateRequest.getPassword() !=null && !companyUpdateRequest.getPassword().equals(company.getPassword())) {
             String hashedPassword = DigestUtils.md5DigestAsHex(company.getPassword().getBytes());
            company.setPassword(hashedPassword);
        }

        if (companyUpdateRequest.getCompanyName()!=null && !companyUpdateRequest.getCompanyName().equals(company.getCompanyName())) {
            company.setCompanyName(companyUpdateRequest.getCompanyName());
        }

        if (companyUpdateRequest.getVatNumber()!=null && !companyUpdateRequest.getVatNumber().equals(company.getVatNumber())) {
            company.setVatNumber(companyUpdateRequest.getVatNumber());
        }

        if (companyUpdateRequest.getAddress()!=null && !companyUpdateRequest.getAddress().equals(company.getAddress())) {
            company.setAddress(companyUpdateRequest.getAddress());
        }

        if (companyUpdateRequest.getTelephone()!=null && !companyUpdateRequest.getTelephone().equals(company.getTelephone())) {
            company.setTelephone(companyUpdateRequest.getTelephone());
        }

        if (companyUpdateRequest.getChangeId()!=null && !companyUpdateRequest.getChangeId().equals(company.getChangeId())) {
            company.setChangeId(companyUpdateRequest.getChangeId());
        }

        company.setLastModifiedDate(LocalDateTime.now());

        Company companysaved = companyDao.save(company);
        return companysaved;
    }

    @Override
    public Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName) {
        return companyDao.findByUsernameOrVatNumberOrCompanyName(username, vatNumber, companyName);
    }

    @Override
    public Company countCompany() {
        return null;
    }


}
