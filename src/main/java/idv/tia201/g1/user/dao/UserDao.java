package idv.tia201.g1.user.dao;

import idv.tia201.g1.constant.UserGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import idv.tia201.g1.entity.User;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    public User findByUserId(Integer userId);

    public User findByUsername(String username);

    public User findByTaxId(String taxId);

    public User findByPhoneNumber(String phoneNumber);

    public long count();

}
