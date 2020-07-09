package com.ncs.ticket.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;

/**
 * @author dushitaoyuan
 * @desc aes加密标记 需单例实例
 * @date 2019/12/17
 */
public class AesTagUtil {

    //标记前缀特殊字符
    public static final String TAG_FLAG = "$t";
    private byte[] password;
    private byte[] iv;
    private int tagLength = TAG_FLAG.length();
    /**
     * 加密模式
     */
    public static final String CIPHER_MODE = "AES/CBC/PKCS5Padding";

    public AesTagUtil(byte[] password, byte[] iv) {
        this.password = password;
        this.iv = iv;
    }

    public String encode(String content) throws Exception {
        if (content.startsWith(TAG_FLAG)) {
            return content;
        }
        byte[] result = encode(content.getBytes("UTF-8"));
        return TAG_FLAG + Base64.encodeBase64String(result);

    }

    public String decode(String encode) throws Exception {
        if (encode.startsWith(TAG_FLAG)) {
            encode = encode.substring(tagLength);
        } else {
            return encode;
        }
        return new String(decode(Base64.decodeBase64(encode)), "UTF-8");
    }

    /**
     * 非字符加密不标记
     */



    public byte[] encode(byte[] data) throws Exception {
        Cipher cipher = AesHelper.getAesCipher(iv, password, Cipher.ENCRYPT_MODE, CIPHER_MODE);
        byte[] result = cipher.doFinal(data);
        return result;
    }

    public byte[] decode(byte[] encode) throws Exception {
        Cipher cipher = AesHelper.getAesCipher(iv, password, Cipher.DECRYPT_MODE, CIPHER_MODE);
        byte[] result = cipher.doFinal(encode);
        return result;
    }
}
