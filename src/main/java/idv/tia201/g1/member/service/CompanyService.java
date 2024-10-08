package idv.tia201.g1.member.service;

import idv.tia201.g1.member.dto.*;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.member.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CompanyService {

   public Company login(CompanyLoginRequest companyLoginRequest);

   public Company register(CompanyRegisterRequest companyRegisterRequest);

   public Company findByCompanyId(Integer companyId);

   public Company UpdateCompany(Integer companyId, CompanyUpdateRequest companyUpdateRequest);

   public Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName);

   public Integer countCompany();

   public Page<Company> findAll(CompanyQueryParams companyQueryParams);

   public Company deleteCompany(Integer companyId);

   public List<Company> findCompaniesList(CompanyQueryParams companyQueryParams);

   public Long count();
}
