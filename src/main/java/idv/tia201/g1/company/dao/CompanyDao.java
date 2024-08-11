package idv.tia201.g1.company.dao;

import idv.tia201.g1.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CompanyDao extends JpaRepository<Company, Integer> {

    //  TODO檢查這些是否有重複
    Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName);


    //	@Query("UPDATE Company u SET " +
//         "u.username = :#{#Company.username}, " +
//	       "u.password = :#{#Company.password}, " +
//	       "u.companyName = :#{#Company.companyName}, " +
//	       "u.vatNumber = :#{#Company.vatNumber}, " +
//	       "u.address = :#{#Company.address}, " +
//	       "u.telephone = :#{#Company.telephone}, " +
//	       "u.changeId = :#{#Company.changeId} " +
//	       "WHERE u.userId = :userId")
//	public void updateCompanyInfo(@Param("CompanyId") Integer CompanyId, @Param("Company") Company company);
}
