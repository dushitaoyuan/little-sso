package com.taoyuanx.sso.core.consts;

/**
 * @author dushitaoyuan
 * @date 2020/12/29
 */
public class SSOConst {


    public static final String SSO_SESSION_ID = "sso_session_id";
    /**
     * 跳转地址
     */
    public static final String SSO_REDIRECT_URL = "redirectUrl";


    public static final String SSO_SESSION_TOKEN = "sessionToken";


    public static final String SSO_REFRESH_TOKEN = "refreshToken";


    public static final String SSO_TOKEN_EXPIRE = "expire";


    public static final String SESSION_MODE_CLIENT = "client";

    public static final String SESSION_MODE_SERVER = "server";


    /**
     * sso 服务异常
     */
    public static final Integer SSO_SERVER_ERROR_CODE = 500;
    /**
     * login check code
     */
    public static final Integer LOGIN_CHECK_FAILED_CODE = 9999;

    /**
     * session 默认过期时间
     */
    public static final Integer DEFAULT_SESSION_TIME_OUT_MINUTE = 8 * 60;
    /**
     * cookie path
     */
    public static final String SSO_COOKIE_PATH = "/";


    public static final String DEFAULT_HMAC_KEY = "dushitaoyuan";
}
