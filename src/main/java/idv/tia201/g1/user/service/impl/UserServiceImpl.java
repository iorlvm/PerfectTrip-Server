package idv.tia201.g1.user.service.impl;

import idv.tia201.g1.constant.UserGroup;
import idv.tia201.g1.dto.UserLoginRequest;
import idv.tia201.g1.dto.UserQueryParams;
import idv.tia201.g1.dto.UserRegisterRequest;
import idv.tia201.g1.dto.UserUpdateRequest;
import idv.tia201.g1.entity.User;
import idv.tia201.g1.user.dao.UserDao;
import idv.tia201.g1.user.service.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserDao userDao;

    /**
     * Registers a new user.
     *
     * @param userRegisterRequest the request body containing the user's registration details
     * @return the ID of the newly registered user
     */
    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {

        // Check if the username already exists
        User existingUserName = userDao.findByUsername(userRegisterRequest.getUsername());
        validateUsernameExists(existingUserName);

        // Check if the tax ID already exists, if provided
        if (userRegisterRequest.getTaxId() != null) {
            User existingTaxId = userDao.findByTaxId(userRegisterRequest.getTaxId());
            validateTaxIdExists(existingTaxId);
        }

        // Check if the phone number already exists, if provided
        if (userRegisterRequest.getPhoneNumber() != null) {
            User existingPhoneNumber = userDao.findByPhoneNumber(userRegisterRequest.getPhoneNumber());
            validatePhoneNumberExists(existingPhoneNumber);
        }

        // Hash the user's password
        String hashedPassword = getHashPassword(userRegisterRequest.getPassword());
        userRegisterRequest.setPassword(hashedPassword);

        // Create the user and return the user ID
        return createUser(userRegisterRequest);

    }

    /**
     * Logs in a user.
     *
     * @param userLoginRequest the request body containing the user's login credentials
     * @return the User object if login is successful
     * @throws ResponseStatusException if the username is not registered or the password is incorrect
     */
    @Override
    public User login(UserLoginRequest userLoginRequest) {

        // Find the user by their username
        User user = userDao.findByUsername(userLoginRequest.getUsername());

        // Validate that the username exists
        validateUsernameNotExists(user);

        // Validate the user's password
        validateUserPassword(user, userLoginRequest.getPassword());

        // Return the user object if login is successful
        return user;

    }

    /**
     * Creates a new user.
     *
     * @param userRegisterRequest the request body containing the user's registration details
     * @return the ID of the newly created user
     */
    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {

        // Build a User object from the registration request
        User user = buildUserFromRequest(userRegisterRequest);

        // Save the User object to the database
        User savedUser = userDao.save(user);

        // Return the ID of the newly created user
        return savedUser.getUserId();

    }

    /**
     * Finds all users based on query parameters.
     *
     * @param userQueryParams the query parameters for filtering users
     * @return a list of User objects
     */
    @Override
    public List<User> findAll(UserQueryParams userQueryParams) {

        Pageable pageable = PageRequest.of(
                userQueryParams.getOffset(),
                userQueryParams.getLimit(),
                Sort.by("createdDate").ascending()
        );

        return userDao.findAll(pageable).getContent();

    }

    /**
     * Finds a user by their user ID.
     *
     * @param userId the ID of the user to find
     * @return the User object if found
     */
    @Override
    public User findByUserId(Integer userId) {

        return userDao.findByUserId(userId);

    }

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to find
     * @return the User object if found
     */
    @Override
    public User findByUsername(String username) {

        return userDao.findByUsername(username);

    }

    /**
     * Counts the total number of users.
     *
     * @return the total number of users
     */
    @Override
    public Long count() {

        return userDao.count();

    }

    /**
     * Updates an existing user.
     *
     * @param userId            the ID of the user to update
     * @param userUpdateRequest the request body containing the user's updated details
     * @return the updated User object
     */
    @Override
    public User updateUser(Integer userId, UserUpdateRequest userUpdateRequest) {

        // Find the user by their user ID
        User user = userDao.findByUserId(userId);

        // Validate that the user ID exists
        validateUserIdNotExists(user);

        // Update the user's password if provided
        Optional.ofNullable(userUpdateRequest.getPassword()).ifPresent(password -> user.setPassword(getHashPassword(password)));

        // Update the user's first name if provided
        Optional.ofNullable(userUpdateRequest.getFirstName()).ifPresent(user::setFirstName);

        // Update the user's last name if provided
        Optional.ofNullable(userUpdateRequest.getLastName()).ifPresent(user::setLastName);

        // Update the user's nickname if provided
        Optional.ofNullable(userUpdateRequest.getNickname()).ifPresent(user::setNickname);

        // Update the user's tax ID if provided and validate it
        Optional.ofNullable(userUpdateRequest.getTaxId()).ifPresent(taxId -> {
            validateTaxIdExists(user);
            user.setTaxId(taxId);
        });

        // Update the user's gender if provided
        Optional.ofNullable(userUpdateRequest.getGender()).ifPresent(user::setGender);

        // Update the user's phone number if provided and validate it
        Optional.ofNullable(userUpdateRequest.getPhoneNumber()).ifPresent(phoneNumber -> {
            validatePhoneNumberExists(user);
            user.setPhoneNumber(phoneNumber);
        });

        // Update the user's country if provided
        Optional.ofNullable(userUpdateRequest.getCountry()).ifPresent(user::setCountry);

        // Update the user's change ID if provided
        Optional.ofNullable(userUpdateRequest.getChangeId()).ifPresent(user::setChangeId);

        // Set the last modified date to the current date
        user.setLastModifiedDate(new Date());

        // Save the updated User object to the database
        User userSaved = userDao.save(user);

        // Return the updated User object
        return userSaved;

    }

    /**
     * Deletes a user by their user ID.
     *
     * @param userId the ID of the user to delete
     */
    @Override
    public void deleteUser(Integer userId) {

        userDao.deleteById(userId);

    }

    @Override
    public String exportAllUsersToExcel() {

        String filePath = "src/main/resources/users.xlsx";

        List<User> userList = userDao.findAll();

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Users");

        Row header = sheet.createRow(0);

        String[] columns = {"User ID",
                "Username",
                "Password",
                "First Name",
                "Last Name",
                "Nickname",
                "Tax ID",
                "Gender",
                "User Group",
                "Phone Number",
                "Country",
                "Change ID",
                "Created Date",
                "Last Modified Date"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (User user : userList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getUserId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getPassword());
            row.createCell(3).setCellValue(user.getFirstName());
            row.createCell(4).setCellValue(user.getLastName());
            row.createCell(5).setCellValue(user.getNickname());
            row.createCell(6).setCellValue(user.getTaxId());
            row.createCell(7).setCellValue(user.getGender().toString());
            row.createCell(8).setCellValue(user.getUserGroup().toString());
            row.createCell(9).setCellValue(user.getPhoneNumber());
            row.createCell(10).setCellValue(user.getCountry());
            row.createCell(11).setCellValue(user.getChangeId());
            row.createCell(12).setCellValue(user.getCreatedDate().toString());
            row.createCell(13).setCellValue(user.getLastModifiedDate().toString());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filePath;

    }

    /**
     * Builds a User object from the given UserRegisterRequest.
     *
     * @param userRegisterRequest the request body containing the user's registration details
     * @return the built User object
     */
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

    /**
     * Validates that the user ID does not exist.
     *
     * @param user the User object to validate
     * @throws ResponseStatusException if the user ID does not exist
     */
    private void validateUserIdNotExists(User user) {

        if (user == null) {
            log.warn("該 user id {} 不存在", user.getUserId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not registered.");
        }

    }

    /**
     * Validates that the username exists.
     *
     * @param user the User object to validate
     * @throws ResponseStatusException if the username already exists
     */
    private void validateUsernameExists(User user) {

        if (user != null) {
            log.warn("該 username {} 已經被註冊", user.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered.");
        }

    }

    /**
     * Validates that the username does not exist.
     *
     * @param user the User object to validate
     * @throws ResponseStatusException if the username does not exist
     */
    private void validateUsernameNotExists(User user) {

        if (user == null) {
            log.warn("該 username {} 尚未註冊", user.getUsername());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not registered.");
        }

    }

    /**
     * Validates that the tax ID exists.
     *
     * @param user the User object to validate
     * @throws ResponseStatusException if the tax ID already exists
     */
    private void validateTaxIdExists(User user) {

        if (user != null) {
            log.warn("該 tax id {} 已經被註冊", user.getTaxId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tax id already registered.");
        }

    }

    /**
     * Validates that the phone number exists.
     *
     * @param user the User object to validate
     * @throws ResponseStatusException if the phone number already exists
     */
    private void validatePhoneNumberExists(User user) {

        if (user != null) {
            log.warn("該 phone number {} 已經被註冊", user.getPhoneNumber());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number already registered.");
        }

    }

    /**
     * Validates the user's password.
     *
     * @param user     the User object to validate
     * @param password the password to validate
     * @throws ResponseStatusException if the password is incorrect
     */
    private void validateUserPassword(User user, String password) {

        String hashedPassword = getHashPassword(password);

        if (!user.getPassword().equals(hashedPassword)) {
            log.warn("該 username {} 的密碼不正確", user.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password.");
        }

    }

    /**
     * Hashes the given password using MD5.
     *
     * @param password the password to hash
     * @return the hashed password
     */
    private String getHashPassword(String password) {

        return DigestUtils.md5DigestAsHex(password.getBytes());

    }

}
