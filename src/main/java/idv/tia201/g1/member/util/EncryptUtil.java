package idv.tia201.g1.member.util;

import org.springframework.util.DigestUtils;

public class EncryptUtil {

    /**
     * Hashes the given password using MD5.
     *
     * @param password the password to hash
     * @return the hashed password
     */
    public static String encryptWithMD5(String password) {

        return DigestUtils.md5DigestAsHex(password.getBytes());

    }

    private EncryptUtil() {

    }

}
