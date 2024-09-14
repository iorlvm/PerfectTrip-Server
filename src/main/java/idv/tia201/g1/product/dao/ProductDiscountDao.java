package idv.tia201.g1.product.dao;

import idv.tia201.g1.product.entity.Product;
import idv.tia201.g1.product.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDiscountDao extends JpaRepository<ProductDiscount, Integer> {
    List<ProductDiscount> findByCompanyId(Integer companyId);
}
