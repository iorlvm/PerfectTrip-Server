package idv.tia201.g1.member.entity;

import idv.tia201.g1.core.entity.UserAuth;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.time.Instant;

import static idv.tia201.g1.core.utils.Constants.ROLE_ADMIN;

@Data
@Entity
@Table(name = "admin_master")
public class Admin implements UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private int adminId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "admin_group")
    private String adminGroup;

    @Column(name = "change_id", nullable = false)
    private int changeId;

    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private Timestamp lastModifiedDate;

    @Override
    public String getRole() {
        return ROLE_ADMIN;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdDate == null) {
            this.createdDate = Timestamp.from(Instant.now());
        }
        this.lastModifiedDate = this.createdDate;
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = Timestamp.from(Instant.now());
    }
}
