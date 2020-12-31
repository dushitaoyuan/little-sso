package com.taoyuanx.sso.client.core.sign.sign.impl;


import com.taoyuanx.sso.client.core.sign.sign.IVerifySign;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;
import java.util.Arrays;

/**
 * @author dushitaoyuan
 * @desc hmac 签名验证
 * @date 2020/2/24
 */
public class HMacVerifySign implements IVerifySign {

    /**
     * mac 非线程安全
     */
    private Mac mac;

    public HMacVerifySign(Mac mac) {
        this.mac = mac;
    }

    public HMacVerifySign(byte[] macKey) {
        this.mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, macKey);
    }

    public HMacVerifySign(HmacAlgorithms hmacAlgorithms, byte[] macKey) {
        this.mac = HmacUtils.getInitializedMac(hmacAlgorithms, macKey);
    }


    @Override
    public synchronized boolean verifySign(byte[] data, byte[] sign) {
        byte[] calcSign = mac.doFinal(data);
        return Arrays.equals(sign, calcSign);
    }


}
