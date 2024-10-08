package idv.tia201.g1.order.service;

import idv.tia201.g1.order.dto.CreateOrderRequest;
import idv.tia201.g1.order.dto.OrderDTO;
import idv.tia201.g1.order.dto.UpdateOrderRequest;
import idv.tia201.g1.order.entity.Order;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {

    //先定義一個方法,為了選擇客房!
    @Transactional
    Order createOrder(CreateOrderRequest createOrderRequest);

    //確定客房資訊
    Order updateOrder(Integer orderId, UpdateOrderRequest updateOrderRequest);

    //根據UserId取得訂單列表
    Page<Order> getOrdersByUserId(Integer userId, Pageable pageable);

    //根據CompanyId取得訂單列表
    Page<Order> getOrdersByCompanyId(Integer companyId, Pageable pageable);

    //Admin取得所有訂單列表
    Page<Order> getValidOrders(Pageable pageable);

    //正式結帳訂單 , 將此會員以通過的課程訂單狀態改為已結帳


    //透過 orderId 取得訂單資料，並將結果以 OrderDTO
    OrderDTO getOrder(Integer orderId);
   //傳入我們找到的訂單 把訂單細節補完
   List<OrderDTO> getOrderDTOs(List<Order> orderList);

   void deleteByOrderId(Integer orderId);
}
