package idv.tia201.g1.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import idv.tia201.g1.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	public User findByUserId(Integer userId);
	
	public User findByUsername(String username);

	public User findByTaxId(String taxId);

	public User findByPhoneNumber(String phoneNumber);
	
	@Query(value = "SELECT count(*) FROM user_master", nativeQuery = true)
	public Integer countUser();
	
}
