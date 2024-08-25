package idv.tia201.g1.member.service;

import idv.tia201.g1.member.dto.CompanyLoginRequest;
import idv.tia201.g1.member.dto.CompanyQueryParams;
import idv.tia201.g1.member.dto.CompanyRegisterRequest;
import idv.tia201.g1.member.dto.CompanyUpdateRequest;
import idv.tia201.g1.member.entity.Company;
import org.springframework.data.domain.Page;

public interface CompanyService {

   public Company login(CompanyLoginRequest companyLoginRequest);

   public Company register(CompanyRegisterRequest companyRegisterRequest);

   public Company findByCompanyId(Integer companyId);

   public Company UpdateCompany(Integer companyId, CompanyUpdateRequest companyUpdateRequest);

   public Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName);

   public Integer countCompany();

   public Page<Company> findAll(CompanyQueryParams companyQueryParams);

   public Company deleteCompany(Integer companyId);
}
