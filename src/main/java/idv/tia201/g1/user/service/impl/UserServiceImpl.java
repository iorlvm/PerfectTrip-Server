package idv.tia201.g1.user.service.impl;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

import idv.tia201.g1.constant.UserGroup;
import idv.tia201.g1.dto.UserLoginRequest;
import idv.tia201.g1.dto.UserQueryParams;
import idv.tia201.g1.dto.UserRegisterRequest;
import idv.tia201.g1.dto.UserUpdateRequest;
import idv.tia201.g1.entity.User;
import idv.tia201.g1.user.dao.UserDao;
import idv.tia201.g1.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserDao userDao;

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {

        User user = userDao.findByUsername(userRegisterRequest.getUsername());

        // 驗證 username 是否已被註冊
        validateUsernameExists(user);

        // 使用 MD5 生成密碼的雜湊值
        String hashedPassword = getHashPassword(userRegisterRequest.getPassword());
        userRegisterRequest.setPassword(hashedPassword);

        // 創建帳號
        return createUser(userRegisterRequest);

    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {

        User user = userDao.findByUsername(userLoginRequest.getUsername());

        // 驗證 username 是否未被註冊
        validateUsernameNotExists(user);

        // 驗證密碼是否正確
        validateUserPassword(user, userLoginRequest.getPassword());

        return user;

    }

    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {

        User user = buildUserFromRequest(userRegisterRequest);

        User savedUser = userDao.save(user);

        return savedUser.getUserId();

    }

    @Override
    public List<User> findAll(UserQueryParams userQueryParams) {

        Pageable pageable = PageRequest.of(
                userQueryParams.getOffset(),
                userQueryParams.getLimit(),
                Sort.by("createdDate").ascending()
        );

        return userDao.findAll(pageable).getContent();

    }

    @Override
    public User findByUserId(Integer userId) {

        return userDao.findByUserId(userId);

    }

    @Override
    public User findByUsername(String username) {

        return userDao.findByUsername(username);

    }

    @Override
    public Integer countUser() {

        return userDao.countUser();

    }

    @Override
    public User updateUser(Integer userId, UserUpdateRequest userUpdateRequest) {

        User user = userDao.findByUserId(userId);

        // 驗證 user id 是否存在
        validateUserIdNotExists(user);

        Optional.ofNullable(userUpdateRequest.getPassword()).ifPresent(password -> user.setPassword(getHashPassword(password)));
        Optional.ofNullable(userUpdateRequest.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(userUpdateRequest.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(userUpdateRequest.getNickname()).ifPresent(user::setNickname);
        Optional.ofNullable(userUpdateRequest.getTaxId()).ifPresent(user::setTaxId);
        Optional.ofNullable(userUpdateRequest.getGender()).ifPresent(user::setGender);
        Optional.ofNullable(userUpdateRequest.getPhoneNumber()).ifPresent(user::setPhoneNumber);
        Optional.ofNullable(userUpdateRequest.getCountry()).ifPresent(user::setCountry);
        Optional.ofNullable(userUpdateRequest.getChangeId()).ifPresent(user::setChangeId);

        user.setLastModifiedDate(new Date());

        User userSaved = userDao.save(user);

        return userSaved;

    }

    @Override
    public void deleteUser(Integer userId) {

        userDao.deleteById(userId);

    }

    private User buildUserFromRequest(UserRegisterRequest userRegisterRequest) {

        return User.builder()
                .username(userRegisterRequest.getUsername())
                .password(userRegisterRequest.getPassword())
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .nickname(userRegisterRequest.getNickname())
                .taxId(userRegisterRequest.getTaxId())
                .gender(userRegisterRequest.getGender())
                .userGroup(UserGroup.NORMAL)
                .phoneNumber(userRegisterRequest.getPhoneNumber())
                .country(userRegisterRequest.getCountry())
                .changeId(0)
                .createdDate(new Date())
                .lastModifiedDate(new Date())
                .build();

    }

    private void validateUserIdNotExists(User user) {

        if (user == null) {
            log.warn("該 user id {} 不存在", user.getUserId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not registered.");
        }

    }

    private void validateUsernameExists(User user) {

        if (user != null) {
            log.warn("該 username {} 已經被註冊", user.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered.");
        }

    }

    private void validateUsernameNotExists(User user) {

        if (user == null) {
            log.warn("該 username {} 尚未註冊", user.getUsername());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not registered.");
        }

    }

    private void validateUserPassword(User user, String password) {

        String hashedPassword = getHashPassword(password);

        if (!user.getPassword().equals(hashedPassword)) {
            log.warn("該 username {} 的密碼不正確", user.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password.");
        }

    }

    private String getHashPassword(String password) {

        return DigestUtils.md5DigestAsHex(password.getBytes());

    }

}
