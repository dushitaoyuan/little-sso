package com.taoyuanx.sso.client.core;


/**
 * @author dushitaoyuan
 */
public class SSOClientConstant {


    public static final String SESSION_KEY_NAME = "sso_session_id";

    public static final String SSO_CLIENT_CONFIG_NAME = "sso_client_config";

    public static final String REDIRECT_URL = "redirectUrl";



    /**
     * sso 服务异常
     */
    public static final Integer SSO_SERVER_ERROR_CODE = 500;
    /**
     * login check code
     */
    public static final Integer LOGIN_CHECK_FAILED_CODE = 9999;


    public static final String COOKIE_STORE_PATH = "/";


    public static final String SSO_SESSION_TOKEN = "sessionToken";
    public static final String SSO_REFRESH_TOKEN = "refreshToken";


    public static final String SSO_TOKEN_EXPIRE = "expire";

    public  static  final String SESSION_MODE_CLIENT="client_session";

    public  static  final String SESSION_MODE_SERVER="server_session";
}
