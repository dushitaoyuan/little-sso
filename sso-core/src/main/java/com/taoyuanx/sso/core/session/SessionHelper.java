package com.taoyuanx.sso.core.session;

import com.taoyuanx.sso.core.dto.SSOUser;

/**
 * @author dushitaoyuan
 * @date 2020/12/29
 */
public interface SessionHelper {

    boolean isLogin(String sessionId);

    SSOUser getSSOUser(String sessionId);

    void logout(String sessionId);
}
