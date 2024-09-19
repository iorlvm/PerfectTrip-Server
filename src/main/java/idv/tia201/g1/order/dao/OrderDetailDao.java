package idv.tia201.g1.order.dao;

import idv.tia201.g1.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

public interface OrderDetailDao extends JpaRepository<OrderDetail, Integer>{
    @Modifying
    @Query("UPDATE OrderDetail od " +
            "SET od.expiredTime = :expiredTime " +
            "WHERE od.orderId = :orderId")
    void updateExpiredTimeByOrderId(
            @Param("orderId") Integer orderId,
            @Param("expiredTime") Timestamp expiredTime);
}
