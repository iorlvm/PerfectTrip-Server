package idv.tia201.g1.user.dao;

import idv.tia201.g1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	public User findByUserId(Integer userId);
	
	public User findByUsername(String username);
	
//	@Query("UPDATE User u SET " +
//	       "u.password = :#{#user.password}, " +
//	       "u.firstName = :#{#user.firstName}, " +
//	       "u.lastName = :#{#user.lastName}, " +
//	       "u.nickName = :#{#user.nickName}, " +
//	       "u.taxId = :#{#user.taxId}, " +
//	       "u.gender = :#{#user.gender}, " +
//	       "u.phoneNumber = :#{#user.phoneNumber}, " +
//	       "u.country = :#{#user.country}, " +
//	       "u.changeId = :#{#user.changeId} " +
//	       "WHERE u.userId = :userId")
//	public void updateUserInfo(@Param("userId") Integer userId, @Param("user") User user);

}
