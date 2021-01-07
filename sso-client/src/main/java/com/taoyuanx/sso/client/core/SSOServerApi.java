package com.taoyuanx.sso.client.core;

/**
 * api 概览
 */
public enum SSOServerApi {
    LOGIN_CHECK("/sso/loginCheck", "登录检测"),
    GET_SSO_USER("/sso/user", "获取用户信息"),
    LOGOUT("/sso/logout", "退出"),


    TOKEN_REFRESH("/sso/token/refresh", "clientSession模式token refresh"),
    TOKEN_USER_DETAIL("/sso/token/userDetail", "clientSession模式用户详细数据"),
    ;
    public String path;
    public String desc;

    SSOServerApi(String path, String desc) {
        this.path = path;
        this.desc = desc;
    }
}
