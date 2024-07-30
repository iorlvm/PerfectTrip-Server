package idv.tia201.g1.user.dao;

import idv.tia201.g1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	public User findByUserId(Integer userId);

	public User findByUsername(String username);

}
