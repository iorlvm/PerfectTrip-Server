package idv.tia201.g1.order.dao;

import idv.tia201.g1.order.entity.Order;
import idv.tia201.g1.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailDao extends JpaRepository<OrderDetail, Integer>{



}
