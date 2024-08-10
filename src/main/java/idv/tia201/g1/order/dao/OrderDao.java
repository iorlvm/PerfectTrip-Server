package idv.tia201.g1.order.dao;

import idv.tia201.g1.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDao extends JpaRepository<Order, Integer>{

    Order findByOrderId (Integer orderId);



}
