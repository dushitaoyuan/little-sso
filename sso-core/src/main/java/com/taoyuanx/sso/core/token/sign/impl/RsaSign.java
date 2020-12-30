package com.taoyuanx.sso.core.token.sign.impl;

import com.taoyuanx.sso.core.token.sign.ISign;
import com.taoyuanx.sso.core.utils.RSAUtil;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author dushitaoyuan
 * @date 2020/2/24
 */
public class RsaSign implements ISign {


    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private String signAlg;

    public RsaSign(RSAPublicKey publicKey, String signAlg) {
        //只验
        this.publicKey = publicKey;
        if (signAlg == null) {
            this.signAlg = "SHA256WITHRSA";
        }
    }

    public RsaSign(RSAPublicKey publicKey, RSAPrivateKey privateKey, String signAlg) {
        //签验
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        if (signAlg == null) {
            this.signAlg = "SHA256WITHRSA";
        }
    }


    @Override
    public byte[] sign(byte[] data) {
        return RSAUtil.sign(data, signAlg, privateKey);
    }

    @Override
    public boolean verifySign(byte[] data, byte[] sign) {
        return RSAUtil.vefySign(data, sign, signAlg, publicKey);
    }
}
