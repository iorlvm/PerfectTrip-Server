package idv.tia201.g1.order.service;

import idv.tia201.g1.order.dto.CreateOrderRequest;
import idv.tia201.g1.order.entity.Order;
import org.hibernate.sql.Update;

public interface OrderService {

    //先定義一個方法,為了選擇客房!
    Order createOrder (CreateOrderRequest createOrderRequest);
    //確定客房資訊
    Order updateOrder (Order order);

}
