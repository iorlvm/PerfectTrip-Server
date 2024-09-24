package idv.tia201.g1.product.dao;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductInventoryDao extends JpaRepository<Product,Integer> {

    List<Product> getProductsByCompanyId(Integer companyId);

    Optional<Product> findById(Integer productId);

    List<Product> findAll();

    void deleteById(Integer productId);

    // 查詢可用房間 (庫存 > 0)
    List<Product> findByStockGreaterThan(int stock);

    // 查詢已預訂房間 (庫存 = 0)
    List<Product> findByStockEquals(int stock);


}
