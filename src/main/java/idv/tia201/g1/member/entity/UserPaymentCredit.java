package idv.tia201.g1.member.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "user_payment_credit")
public class UserPaymentCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    @Column(name = "user_payment_credit_id")
    private Integer userPaymentCreditId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "credit_card_id")
    private Integer creditCardId;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "change_id")
    private Integer changeId;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    public UserPaymentCredit() {

    }

    public Integer getUserPaymentCreditId() {
        return userPaymentCreditId;
    }

    public void setUserPaymentCreditId(Integer userPaymentCreditId) {
        this.userPaymentCreditId = userPaymentCreditId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(Integer creditCardId) {
        this.creditCardId = creditCardId;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getChangeId() {
        return changeId;
    }

    public void setChangeId(Integer changeId) {
        this.changeId = changeId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
