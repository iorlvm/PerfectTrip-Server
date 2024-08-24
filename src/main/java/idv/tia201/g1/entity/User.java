package idv.tia201.g1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import idv.tia201.g1.authentication.service.UserAuth;
import idv.tia201.g1.constant.Gender;
import idv.tia201.g1.constant.UserGroup;
import jakarta.persistence.*;

import java.util.Date;

import static idv.tia201.g1.utils.Constants.ROLE_USER;

@Entity
@Table(name = "user_master")
public class User implements UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username")
    private String username;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "tax_id")
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_group")
    private UserGroup userGroup;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "country")
    private String country;

    @Column(name = "change_id")
    private Integer changeId;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Transient
    private String token;

    public User() {

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer userId;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String nickname;
        private String taxId;
        private Gender gender;
        private UserGroup userGroup;
        private String phoneNumber;
        private String country;
        private Integer changeId;
        private Date createdDate;
        private Date lastModifiedDate;

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder taxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder userGroup(UserGroup userGroup) {
            this.userGroup = userGroup;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder changeId(Integer changeId) {
            this.changeId = changeId;
            return this;
        }

        public Builder createdDate(Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder lastModifiedDate(Date lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public User build() {
            User user = new User();
            user.userId = this.userId;
            user.username = this.username;
            user.password = this.password;
            user.firstName = this.firstName;
            user.lastName = this.lastName;
            user.nickname = this.nickname;
            user.taxId = this.taxId;
            user.gender = this.gender;
            user.userGroup = this.userGroup;
            user.phoneNumber = this.phoneNumber;
            user.country = this.country;
            user.changeId = this.changeId;
            user.createdDate = this.createdDate;
            user.lastModifiedDate = this.lastModifiedDate;
            return user;
        }

    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    @Override
    public String getRole() {
        return ROLE_USER;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
