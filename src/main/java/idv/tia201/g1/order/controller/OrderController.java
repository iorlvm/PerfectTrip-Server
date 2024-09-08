package idv.tia201.g1.order.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.order.dto.CreateOrderRequest;
import idv.tia201.g1.order.entity.Order;
import idv.tia201.g1.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static idv.tia201.g1.core.utils.Constants.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        try {
            Order order = orderService.createOrder(createOrderRequest);
            return Result.ok(order);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping
    public Result getOrders() {
        //取得登入的使用者
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null) {
            return Result.fail("使用者未登入!");
        }
        try {
            List<Order> res = null;
            switch (loginUser.getRole()) {
                case ROLE_USER:
                    res = orderService.getOrdersByUserId(loginUser.getId());
                    break;
                case ROLE_COMPANY:
                    res = orderService.getOrdersByCompanyId(loginUser.getId());
                    break;
                case ROLE_ADMIN:
                    res = orderService.getOrders();
                    break;
                default:
            }

            return Result.ok(res, res == null ? 0L : res.size());

        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
