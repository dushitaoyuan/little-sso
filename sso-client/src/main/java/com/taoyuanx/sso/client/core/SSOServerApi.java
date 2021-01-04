package com.taoyuanx.sso.client.core;

/**
 * api 概览
 */
public enum SSOServerApi {
    LOGIN_CHECK("/sso/loginCheck", "登录检测"),
    GET_SSO_USER("/sso/user", "获取用户信息"),
    LOGOUT("/sso/logout", "退出"),
    ;
    public String path;
    public String desc;

    SSOServerApi(String path, String desc) {
        this.path = path;
        this.desc = desc;
    }
}
