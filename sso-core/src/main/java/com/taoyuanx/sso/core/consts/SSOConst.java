package com.taoyuanx.sso.core.consts;

/**
 * @author dushitaoyuan
 * @date 2020/12/29
 */
public class SSOConst {
    public static final String FILTER_EXCLUDE_PATH = "exclude_path";

    public static final String FILTER_PATH = "path";

    public static final String CONFIG_SPILT = ",";

    public static final String SSO_SESSION_ID = "sso_session_id";

    public static final String SSO_SESSION_UNIQUE_DATA = "sso_session_unique_data";

    public static final String LOGIN_URL = "/login";

    public static final String LOGOUT_URL = "/logout";


    public static final String LOGOUT_HTTP_METHOD = "GET";

    public static final String BEAN_PROVIDER = "bean_provider";

    /**
     * 跳转地址
     */
    public static final String SSO_REDIRECT_URL = "redirectUrl";
    /**
     * 未登录错误码
     */
    public static final Integer SSO_NOT_LOGIN_CODE =9999;
}
