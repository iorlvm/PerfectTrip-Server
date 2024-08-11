package idv.tia201.g1.company.service;

import idv.tia201.g1.dto.CompanyLoginRequest;
import idv.tia201.g1.dto.CompanyRegisterRequest;
import idv.tia201.g1.dto.Result;
import idv.tia201.g1.entity.Company;

public interface CompanyService {

   public Company login(CompanyLoginRequest companyLoginRequest);

   public Company register(CompanyRegisterRequest companyRegisterRequest);

   public Company createCompany (CompanyLoginRequest companyLoginRequest);

   public Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName);
}
