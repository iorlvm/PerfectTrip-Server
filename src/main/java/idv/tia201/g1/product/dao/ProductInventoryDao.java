package idv.tia201.g1.product.dao;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.product.entity.Product;
import idv.tia201.g1.search.dto.ProductCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ProductInventoryDao extends JpaRepository<Product,Integer>, ProductInventoryDaoCustom {

    List<Product> getProductsByCompanyId(Integer companyId);

    Optional<Product> findById(Integer productId);

    List<Product> findAll();

    void deleteById(Integer productId);

    // 查詢可用房間 (庫存 > 0)
    List<Product> findByStockGreaterThan(int stock);

    // 查詢已預訂房間 (庫存 = 0)
    List<Product> findByStockEquals(int stock);



}
