package idv.tia201.g1.order.entity;


import idv.tia201.g1.member.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
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

    //訂單狀態 EX:已完成訂單
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    //折扣前金額(快照)
    @Column(name = "full_price", nullable = false)
    private int fullPrice;

    //折扣(快照)
    @Column(name = "discount", nullable = false)
    private int discount;

    //稅金(快照)
    @Column(name = "tax", nullable = false)
    private int tax;

    //手續費(快照)
    @Column(name = "service_fee", nullable = false)
    private int serviceFee;

    //實際金額
    @Column(name = "actual_price", nullable = false)
    private int actualPrice;

    //實際入住人名
    @Column(name = "actual_living", nullable = false, length = 50)
    private String actualLiving;

    //FK;由哪位使用者下訂的
    @Column(name = "user_id", nullable = false)
    private int userId;

    //FK;由哪一間旅館的房間
    @Column(name = "change_id", nullable = false)
    private int changeId;

    //開始入住日期
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    //退房日期
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    //訂單創建日期
    @Column(name = "created_date", nullable = false, updatable = false)
    private Timestamp createdDate;

    //
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
//        if (lastModifiedDate == null) {
//            lastModifiedDate = Timestamp.from(Instant.now());
//        }
    }

}
