package idv.tia201.g1.utils;

import idv.tia201.g1.authentication.service.UserAuth;
import idv.tia201.g1.entity.User;

import static idv.tia201.g1.utils.Constants.*;

public class UserHolder {
    private static final ThreadLocal<UserAuth> threadLocal = new ThreadLocal<>();

    public static void saveUser(UserAuth user) {
        threadLocal.set(user);
    }

    public static UserAuth getUser() {
        return threadLocal.get();
    }

    public static <R> R getUser(Class<R> userClass) {
        UserAuth user = threadLocal.get();
        if (userClass.isInstance(user)) {
            return userClass.cast(user);
        } else {
            throw new IllegalArgumentException("轉型失敗: 傳入的Class與物件型態不相符");
        }
    }

    public static String getRole() {
        UserAuth userAuth = getUser();
        return userAuth == null? null : userAuth.getRole();
    }

    public static Integer getId() {
        String role = getRole();
        if (role == null) return null;

        switch (role) {
            case ROLE_USER:
                return getUser(User.class).getUserId();
            case ROLE_COMPANY:
            case ROLE_ADMIN:
            default:
                return null;
        }
    }

    public static void removeUser(){
        threadLocal.remove();
    }
}
