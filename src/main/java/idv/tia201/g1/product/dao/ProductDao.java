package idv.tia201.g1.product.dao;

import idv.tia201.g1.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
    List<Product> getProductsByCompanyId(Integer companyId);

    @Query("SELECT p.productName FROM Product p WHERE p.productId = :productId")
    String getProductNameByProductId(@Param("productId") Integer productId);
}
