package idv.tia201.g1.order.service;

import idv.tia201.g1.order.dto.PaymentRequest;
import idv.tia201.g1.order.dto.PaymentResponse;

public interface PaymentService {
    /**
     * 支付處理流程
     *
     * @param orderId 訂單編號
     * @param paymentRequest 支付請求物件
     * @return 支付回應物件
     */
    PaymentResponse processPayment(Integer orderId, PaymentRequest paymentRequest);
}
