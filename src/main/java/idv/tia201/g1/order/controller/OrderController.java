package idv.tia201.g1.order.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.order.dto.CreateOrderRequest;
import idv.tia201.g1.order.entity.Order;
import idv.tia201.g1.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static idv.tia201.g1.core.utils.Constants.ROLE_USER;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        try {
            Order order = orderService.createOrder(createOrderRequest);
            return  Result.ok(order);
        }catch (Exception e){
           return  Result.fail(e.getMessage());
        }
    }
}
