package idv.tia201.g1.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import idv.tia201.g1.authentication.service.TokenService;
import idv.tia201.g1.dto.Result;
import idv.tia201.g1.dto.UserLoginRequest;
import idv.tia201.g1.dto.UserQueryParams;
import idv.tia201.g1.dto.UserRegisterRequest;
import idv.tia201.g1.dto.UserUpdateRequest;
import idv.tia201.g1.entity.User;
import idv.tia201.g1.user.service.UserService;
import idv.tia201.g1.utils.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * Handles the POST request to register a new user.
     *
     * @param userRegisterRequest The registration details submitted by the user, validated before processing.
     * @return A {@link Result} containing the registered user's data if successful.
     */
    @PostMapping("/users/register")
    public Result register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

        // Register the user and obtain the generated user ID.
        Integer userId = userService.register(userRegisterRequest);

        // Retrieve the registered user details using the obtained user ID.
        User user = userService.findByUserId(userId);

        // Return a successful result including the user details.
        return Result.ok(user);

    }

    @PostMapping("/users/login")
    public Result login(@RequestBody @Valid UserLoginRequest userLoginRequest) {

        User user = userService.login(userLoginRequest);

        if (user != null) {
            String token = tokenService.createToken(user);
            user.setToken(token);
        }

        return Result.ok(user);

    }

    @GetMapping("/users/{userId}")
    public Result getUser(@PathVariable Integer userId) {

        User user = userService.findByUserId(userId);

        return Result.ok(user);

    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUsers(
            // Sorting
            @RequestParam(defaultValue = "created_date") String orderBy,
            @RequestParam(defaultValue = "desc") String sort,
            // Pagination
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset) {

        UserQueryParams userQueryParams = new UserQueryParams();
        userQueryParams.setOrderBy(orderBy);
        userQueryParams.setSort(sort);
        userQueryParams.setLimit(limit);
        userQueryParams.setOffset(offset);

        // 取得 user list
        List<User> userList = userService.findAll(userQueryParams);

        // 取得 user 總筆數
        Integer total = userService.countUser();

        // 分頁
        Page<User> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setResult(userList);
        page.setTotal(total);

        return ResponseEntity.status(HttpStatus.OK).body(page);

    }

    @PutMapping("/users/{userId}")
    public Result updateUser(@PathVariable Integer userId, @RequestBody @Valid UserUpdateRequest userUpdateRequest) {

        User user = userService.updateUser(userId, userUpdateRequest);

        return Result.ok(user);

    }

    @DeleteMapping("/users/{userId}")
    public Result deleteUser(@PathVariable Integer userId) {

        userService.deleteUser(userId);

        return Result.ok();

    }

}
