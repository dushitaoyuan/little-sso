package com.taoyuanx.littlesso.server.commons;

/**
 * @author dushitaoyuan
 * @date 2019/1/8 14:57
 * @desc 账户相关常量
 **/
public class AccountConstant {

    //账户正常
    public static final Integer ACCOUNT_NORMAL = 0;

    //账户冻结
    public static final Integer ACCOUNT_FROZEN = 1;


    //账户失效
    public static final Integer ACCOUNT_INVALID = -1;


    //登录验证码
    public static final String VAFY_CODE_SESSION_KEY = "login_verify_code";

    /**
     * global session_id (cookie ,param,header)
     */
    public static final String SSO_GLOBAL_SESSION_ID_KEY = "littlesso_session";
    /**
     * 全局token key 登录成功后由client保存
     */
    public static final String SSO_TOKEN_KEY = "littlesso_token";


    /**
     * 登录成功后跳转url参数key
     */
    public static final String REDIRECT_URL_PARAM_KEY = "redirectUrl";
    /**
     * 应用ID
     */
    public static final String APP_ID = "app_id";
}
