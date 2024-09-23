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
    //使用者id
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "guest_count")
    private Integer guestCount;
    //訂單狀態
    @Column(name = "pay_status")  //未付款unpaid((臨時訂單) ; 已付款的情況下放入退款碼(萬一客人要退費)
    private String payStatus;
    //總金額
    @Column(name = "full_price")
    private Integer fullPrice;
    //服務費
    @Column(name = "service_fee")
    private Integer serviceFee;
    //折扣
    @Column(name = "discount")
    private Integer discount;
    //稅金
    @Column(name = "tax")
    private Integer tax;
    //實際金額
    @Column(name = "actual_price")
    private Integer actualPrice;
    //入住開始時間
    @Column(name = "start_date")
    private Date startDate;
    //入住結束時間
    @Column(name = "end_date")
    private Date endDate;
    //訂單要求
    @Column(name = "order_request")
    private String orderNotes;

    //希望入住時間
    @Column(name = "wished_time")
    private String wishedTime;
    //
    @Column(name = "change_id")
    private Integer changeId;
    //
    @Column(name = "created_date", updatable = false)
    private Timestamp createdDate;
//最後修改日期
    @Column(name = "last_modified_date")
    private Timestamp lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        if (changeId == null) changeId = 0;
        createdDate = new Timestamp(System.currentTimeMillis());
        lastModifiedDate = createdDate;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = new Timestamp(System.currentTimeMillis());
    }
}
