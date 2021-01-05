package com.taoyuanx.sso.core.session;

/**
 * @author dushitaoyuan
 * @date 2020/12/29
 */
public interface TokenSessionManager extends SessionManager {

    Integer TOKEN_TYPE_SESSION = 1;
    Integer TOKEN_TYPE_REFRESH = 2;


}