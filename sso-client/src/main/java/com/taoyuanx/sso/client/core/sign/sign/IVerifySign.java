package com.taoyuanx.sso.client.core.sign.sign;

/**
 * @author dushitaoyuan
 * @desc 签名验证接口
 * @date 2020/2/24
 */
public interface IVerifySign {


    boolean verifySign(byte[] data, byte[] sign);
}
