package idv.tia201.g1.order.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.order.dto.PaymentRequest;
import idv.tia201.g1.order.dto.PaymentResponse;
import idv.tia201.g1.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay/{orderId}")
    public Result pay(@PathVariable Integer orderId, @RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResponse response = paymentService.processPayment(orderId, paymentRequest);
            return Result.ok(response);
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }
    }
}
