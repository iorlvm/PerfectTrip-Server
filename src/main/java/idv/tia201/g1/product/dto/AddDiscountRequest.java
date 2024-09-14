package idv.tia201.g1.product.dto;

import lombok.Data;

import java.time.LocalDate;

import java.time.LocalDate;

@Data
public class AddDiscountRequest {
    private String discountTitle;   // 折扣標題
    private String roomType;        // 房型 (可以是ID或名稱)
    private Float discountRate;     // 折扣率 (%)
    private LocalDate startDate;    // 開始日期
    private LocalDate endDate;      // 結束日期

}
