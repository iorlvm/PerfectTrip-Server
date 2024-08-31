package idv.tia201.g1.product.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "product_master")  // 指定對應的資料表名稱
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "price")
    private Integer price;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "change_id")
    private Integer changeId;

    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

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
