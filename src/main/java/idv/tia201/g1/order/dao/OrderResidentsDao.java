package idv.tia201.g1.order.dao;

import idv.tia201.g1.order.entity.OrderDetail;
import idv.tia201.g1.order.entity.OrderResidents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderResidentsDao extends JpaRepository<OrderResidents, Integer>{



}
