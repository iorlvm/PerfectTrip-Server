package idv.tia201.g1.order.dao;

import idv.tia201.g1.order.dto.OrderProductDTO;
import idv.tia201.g1.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface OrderDetailDao extends JpaRepository<OrderDetail, Integer>{
    @Modifying
    @Query("UPDATE OrderDetail od " +
            "SET od.expiredTime = :expiredTime " +
            "WHERE od.orderId = :orderId")
    void updateExpiredTimeByOrderId(
            @Param("orderId") Integer orderId,
            @Param("expiredTime") Timestamp expiredTime);

    @Query("SELECT DISTINCT new idv.tia201.g1.order.dto.OrderProductDTO(od.productId, od.productName, od.quantity) FROM OrderDetail od " +
            "WHERE od.orderId = :orderId ")
    List<OrderProductDTO> getOrderProductByOrderId(@Param("orderId") Integer orderId);

    @Query(value = "SELECT pm.max_occupancy, SUM(od.quantity) " +
            "FROM order_detail od " +
            "JOIN order_master om ON od.order_id = om.order_id " +
            "JOIN product_master pm ON od.product_id = pm.product_id " +
            "WHERE om.pay_status <> '未付款' " +
            "GROUP BY pm.max_occupancy", nativeQuery = true)
    List<Object[]> findRoomTypeStats();
}