package idv.tia201.g1.member.dao;

import idv.tia201.g1.member.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CompanyDao extends JpaRepository<Company, Integer> {

    Company findByCompanyId(Integer companyId);
    Company findByUsername(String username);

    //  TODO檢查這些是否有重複
    Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName);

    //@Query: 指定一個原生 SQL 查詢，用來在 Repository 接口中定義自訂查詢。
    @Query(value = "SELECT count(*) FROM company_master", nativeQuery = true)
    Integer countCompany();

    @Query("SELECT DISTINCT c FROM Company c " +
            "JOIN Product p ON p.companyId = c.companyId " +
            "JOIN OrderDetail od ON od.productId = p.productId " +
            "WHERE  od.orderId = :orderId")
    Company findByOrderId(@Param("orderId") Integer orderId);

    public long count();
}
