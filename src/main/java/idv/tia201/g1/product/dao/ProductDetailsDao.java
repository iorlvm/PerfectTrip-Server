package idv.tia201.g1.product.dao;

import idv.tia201.g1.product.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductDetailsDao extends JpaRepository<ProductDetails, Integer> {

    ProductDetails findByProductId (Integer productId);
    List<ProductDetails> findByProductIdIn (List<Integer> productIds);
}
