package idv.tia201.g1.member.entity;

import jakarta.persistence.*;

import java.util.Date;

public class CompanyReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_reviews_id")
    private Integer companyReviewsId;

    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "star_rank")
    private Integer starRank;

    @Column(name = "comment")
    private String comment;

    @Column(name = "change_id")
    private Integer changeId;

    @Column(name = "created_date", insertable = false)
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
        lastModifiedDate =new Date();
    }
}
