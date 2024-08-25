package idv.tia201.g1.entity;

import idv.tia201.g1.authentication.service.UserAuth;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

import static idv.tia201.g1.utils.Constants.ROLE_COMPANY;

@Data
@Entity
@Table(name = "company_master")
public class Company implements Serializable, UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private int companyId;

    @Setter
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Setter
    @Getter
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "vat_number", nullable = false, unique = true)
    private String vatNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "telephone", nullable = false)
    private String telephone;

    @Column(name = "score", nullable = false)
    private float score;

    @Column(name = "change_id", nullable = false)
    private int changeId;

    @Column(name = "created_date", nullable = false, updatable = false)//更新時忽略這個欄位
    private Date createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate;

    @Column(name = "pass", nullable = false)
    private String pass;

    @Transient
    private String token;

    @Override
    public String getRole() {
        return ROLE_COMPANY;
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
            lastModifiedDate =new Date();
    }
}

