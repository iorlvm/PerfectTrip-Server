package idv.tia201.g1.product.service;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.product.dto.ProductRequest;
import idv.tia201.g1.product.entity.Product;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * ClassName:ProductInventoryService
 * Package: idv.tia201.g1.product.service.impl
 * Description:
 *
 * @Author: Jacob
 * @Create: 2024/9/21 - 上午11:19
 * @Version: v1.0
 */
@Service
public interface ProductInventoryService {
    Result getAllInventories();

    Product updateProduct(Product product);

    void deleteProduct(Integer id);

    List<Product> getInventoriesByStatus(String status);

    Product addInventory(ProductRequest productRequest);

    List<Product> getInventoryByDateRange(LocalDate start, LocalDate end);
}
