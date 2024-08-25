package idv.tia201.g1.member.service;

import idv.tia201.g1.member.dto.UserLoginRequest;
import idv.tia201.g1.member.dto.UserQueryParams;
import idv.tia201.g1.member.dto.UserRegisterRequest;
import idv.tia201.g1.member.dto.UserUpdateRequest;
import idv.tia201.g1.member.entity.User;

import java.util.List;

public interface UserService {

    /**
     * Registers a new user.
     *
     * @param userRegisterRequest the request body containing the user's registration details
     * @return the ID of the newly registered user
     */
    public Integer register(UserRegisterRequest userRegisterRequest);

    /**
     * Logs in a user.
     *
     * @param userLoginRequest the request body containing the user's login credentials
     * @return the User object if login is successful
     */
    public User login(UserLoginRequest userLoginRequest);

    /**
     * Creates a new user.
     *
     * @param userRegisterRequest the request body containing the user's registration details
     * @return the ID of the newly created user
     */
    public Integer createUser(UserRegisterRequest userRegisterRequest);

    /**
     * Finds all users based on query parameters.
     *
     * @param userQueryParams the query parameters for filtering users
     * @return a list of User objects
     */
    public List<User> findAll(UserQueryParams userQueryParams);

    /**
     * Finds a user by their user ID.
     *
     * @param userId the ID of the user to find
     * @return the User object if found
     */
    public User findByUserId(Integer userId);

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to find
     * @return the User object if found
     */
    public User findByUsername(String username);

    /**
     * Counts the total number of users.
     *
     * @return the total number of users
     */
    public Long count();

    /**
     * Updates an existing user.
     *
     * @param userId            the ID of the user to update
     * @param userUpdateRequest the request body containing the user's updated details
     * @return the updated User object
     */
    public User updateUser(Integer userId, UserUpdateRequest userUpdateRequest);

    /**
     * Deletes a user by their user ID.
     *
     * @param userId the ID of the user to delete
     */
    public void deleteUser(Integer userId);

    public String exportAllUsersToExcel();

}
