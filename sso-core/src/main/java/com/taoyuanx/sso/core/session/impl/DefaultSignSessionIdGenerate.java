package com.taoyuanx.sso.core.session.impl;

import com.taoyuanx.sso.core.dto.SSOUser;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.token.TokenForamtUtil;
import com.taoyuanx.sso.core.token.sign.ISign;
import com.taoyuanx.sso.core.utils.HelperUtil;

import java.util.regex.Pattern;

/**
 * @author dushitaoyuan
 * @desc sign session id 实现
 * @date 2020/12/29
 */
public class DefaultSignSessionIdGenerate implements SessionIdGenerate {
    public Pattern sessionIdPattern = Pattern.compile(".*\\..*");
    /**
     * 签名实现
     */
    private ISign sign;
    /**
     * 是否混淆数据
     */
    private boolean mixData;

    public DefaultSignSessionIdGenerate(ISign sign) {
        this(sign, false);
    }

    public DefaultSignSessionIdGenerate(ISign sign, boolean mixData) {
        this.sign = sign;
        this.mixData = mixData;
    }

    @Override
    public void generateSessionId(SSOUser ssoUser) {
        byte[] data = mixData(String.valueOf(ssoUser.getUserId())).getBytes();
        String sessionId = TokenForamtUtil.format(data, sign.sign(data));
        ssoUser.setSessionId(sessionId);
    }

    @Override
    public boolean isSessionIdValid(String sessionId) {
        parseSessionId(sessionId);
        return true;
    }

    @Override
    public String parseSessionId(String sessionId) {
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

    /**
     * 混淆数据 保证hmac每次签名都不一致
     */

    private String mixData(String uniqueId) {
        if (mixData) {
            return uniqueId + System.currentTimeMillis();
        }
        return uniqueId;
    }

    /**
     * 还原数据
     */
    private String parseMixData(String mixData) {
        if (this.mixData) {
            return mixData.substring(0, mixData.length() - 13);
        }
        return mixData;
    }
}
