package idv.tia201.g1.product.dto;

import idv.tia201.g1.image.dto.ImageUploadRequest;
import idv.tia201.g1.product.entity.ProductDetails;
import idv.tia201.g1.product.entity.ProductFacilities;
import lombok.Data;

import java.util.List;

@Data
public class AddProductRequest {

    private Integer price;

    private Integer stock;

    private Integer maxOccupancy;

    private String productName;

    private ImageUploadRequest imageUploadRequest;

    private ProductDetails productDetails;

    private List<ProductFacilities> productFacilities;
}
