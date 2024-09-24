package idv.tia201.g1.order.dao;

import idv.tia201.g1.order.entity.OrderResidents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderResidentsDao extends JpaRepository<OrderResidents, Integer>{
    List<OrderResidents> findByOrderId(Integer orderId);
}
