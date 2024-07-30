package idv.tia201.g1.user.service;

import idv.tia201.g1.dto.UserLoginRequest;
import idv.tia201.g1.dto.UserRegisterRequest;
import idv.tia201.g1.entity.User;

public interface UserService {

	public Integer register(UserRegisterRequest userRegisterRequest);

	public User login(UserLoginRequest userLoginRequest);

	public Integer createUser(UserRegisterRequest userRegisterRequest);

	public User findByUserId(Integer userId);

	public User findByUsername(String username);

}
