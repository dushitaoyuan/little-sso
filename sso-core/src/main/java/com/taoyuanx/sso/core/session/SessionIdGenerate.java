package com.taoyuanx.sso.core.session;

import com.taoyuanx.sso.core.dto.SSOUser;

/**
 * @author dushitaoyuan
 * @desc session id generate
 * @date 2020/12/29
 */
public interface SessionIdGenerate {

    void generateSessionId(SSOUser ssoUser);

    boolean isSessionIdValid(String sessionId);

    /**
     * 解析sessionId 获取数据
     */
    String parseSessionId(String sessionId);

}
