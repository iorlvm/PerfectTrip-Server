package idv.tia201.g1.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.serial.SerialBlob;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Repository
@Table(name = "product_master")  // 指定對應的資料表名稱
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "price")  // 對應資料表中的 price 欄位
    private Integer price;

    @Column(name = "stock")  // 如果 stock 用於表示是否被預訂
    private int stock;

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
