package idv.tia201.g1.utils;

public class Constants {
    public static final String ROLE_USER = "user";
    public static final String ROLE_COMPANY = "company";
    public static final String ROLE_ADMIN = "admin";
    public static final String LOGIN_USER = "login:";
    public static final Long LOGIN_TTL = 3600L;
    public static final String LOCK_IMG = "lock:img:";
    public static final String CACHE_IMG = "cache:img:";
    public static final Long CACHE_IMG_SIZE = 51200L; // 50 * 1024 (50KB)
    public static final Long CACHE_IMG_DATA_TTL = 60L;
    public static final Long CACHE_IMG_STATUS_TTL = 30L;
    public static final Long CACHE_IMG_NATURAL_TTL = 1800L;
    public static final String CHAT_ACTION_SEND_MESSAGE = "send-message";
    public static final String CHAT_ACTION_READ_MESSAGE = "read-message";
    public static final String CHAT_ACTION_UPDATE_USER_INFO = "update-user-info";
    public static final String CHAT_ACTION_UPDATE_ROOM_INFO = "update-room-info";
    public static final String CHAT_ACTION_UPDATE_NOTIFY = "update-notify";
    public static final String CHAT_ACTION_UPDATE_PINNED = "update-pinned";

}
