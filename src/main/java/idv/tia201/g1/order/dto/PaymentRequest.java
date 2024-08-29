package idv.tia201.g1.order.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String prime;           // 前端放入 (後端驗證不為空)
    private String partnerKey;      // 後端放入 (支付網站欄位)
    private String merchantId;      // 後端放入 (支付網站欄位)
    private String orderNumber;     // 後端放入 (前綴 + orderId組成)
    private String details;         // 前端放入 (支付網站需要防詐欺用, 後端不驗證)
    private int amount;             // 後端放入 (訂單金額)
    private Cardholder cardholder;  // 前端放入 (後端驗證必要欄位是否為空)

    @Data
    public static class Cardholder {
        private String phoneNumber; // 必填
        private String name;        // 必填
        private String email;       // 必填
        private String zipCode;     // 非必填, 不驗證
        private String address;     // 非必填, 不驗證
        private String memberId;    // 支付網站詐欺檢測 (後端填入)
    }
}
