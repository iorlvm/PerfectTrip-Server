package idv.tia201.g1.user.controller;

import idv.tia201.g1.authentication.service.TokenService;
import idv.tia201.g1.dto.Result;
import idv.tia201.g1.dto.UserLoginRequest;
import idv.tia201.g1.dto.UserRegisterRequest;
import idv.tia201.g1.entity.User;
import idv.tia201.g1.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
    @Autowired
    private TokenService tokenService;

	@PostMapping("/users/register")
	public Result register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

		Integer userId = userService.register(userRegisterRequest);

		User user = userService.findByUserId(userId);

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

}
