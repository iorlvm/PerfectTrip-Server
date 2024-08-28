package idv.tia201.g1.order.service.impl;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.order.dto.CreateOrderRequest;
import idv.tia201.g1.order.entity.Order;
import idv.tia201.g1.order.service.OrderService;
import org.springframework.stereotype.Service;

import static idv.tia201.g1.core.utils.Constants.ROLE_USER;

@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public Order createOrder(CreateOrderRequest createOrderRequest) {
        UserAuth user = UserHolder.getUser();
        if(user == null || !ROLE_USER.equals(user.getRole())) {
            throw new IllegalStateException("未登入或是身分不符合");
        }
        //已確定身分為顧客!獲得顧客id
        Integer customId = user.getId();
        return null;
    }

    @Override
    public Order updateOrder(Order order) {
        return null;
    }
}
