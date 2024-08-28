package idv.tia201.g1.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
//在這裡收到請求 1.優惠卷是否使用,2.選擇了哪些房間 (同時順便傳是哪一間旅館的)

    //優惠倦代碼
    @NotBlank
    private Integer couponId;

    @NotBlank
    private Date beginDate;

    @NotBlank
    private Date endDate;
    //哪一間飯店
    @NotBlank
    private Integer companyId;
    //哪一間房型
    @NotBlank
    private List <Product> productList;

    @Data
    static class Product {
        private Integer productId;
        private Integer count;
    }


}
