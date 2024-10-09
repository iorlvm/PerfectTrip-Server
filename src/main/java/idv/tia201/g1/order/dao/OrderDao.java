package idv.tia201.g1.order.dao;

import idv.tia201.g1.order.entity.Order;
import idv.tia201.g1.statistics.dto.OrderStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public interface OrderDao extends JpaRepository<Order, Integer> {
    /**
     * 排程使用 : 刪除所有未付款且已過期的臨時訂單
     */
    @Modifying
    @Query("DELETE FROM Order o " +
            "WHERE o.payStatus = '未付款' " +
            "AND o.orderId IN " +
            "(SELECT od.orderId FROM OrderDetail od WHERE od.expiredTime < CURRENT_TIMESTAMP)")
    void deleteExpiredOrders();

    Order findByOrderId(Integer orderId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN OrderDetail od ON od.orderId = o.orderId " +
            "WHERE o.userId = :userId AND " +
            "(od.expiredTime > CURRENT_TIMESTAMP OR o.payStatus <> '未付款')")
    Page<Order> findByUserId(@Param("userId")Integer userId, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "JOIN OrderDetail od ON od.orderId = o.orderId " +
            "JOIN Product p ON p.productId = od.productId " +
            "WHERE p.companyId = :companyId AND " +
            "(od.expiredTime > CURRENT_TIMESTAMP OR o.payStatus <> '未付款')")
    Page<Order> findByCompanyId(@Param("companyId") Integer companyId, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "JOIN OrderDetail od ON od.orderId = o.orderId " +
            "JOIN Product p ON p.productId = od.productId " +
            "WHERE od.expiredTime > CURRENT_TIMESTAMP OR o.payStatus <> '未付款'")
    Page<Order> findValidOrders(Pageable pageable);

    @Query("SELECT SUM(p.price * od.quantity) FROM Order o " +
            "JOIN OrderDetail od ON od.orderId = o.orderId " +
            "JOIN Product p ON p.productId = od.productId " +
            "WHERE o.orderId = :orderId")
    Integer calculateTotalPrice(@Param("orderId") Integer orderId);

    @Query("SELECT MIN(pd.discountRate) " +
            "FROM ProductDiscount pd " +
            "WHERE pd.companyId = :companyId " +
            "AND pd.startDateTime <= :date " +
            "AND pd.endDateTime >= :date")
    Double getDiscountByCompanyIdAndDate(
            @Param("companyId") Integer companyId,
            @Param("date") Date date);


    @Query(value = "SELECT DATE(o.created_date) as orderDate, COUNT(o.order_id), SUM(o.actual_price) " +
            "FROM order_master o " +
            "WHERE o.created_date >= :startDate AND o.pay_status <> '未付款' " +
            "GROUP BY DATE(o.created_date)", nativeQuery = true)
    List<Object[]> findLast7DaysOrderStats(@Param("startDate") LocalDate startDate);

    @Query("SELECT SUM(o.actualPrice) FROM Order o " +
            "WHERE o.payStatus <> '未付款' AND o.createdDate BETWEEN :startDate AND :endDate")
    Long findRevenueBetweenDates(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
}