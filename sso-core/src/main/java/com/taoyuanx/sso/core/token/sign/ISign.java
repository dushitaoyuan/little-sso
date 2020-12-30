package com.taoyuanx.sso.core.token.sign;

/**
 * @author dushitaoyuan
 * @desc 签名接口
 * @date 2020/2/24
 */
public interface ISign {

    byte[] sign(byte[] data);

    boolean verifySign(byte[] data, byte[] sign);

}
