package idv.tia201.g1.product.dto;

import idv.tia201.g1.product.entity.ProductDetails;
import idv.tia201.g1.product.entity.ProductFacilities;
import idv.tia201.g1.product.entity.ProductPhotos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest {
    private Integer companyId;  // 確保類中的字段名與前端一致
    private Integer price;
    private Integer stock;
    private Integer maxOccupancy;
    private String productName;

    private ProductDetails productDetails;
    private List<ProductPhotos> productPhotos;
    private List<ProductFacilities> productFacilities;
}
