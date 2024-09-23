package idv.tia201.g1.product.service;


import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.product.dto.ProductRequest;
import idv.tia201.g1.product.entity.Product;
import idv.tia201.g1.product.entity.ProductDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface ProductService {
    @Transactional
    Product updateProduct(ProductRequest request);

    @Transactional
    Product handleAddProduct(ProductRequest request);

    List<Product> getAllProductTypes();

    ProductDetails getProductById(Integer productId);

    Result getAllProducts();

    void deleteProduct(Long productId);


}
