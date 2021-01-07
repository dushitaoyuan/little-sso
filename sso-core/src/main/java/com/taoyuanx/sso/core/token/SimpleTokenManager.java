package com.taoyuanx.sso.core.token;


import com.taoyuanx.sso.core.exception.SSOTokenException;
import com.taoyuanx.sso.core.token.sign.ISign;
import com.taoyuanx.sso.core.utils.HelperUtil;
import com.taoyuanx.sso.core.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.util.Objects;


/**
 * @author dushitaoyuan
 * @desc token 实现
 */
@Slf4j
public class SimpleTokenManager {
    private ISign signImpl;

    public SimpleTokenManager(ISign sign) {
        this.signImpl = sign;
    }

    public String createToken(SuperToken token) {

        try {
            byte[] data = JSONUtil.toJsonBytes(token);
            byte[] sign = doSign(data);
            return TokenForamtUtil.format(data, sign);
        } catch (Exception e) {
            log.error("生成token异常", e);
            throw new SSOTokenException("生成token异常", e);
        }
    }


    public <T extends SuperToken> T parseToken(String token, Class<? extends SuperToken> tokenClass) {
        if (HelperUtil.isEmpty(token)) {
            throw new SSOTokenException("token格式非法");
        }
        String[] split = TokenForamtUtil.splitToken(token);
        if (split.length != 2) {
            throw new SSOTokenException("token格式非法");
        }
        byte[] data = Base64.decodeBase64(split[TokenForamtUtil.DATA_INDEX]);
        SuperToken tokenObj = JSONUtil.parseObject(data, tokenClass);
        tokenObj.setData(data);
        tokenObj.setSign(split[TokenForamtUtil.SING_INDEX]);
        return (T) tokenObj;


    }

    public boolean verify(SuperToken token) {
        return verify(token, null);
    }

    public boolean verify(SuperToken token, Integer matchTokenType) {
        if (Objects.nonNull(matchTokenType) && !matchTokenType.equals(token.getType())) {
            throw new SSOTokenException("token 类型 非法");
        }
        long now = System.currentTimeMillis();

        Long start = token.getEffectTime();
        if (Objects.nonNull(start) && start > now) {
            throw new SSOTokenException("token尚未生效,请耐心等待");
        }
        Long end = token.getEndTime();
        if (Objects.nonNull(end) && end < now) {
            throw new SSOTokenException("token过期");
        }

        if (!doVerifySign(token.getData(), Base64.decodeBase64(token.getSign()))) {
            throw new SSOTokenException("token签名非法");
        }
        return true;
    }

    public boolean verify(String token) {
        SuperToken superToken = parseToken(token, SuperToken.class);
        return verify(superToken, null);
    }

    public boolean verify(String token, Integer matchTokenType) {
        SuperToken superToken = parseToken(token, SuperToken.class);
        return verify(superToken, matchTokenType);
    }

    private byte[] doSign(byte[] data) {
        try {
            return this.signImpl.sign(data);
        } catch (Exception e) {
            log.error("签名异常", e);
            throw new SSOTokenException("签名异常");
        }
    }


    private boolean doVerifySign(byte[] data, byte[] signValue) {
        try {
            return this.signImpl.verifySign(data, signValue);
        } catch (Exception e) {
            log.error("签名异常", e);
            throw new SSOTokenException("签名异常");
        }
    }

}
