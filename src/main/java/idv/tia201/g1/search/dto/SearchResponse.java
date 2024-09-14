package idv.tia201.g1.search.dto;

import lombok.Data;


@Data
public class SearchResponse {
    private Integer companyId;
    private String companyName;             // 旅館名稱
    private String country;                 // 國家
    private String city;                    // 位置
    private Float score;                   // 評分數值
    private Integer commentCount;           // 評價數量
    private Boolean isPromotion;            // 促銷中
    private Boolean includesBreakfast;      // 是否包含早餐
    private Boolean allowDateChanges;       // 可否修改日期
    private Boolean allowFreeCancellation;  // 是否免費取消
    private Boolean isRefundable;           // 可否退款
    private Integer price;                  // 價格 (整數)
    private String photo;
}
