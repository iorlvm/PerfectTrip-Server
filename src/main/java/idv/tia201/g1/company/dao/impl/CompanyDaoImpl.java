package idv.tia201.g1.company.dao.impl;

import idv.tia201.g1.company.dao.CompanyDao;
import idv.tia201.g1.entity.Company;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyDaoImpl implements CompanyDao {
    @Override
    public Company findByCompanyId(Integer storeId) {
        return null;
    }

    @Override
    public Company findByCompanyName(String storeName) {
        return null;
    }
}
