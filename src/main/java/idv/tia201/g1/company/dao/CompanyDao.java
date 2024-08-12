package idv.tia201.g1.company.dao;

import idv.tia201.g1.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CompanyDao extends JpaRepository<Company, Integer> {

    Company findByCompanyId(Integer companyId);
    Company findByCompanyName(String companyname);
    Company findByUsername(String username);

    //  TODO檢查這些是否有重複

    Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName);

    //@Query: 指定一個原生 SQL 查詢，用來在 Repository 接口中定義自訂查詢。
    @Query(value = "SELECT count(*) FROM company_master", nativeQuery = true)
    public Integer countCompany();

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
