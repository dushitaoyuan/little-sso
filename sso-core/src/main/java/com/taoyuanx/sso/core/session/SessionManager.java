package com.taoyuanx.sso.core.session;

import com.taoyuanx.sso.core.dto.SSOUser;

/**
 * @author dushitaoyuan
 * @date 2020/12/29
 * session 管理接口
 */
public interface SessionManager {
    void createSession(SSOUser ssoUser);

    boolean isLogin(String sessionId);

    SSOUser getSSOUser(String sessionId);

    void logout(String sessionId);
}
