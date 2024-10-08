package idv.tia201.g1.order.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String prime;           // 前端放入 (後端驗證不為空)
    private String partner_key;      // 後端放入 (支付網站欄位)
    private String merchant_id;      // 後端放入 (支付網站欄位)
    private String order_number;     // 後端放入 (前綴 + orderId組成)
    private String details;         // 前端放入 (支付網站需要防詐欺用, 後端不驗證)
    private int amount;             // 後端放入 (訂單金額)
    private Cardholder cardholder;  // 前端放入 (後端驗證必要欄位是否為空)

    @Data
    public static class Cardholder {
        private String phone_number; // 必填
        private String name;        // 必填
        private String email;       // 必填
        private String memberId;    // 支付網站詐欺檢測 (後端填入)
    }
}
