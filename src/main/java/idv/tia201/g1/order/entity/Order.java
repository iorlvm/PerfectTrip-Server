package idv.tia201.g1.order.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "order_master")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "pay_status")
    private String payStatus;

    @Column(name = "full_price")
    private Integer fullPrice;

    @Column(name = "service_fee")
    private Integer serviceFee;

    @Column(name = "discount")
    private Integer discount;

    @Column(name = "tax")
    private Integer tax;

    @Column(name = "actual_price")
    private Integer actualPrice;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "order_request")
    private String orderRequest;

    //希望入住時間
    @Column(name = "wished_time")
    private String wishedTime;

    @Column(name = "change_id")
    private Integer changeId;

    @Column(name = "created_date", updatable = false)
    private Timestamp createdDate;

    @Column(name = "last_modified_date")
    private Timestamp lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = new Timestamp(System.currentTimeMillis());
        lastModifiedDate = createdDate;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = new Timestamp(System.currentTimeMillis());
    }
}
