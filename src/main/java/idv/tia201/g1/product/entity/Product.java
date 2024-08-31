package idv.tia201.g1.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "product_master")  // 指定對應的資料表名稱
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")  // 對應資料表中的 product_id 欄位
    private Integer id;


    @Column(name = "price")  // 對應資料表中的 price 欄位
    private int roomPrice;

    @Column(name = "stock")  // 如果 stock 用於表示是否被預訂
    private int stock;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "change_id")
    private Long changeId;

    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    public Product() {

    }

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
