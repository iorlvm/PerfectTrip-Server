package idv.tia201.g1.product.service;


import idv.tia201.g1.product.dto.AddProductRequest;
import idv.tia201.g1.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {

    Product updateProduct(Long productId,
                          String productName,
                          int roomPrice,
                          byte[] photoByte,
                          Integer maxOccupancy,
                          int stock);

    Product handleAddProduct(AddProductRequest request);

    List<Product> getAllProductTypes();

    // Optional 可以解決可能出現的空指針異常（NullPointerException）問題，Optional<T>
    // 作為泛型表示一個可能包含或不包含非 null 值的對象。還可以結合返回特定值 orElse()

    Optional<Product> getProductById(Long productId);

    List<Product> getAllProducts();

    byte[] getRoomPhotoByRoomId(Integer id);

    void deleteProduct(Long productId);


}
