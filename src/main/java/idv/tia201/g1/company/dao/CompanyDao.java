package idv.tia201.g1.company.dao;

import idv.tia201.g1.entity.Company;


public interface CompanyDao {
    Company findByCompanyId(Integer companyId);

    Company findByCompanyName(String companyName);
}
