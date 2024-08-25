package idv.tia201.g1.order.entity;


import idv.tia201.g1.member.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Table(name = "order_master")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "full_price", nullable = false)
    private int fullPrice;

    @Column(name = "discount", nullable = false)
    private int discount;

    @Column(name = "tax", nullable = false)
    private int tax;

    @Column(name = "service_fee", nullable = false)
    private int serviceFee;

    @Column(name = "actual_price", nullable = false)
    private int actualPrice;

    @Column(name = "actual_living", nullable = false, length = 50)
    private String actualLiving;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "change_id", nullable = false)
    private int changeId;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Timestamp createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private Timestamp lastModifiedDate;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = Timestamp.from(Instant.now());
        }
        lastModifiedDate = createdDate;
    }

    @PreUpdate
    protected void onUpdate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = Timestamp.from(Instant.now());
        }
    }

}
