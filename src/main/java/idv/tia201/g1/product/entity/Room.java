package idv.tia201.g1.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "product_master")  // 指定對應的資料表名稱
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")  // 對應資料表中的 product_id 欄位
    private Long id;

    @Column(name = "category")  // 對應資料表中的 category 欄位
    private String roomType;

    @Column(name = "price")  // 對應資料表中的 price 欄位
    private int roomPrice;

    @Column(name = "stock")  // 如果 stock 用於表示是否被預訂
    private int stock;

    @Column(name = "isBooked")
    private boolean isBooked = false;

    @Lob
    @Column(name = "photo")  // 對應資料表中的 photo 欄位
    private Blob photo;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookedRoom> bookings;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "change_id")
    private Long changeId;

    @Column(name = "created_date")
    private Date createDate;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    public Long getChangeId() {
        return changeId;
    }

    public void setChangeId(Long changeId) {
        this.changeId = changeId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Room() {
        this.bookings = new ArrayList<>();
    }

    public void addBooking(BookedRoom booking) {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
        booking.setRoom(this);
        isBooked = true;
        long bookingCode = (long) (Math.random() * 1e10);
        String bookingCode1 = Long.toString(bookingCode);
        booking.setBookingConfirmCode(bookingCode1);
    }

    public void setCreatedDate(Date currentDate) {
        this.createDate = currentDate;
    }
}
