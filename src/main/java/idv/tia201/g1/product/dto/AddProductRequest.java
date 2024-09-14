package idv.tia201.g1.product.dto;

import idv.tia201.g1.image.dto.ImageUploadRequest;
import idv.tia201.g1.product.entity.ProductDetails;
import idv.tia201.g1.product.entity.ProductFacilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    // 使用空列表避免返回 null
    private List<ProductFacilities> productFacilities = new ArrayList<>();

    // 返回一個默認的空 ImageUploadRequest，如果沒有圖片
    private ImageUploadRequest imageUploadRequest = new ImageUploadRequest();

    // 返回一個默認的空 ProductDetails，如果沒有細節
    private ProductDetails productDetails = new ProductDetails();
}
