package idv.tia201.g1.order.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.order.dto.CreateOrderRequest;
import idv.tia201.g1.order.dto.OrderDTO;
import idv.tia201.g1.order.dto.UpdateOrderRequest;
import idv.tia201.g1.order.entity.Order;
import idv.tia201.g1.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static idv.tia201.g1.core.utils.Constants.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        try {
            Order order = orderService.createOrder(createOrderRequest);
            return Result.ok(order);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping
    public Result getOrders(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdDate") String orderBy
    ) {
        //取得登入的使用者
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null) {
            return Result.fail("使用者未登入!");
        }

        Sort sortBy = Sort.by(Sort.Direction.DESC, "createdDate");
        //TODO: 未來也許可以根據orderBy增加新的排序規則

        Pageable pageable = PageRequest.of(page, size, sortBy);
        try {
            Page<Order> res = null;
            switch (loginUser.getRole()) {
                case ROLE_USER:
                    res = orderService.getOrdersByUserId(loginUser.getId(), pageable);
                    break;
                case ROLE_COMPANY:
                    res = orderService.getOrdersByCompanyId(loginUser.getId(), pageable);
                    break;
                case ROLE_ADMIN:
                    res = orderService.getValidOrders(pageable);
                    break;
                default:
            }
            if (res == null || res.getContent().isEmpty()) {
                return Result.ok(Collections.emptyList(), 0L);
            }

            List<OrderDTO> orderDTOs = orderService.getOrderDTOs(res.getContent());

            return Result.ok(orderDTOs, res.getTotalElements());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{orderId}")
    public Result updateOrders(@PathVariable Integer orderId, @RequestBody @Valid UpdateOrderRequest updateOrderRequest) {

        try {
            Order order = orderService.updateOrder(orderId, updateOrderRequest);
            return Result.ok(order);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public Result getOrderById(@PathVariable Integer orderId) {
        try {
            OrderDTO order = orderService.getOrder(orderId);
            return Result.ok(order);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}


