package com.taoyuanx.sso.core.token.sign.impl;


import com.taoyuanx.sso.core.token.sign.ISign;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;
import java.util.Arrays;

/**
 * @author dushitaoyuan
 * @desc hmac 签名
 * @date 2020/2/24
 */
public class HMacSign implements ISign {

    /**
     * mac 非线程安全
     */
    private Mac mac;

    public HMacSign(Mac mac) {
        this.mac = mac;
    }

    public HMacSign(byte[] macKey) {
        this.mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, macKey);
    }

    public HMacSign(HmacAlgorithms hmacAlgorithms, byte[] macKey) {
        this.mac = HmacUtils.getInitializedMac(hmacAlgorithms, macKey);
    }

    @Override
    public synchronized byte[] sign(byte[] data) {
        return mac.doFinal(data);
    }

    @Override
    public synchronized boolean verifySign(byte[] data, byte[] sign) {
        byte[] calcSign = mac.doFinal(data);
        return Arrays.equals(sign, calcSign);
    }


}
