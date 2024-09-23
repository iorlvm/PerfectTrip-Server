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
    private Integer guestCount;
    //優惠倦代碼
    private Integer couponId;
    //優惠卷效期開始日
    private Date beginDate;
    //優惠卷效期結束日
    private Date endDate;
    //哪一間飯店
    private Integer companyId;
    //哪一間房型
    private List <Product> productList;

    @Data
    public static class Product {
        private Integer productId;
        private Integer count;
    }
}
