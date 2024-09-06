package idv.tia201.g1.order.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "quantity")
    private Integer quantity = 1; // default value

    @Column(name = "booked_date")
    private Date bookedDate;

    @Column(name = "expired_time")
    private Timestamp expiredTime;

//    @ManyToOne
//    @JoinColumn(name = "order_id", insertable = false, updatable = false)
//    private Order order;

//    @ManyToOne
//    @JoinColumn(name = "product_id", insertable = false, updatable = false)
//    private Product product;
}
