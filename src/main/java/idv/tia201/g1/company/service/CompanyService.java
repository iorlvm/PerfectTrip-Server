package idv.tia201.g1.company.service;

import idv.tia201.g1.dto.CompanyLoginRequest;
import idv.tia201.g1.dto.CompanyRegisterRequest;
import idv.tia201.g1.entity.Company;

public interface CompanyService {

   public Company login(CompanyLoginRequest companyLoginRequest);

   public Integer register(CompanyRegisterRequest companyRegisterRequest);

   public Integer createCompany (CompanyLoginRequest companyLoginRequest);

   public Company findByCompanyId(Integer companyId);

   public Company findByCompanyName(String companyName);

}
