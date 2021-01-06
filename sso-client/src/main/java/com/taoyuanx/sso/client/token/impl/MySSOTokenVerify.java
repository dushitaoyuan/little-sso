package com.taoyuanx.sso.client.token.impl;

import com.alibaba.fastjson.JSON;
import com.taoyuanx.sso.client.core.sign.sign.IVerifySign;
import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.ex.SSOTokenException;
import com.taoyuanx.sso.client.token.AbstractSSOTokenVerify;
import com.taoyuanx.sso.client.token.SSOToken;
import com.taoyuanx.sso.client.utils.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.util.Objects;

/**
 * @author dushitaoyuan
 * @date 2020/12/29
 * 自定义token  实现
 */
@Slf4j
public class MySSOTokenVerify extends AbstractSSOTokenVerify {
    private IVerifySign verifySign;

    public MySSOTokenVerify(IVerifySign verifySign) {
        this.verifySign = verifySign;
    }


    @Override
    public SSOUser parseToken(String token) {
        if (StrUtil.isEmpty(token)) {
            throw new SSOTokenException("token格式非法");
        }
        if (Objects.isNull(token)) {
            throw new SSOTokenException("token格式非法");
        }
        String[] split = token.split("\\.");
        if (split.length != 2) {
            throw new SSOTokenException("token格式非法");
        }
        byte[] data = Base64.decodeBase64(split[0]);
        SSOToken tokenObj = JSON.parseObject(data, SSOToken.class);
        return tokenObj.getSsoUser();
    }

    @Override
    public boolean verify(String token, Integer matchTokenType) {
        if (StrUtil.isEmpty(token)) {
            throw new SSOTokenException("token格式非法");
        }
        String[] split = token.split("\\.");
        if (split.length != 2) {
            throw new SSOTokenException("token格式非法");
        }
        byte[] data = Base64.decodeBase64(split[0]);
        byte[] sign = Base64.decodeBase64(split[1]);
        SSOToken tokenObj = JSON.parseObject(data, SSOToken.class);
        long now = System.currentTimeMillis();
        Long start = tokenObj.getEffectTime();
        if (Objects.nonNull(start) && start > now) {
            throw new SSOTokenException("token尚未生效,请耐心等待");
        }
        Long end = tokenObj.getEndTime();
        if (Objects.nonNull(end) && end < now) {
            throw new SSOTokenException("token过期");
        }
        if (!verifySign.verifySign(data, sign)) {
            throw new SSOTokenException("token签名非法");
        }
        return true;
    }
}
