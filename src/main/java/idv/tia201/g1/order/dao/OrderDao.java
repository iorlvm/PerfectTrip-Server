package idv.tia201.g1.order.dao;

import idv.tia201.g1.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDao extends JpaRepository<Order, Integer>{

//    Order createOrder(Order order);
    Order findByOrderId (Integer orderId);



}
