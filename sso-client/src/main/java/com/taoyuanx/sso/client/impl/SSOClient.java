package com.taoyuanx.sso.client.impl;

import com.taoyuanx.sso.client.dto.SSOUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dushitaoyuan
 * @desc sso-client
 * @date 2020/12/30
 */
public interface SSOClient {
    /**
     * 登录校验
     */
    boolean loginCheck(String sessionId);


    /**
     * 获取用户信息
     */
    SSOUser getSSOUser(String sessionId);

    /**
     * 退出
     */
    void logout(String sessionId);

    /**
     * sessionId 是否合法
     */
    boolean isSessionIdValid(String sessionId);

    /**
     * 获取sessionId
     */
    String getSessionId(HttpServletRequest request);


}
