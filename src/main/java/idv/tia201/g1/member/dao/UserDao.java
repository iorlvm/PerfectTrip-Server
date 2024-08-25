package idv.tia201.g1.member.dao;

import idv.tia201.g1.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}
