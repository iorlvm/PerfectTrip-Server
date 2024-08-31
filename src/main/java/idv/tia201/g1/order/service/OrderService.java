package idv.tia201.g1.order.service;

import idv.tia201.g1.order.dto.CreateOrderRequest;
import idv.tia201.g1.order.entity.Order;
import org.hibernate.sql.Update;

public interface OrderService {

    //先定義一個方法,為了選擇客房!
    Order createOrder (CreateOrderRequest createOrderRequest);
    //確定客房資訊
    Order updateOrder (Order order);


    //取得訂購人資料 (orderId? 裡面含有訂購人姓名)

    //是否套用優惠卷

    //正式結帳訂單 , 將此會員以通過的課程訂單狀態改為已結帳

}
