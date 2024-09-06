package idv.tia201.g1.product.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name = "product_discount")
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_discount_id")
    private Integer productDiscountId; // 商品折扣活動id

    @Column(name = "discount_title", length = 50)
    private String discountTitle; // 活動標題

    @Column(name = "discount_rate")
    private Float discountRate; // 打折

    @Column(name = "start_date_time")
    private Timestamp startDateTime; // 開始時間

    @Column(name = "end_date_time")
    private Timestamp endDateTime; // 結束時間

    @Column(name = "company_id")
    private Integer companyId; // 商家id (FK)

    @Column(name = "change_id")
    private Integer changeId; // 變更id

    @Column(name = "created_date", updatable = false)
    private Date createdDate; // 創建時間

    @Column(name = "last_modified_date")
    private Date lastModifiedDate; // 最後修改時間

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = new Date();
        }
        lastModifiedDate = createdDate;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = new Date();
    }
}
