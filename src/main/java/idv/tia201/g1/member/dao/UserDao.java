package idv.tia201.g1.member.dao;

import idv.tia201.g1.member.dto.UserQueryParams;
import idv.tia201.g1.member.entity.User;
import idv.tia201.g1.statistics.dto.CustomerSourceData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    /**
     * Finds a user by their user ID.
     *
     * @param userId the ID of the user to find
     * @return the User object with the specified user ID
     */
    public User findByUserId(Integer userId);

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to find
     * @return the User object with the specified username
     */
    public User findByUsername(String username);

    /**
     * Finds a user by their tax ID.
     *
     * @param taxId the tax ID of the user to find
     * @return the User object with the specified tax ID
     */
    public User findByTaxId(String taxId);

    /**
     * Finds a user by their phone number.
     *
     * @param phoneNumber the phone number of the user to find
     * @return the User object with the specified phone number
     */
    public User findByPhoneNumber(String phoneNumber);

    /**
     * Counts the total number of users.
     *
     * @return the total number of users
     */
    public long count();


    @Query("SELECT u.gender, COUNT(u) FROM User u GROUP BY u.gender")
    List<Object[]> findGenderStatistics();

    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdDate) = CURRENT_DATE")
    Long countNewCustomersToday();

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdDate >= :startDate")
    Long countNewCustomersInLast30Days(@Param("startDate") LocalDateTime startDate);
}
