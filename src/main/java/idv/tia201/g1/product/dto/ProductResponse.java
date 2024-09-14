package idv.tia201.g1.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer productId;
    private String productName;
    private Integer price;  // 使用 price 而不是 roomPrice，确保统一
    private Integer maxOccupancy;
    private Integer stock;
    private Integer companyId;  // 使用 Integer 类型，确保一致

    // 构造函数，使用 Integer 类型的 companyId
    public ProductResponse(Integer companyId, Integer productId, String productName, Integer price, Integer maxOccupancy, int stock) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;  // 保持 price 和字段一致
        this.maxOccupancy = maxOccupancy;
        this.stock = stock;
        this.companyId = companyId;  // 使用 Integer 类型
    }

}
