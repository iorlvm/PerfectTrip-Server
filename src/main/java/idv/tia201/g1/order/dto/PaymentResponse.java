package idv.tia201.g1.order.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private int status;                 // 交易代碼，成功的話為0
    private String msg;                 // 錯誤訊息
    private String recTradeId;          // 由 TapPay 伺服器產生的交易字串
    private String bankTransactionId;   // 銀行端的訂單編號
    private String bankOrderNumber;     // 銀行或錢包端於授權時回傳的訂單編號
    private String paymentUrl;          // 付款頁面網址
    private String orderNumber;         // 自定義的訂單編號
}
