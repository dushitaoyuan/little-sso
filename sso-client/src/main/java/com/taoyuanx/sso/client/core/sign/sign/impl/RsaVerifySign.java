package com.taoyuanx.sso.client.core.sign.sign.impl;

import com.taoyuanx.sso.client.core.sign.sign.IVerifySign;
import com.taoyuanx.sso.client.utils.RSAUtil;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author dushitaoyuan
 */
public class RsaVerifySign implements IVerifySign {


    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private String signAlg;

    public RsaVerifySign(RSAPublicKey publicKey, String signAlg) {
        //只验
        this.publicKey = publicKey;
        if (signAlg == null) {
            this.signAlg = "SHA256WITHRSA";
        }
    }




    @Override
    public boolean verifySign(byte[] data, byte[] sign) {
        return RSAUtil.verifySign(data, sign, signAlg, publicKey);
    }
}
