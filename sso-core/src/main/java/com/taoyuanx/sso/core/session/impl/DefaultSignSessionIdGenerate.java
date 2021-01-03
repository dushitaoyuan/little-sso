package com.taoyuanx.sso.core.session.impl;

import com.taoyuanx.sso.core.exception.SSOException;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.token.TokenForamtUtil;
import com.taoyuanx.sso.core.token.sign.ISign;
import com.taoyuanx.sso.core.utils.HelperUtil;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author dushitaoyuan
 * @desc sign session id 实现
 * @date 2020/12/29
 */
public class DefaultSignSessionIdGenerate implements SessionIdGenerate {
    private ISign sign;

    public Pattern sessionIdPattern = Pattern.compile(".*\\..*");

    public DefaultSignSessionIdGenerate(ISign sign) {
        this.sign = sign;
    }

    @Override
    public String generate(String uniqueId) {
        byte[] data = uniqueId.getBytes();
        return TokenForamtUtil.format(data, sign.sign(data));
    }

    @Override
    public String isSessionIdValid(String sessionId) {
        if (HelperUtil.isEmpty(sessionId) || !sessionIdPattern.matcher(sessionId).matches()) {
            throw new SessionIdInvalidException();
        }
        byte[][] dataAndSign = TokenForamtUtil.splitTokenToByte(sessionId);
        byte[] data = dataAndSign[TokenForamtUtil.DATA_INDEX];
        byte[] sign = dataAndSign[TokenForamtUtil.SING_INDEX];
        if (!this.sign.verifySign(data, sign)) {
            throw new SessionIdInvalidException();
        }
        return new String(data);
    }

}
