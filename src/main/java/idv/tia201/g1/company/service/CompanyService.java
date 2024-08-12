package idv.tia201.g1.company.service;

import idv.tia201.g1.dto.CompanyLoginRequest;
import idv.tia201.g1.dto.CompanyRegisterRequest;
import idv.tia201.g1.dto.CompanyUpdateRequest;
import idv.tia201.g1.entity.Company;
import idv.tia201.g1.entity.User;

public interface CompanyService {

   public Company login(CompanyLoginRequest companyLoginRequest);

   public Company register(CompanyRegisterRequest companyRegisterRequest);

   public User findCompanyId(Integer companyId);

   public Company UpdateCompany(Integer companyId, CompanyUpdateRequest companyUpdateRequest);

   public Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName);

   public Company countCompany();
}
