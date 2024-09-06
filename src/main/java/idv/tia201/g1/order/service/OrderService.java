package idv.tia201.g1.order.service;

import idv.tia201.g1.order.dto.CreateOrderRequest;
import idv.tia201.g1.order.entity.Order;
import org.hibernate.sql.Update;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {


    //先定義一個方法,為了選擇客房!
    @Transactional
    Order createOrder (CreateOrderRequest createOrderRequest);
    //確定客房資訊
    Order updateOrder (Order order);
    //根據UserId取得訂單列表

    //正式結帳訂單 , 將此會員以通過的課程訂單狀態改為已結帳

}
