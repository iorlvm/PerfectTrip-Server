package idv.tia201.g1.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCalculation {
    private Integer productId;          // 商品編號
    private Integer maxOccupancy;       // 幾人房
    private Integer remainingRooms;     // 剩餘房間數量
    private Integer price;              // 價格 (原價)
}
