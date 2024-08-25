package idv.tia201.g1.member.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.core.service.TokenService;
import idv.tia201.g1.member.constant.Gender;
import idv.tia201.g1.member.dto.UserLoginRequest;
import idv.tia201.g1.member.dto.UserQueryParams;
import idv.tia201.g1.member.dto.UserRegisterRequest;
import idv.tia201.g1.member.dto.UserUpdateRequest;
import idv.tia201.g1.member.entity.User;
import idv.tia201.g1.member.service.UserService;
import idv.tia201.g1.core.utils.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * Handles HTTP POST requests to register a new user.
     *
     * @param userRegisterRequest the request body containing the user's registration details
     * @return a Result object containing the newly registered User object
     */
    @PostMapping("/users/register")
    public Result register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

        // Set the gender of the user to uppercase
        userRegisterRequest.setGender(Gender.valueOf(userRegisterRequest.getGender().toString().toUpperCase()));

        // Register the user and get the user ID
        Integer userId = userService.register(userRegisterRequest);

        // Find the user by their user ID
        User user = userService.findByUserId(userId);

        // Return the newly registered user wrapped in a Result object
        return Result.ok(user);

    }

    /**
     * Handles HTTP POST requests to log in a user.
     *
     * @param userLoginRequest the request body containing the user's login credentials
     * @return a Result object containing the User object with a generated token if login is successful
     */
    @PostMapping("/users/login")
    public Result login(@RequestBody @Valid UserLoginRequest userLoginRequest) {

        // Attempt to log in the user with the provided credentials
        User user = userService.login(userLoginRequest);

        // If the user is found, generate a token and set it in the user object
        if (user != null) {
            String token = tokenService.createToken(user);
            user.setToken(token);
        }

        // Return the user wrapped in a Result object
        return Result.ok(user);

    }

    /**
     * Handles HTTP GET requests to retrieve a user by their user ID.
     *
     * @param userId the ID of the user to retrieve
     * @return a Result object containing the User object
     */
    @GetMapping("/users/{userId}")
    public Result getUser(@PathVariable Integer userId) {

        // Find the user by their user ID
        User user = userService.findByUserId(userId);

        // Return the user wrapped in a Result object
        return Result.ok(user);

    }

    /**
     * Handles HTTP GET requests to retrieve a list of users with pagination and sorting options.
     *
     * @param orderBy the field by which to order the results, defaults to "created_date"
     * @param sort    the sort direction, either "asc" or "desc", defaults to "desc"
     * @param limit   the maximum number of users to return, defaults to 10, must be between 0 and 1000
     * @param offset  the starting point for the results, defaults to 0, must be 0 or greater
     * @return a Result object containing a Page of User objects
     */
    @GetMapping("/users")
    public Result getUsers(
            @RequestParam(defaultValue = "created_date") String orderBy,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset ) {

        // Create a UserQueryParams object and set its properties based on the request parameters
        UserQueryParams userQueryParams = new UserQueryParams();
        userQueryParams.setOrderBy(orderBy);
        userQueryParams.setSort(sort);
        userQueryParams.setLimit(limit);
        userQueryParams.setOffset(offset);

        // Retrieve the list of users based on the query parameters
        List<User> userList = userService.findAll(userQueryParams);

        // Get the total number of users
        Long total = userService.count();

        // Create a Page object to hold the paginated results
        Page<User> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setResult(userList);
        page.setTotal(total);

        // Return the paginated results wrapped in a Result object
        return Result.ok(page);
    }

    /**
     * Handles HTTP PUT requests to update an existing user.
     *
     * @param userId            the ID of the user to update
     * @param userUpdateRequest the request body containing the user's updated details
     * @return a Result object containing the updated User object
     */
    @PutMapping("/users/{userId}")
    public Result updateUser(@PathVariable Integer userId, @RequestBody @Valid UserUpdateRequest userUpdateRequest) {

        // Update the user with the provided user ID and details
        User user = userService.updateUser(userId, userUpdateRequest);

        // Return the updated user wrapped in a Result object
        return Result.ok(user);

    }

    /**
     * Handles HTTP DELETE requests to delete a user by their user ID.
     *
     * @param userId the ID of the user to delete
     * @return a Result object indicating the success of the operation
     */
    @DeleteMapping("/users/{userId}")
    public Result deleteUser(@PathVariable Integer userId) {

        // Delete the user with the provided user ID
        userService.deleteUser(userId);

        // Return a success response
        return Result.ok();

    }

    @GetMapping("/users/export")
    public Result exportAllUsersToExcel() {
        String path = userService.exportAllUsersToExcel();
        return Result.ok(path);
    }

}
