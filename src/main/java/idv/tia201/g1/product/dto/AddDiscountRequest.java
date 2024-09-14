package idv.tia201.g1.product.dto;

import lombok.Data;

import java.time.LocalDate;

import java.time.LocalDate;

@Data
public class AddDiscountRequest {
    private String discountTitle;
    private Integer discountRate;
    private LocalDate startDate;  // 確保這裡是 LocalDate
    private LocalDate endDate;     // 結束日期

}
