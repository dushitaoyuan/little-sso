package com.taoyuanx.sso.util;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * @author dushitaoyuan
 */
public class PasswordUtil {
    /**
     * 密码加密
     *
     * @param password 明文密码
     * @return
     */
    public static String passwordWithMd5Encode(String password) {
        return passwordEncode(md5Hashed(password));
    }

    public static String md5Hashed(String password) {
        return Hex.encodeHexString(DigestUtils.getMd5Digest().digest(password.getBytes()));
    }

    public static String passwordEncode(String hashedPassword) {
        return BCrypt.hashpw(hashedPassword, BCrypt.gensalt());
    }

    /**
     * 密码是否匹配
     *
     * @param encodePassword 已加密的密码
     * @param hashedPassword hash后的密码
     * @return
     */
    public static boolean passwordEqual(String encodePassword, String hashedPassword) {
        if (StringUtils.isEmpty(encodePassword) || StringUtils.isEmpty(hashedPassword)) {
            return false;
        }
        return BCrypt.checkpw(hashedPassword, encodePassword);
    }

}
