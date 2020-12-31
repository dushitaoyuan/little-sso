package com.taoyuanx.sso.client.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;

public final class RSAUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RSAUtil.class);
    public static final String DEFAILT_SIGN_ALGORITHM = "MD5withRSA";
    public static final String CERT_TYPE_X509 = "X.509";

    public static X509Certificate getPublicKeyCer(InputStream publicInput) throws Exception {
        CertificateFactory certificatefactory = CertificateFactory.getInstance(CERT_TYPE_X509);
        X509Certificate cert = (X509Certificate) certificatefactory.generateCertificate(publicInput);
        return cert;
    }


    public static boolean verifySign(byte[] data, byte[] sign, String signAlgorithm, PublicKey publicKey) {
        try {
            if (null == sign || sign.length == 0 || null == data || data.length == 0) {
                return false;
            }
            if (null == publicKey) {
                throw new Exception("rsa publicKey  is null");
            }
            if (StrUtil.isEmpty(signAlgorithm)) {
                signAlgorithm = DEFAILT_SIGN_ALGORITHM;
            }
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            LOG.error("验签异常{}", e);
            return false;
        }
    }

    public static PublicKey readPublicKey(File fileInputStream) {
        try {
            return RSAUtil.getPublicKeyCer(new FileInputStream(fileInputStream)).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("读取公钥失败", e);
        }

    }

    /**
     * 读取pem格式公钥
     */
    public static PublicKey readPublicKeyPEM(String pemString) {
        try {
            return RSAUtil.getPublicKeyCer(new ByteArrayInputStream(pemString.getBytes("UTF-8"))).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("读取公钥失败", e);
        }

    }


}
