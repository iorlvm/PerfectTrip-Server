package idv.tia201.g1.utils;

import idv.tia201.g1.authentication.service.UserAuth;

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

    public static void removeUser(){
        threadLocal.remove();
    }
}
