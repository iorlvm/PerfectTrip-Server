package idv.tia201.g1.user.service;

import java.util.List;

import idv.tia201.g1.dto.UserLoginRequest;
import idv.tia201.g1.dto.UserQueryParams;
import idv.tia201.g1.dto.UserRegisterRequest;
import idv.tia201.g1.dto.UserUpdateRequest;
import idv.tia201.g1.entity.User;

public interface UserService {

	public Integer register(UserRegisterRequest userRegisterRequest);

	public User login(UserLoginRequest userLoginRequest);

	public Integer createUser(UserRegisterRequest userRegisterRequest);
	
	public List<User> findAll(UserQueryParams userQueryParams);

	public User findByUserId(Integer userId);

	public User findByUsername(String username);
	
	public Long count();

	public User updateUser(Integer userId, UserUpdateRequest userUpdateRequest);
	
	public void deleteUser(Integer userId);

}
