package com.taoyuanx.sso.client.filter;


import com.alibaba.fastjson.JSON;
import com.taoyuanx.sso.client.core.Result;
import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.impl.SSOClient;
import com.taoyuanx.sso.client.utils.CookieUtil;
import com.taoyuanx.sso.client.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dushitaoyuan
 * @desc sso header or param 过滤器
 * @date 2020/12/29
 */
@Slf4j

public class SSOHeaderOrParamFilter extends SSOFilter {
    public SSOHeaderOrParamFilter() {
        super();
    }

    public SSOHeaderOrParamFilter(SSOClientConfig ssoClientConfig, SSOClient ssoClient) {
        super(ssoClientConfig, ssoClient);
    }

    @Override
    public void checkLoginFailedHandler(HttpServletRequest request, HttpServletResponse response) {
        Result result = new Result();
        result.setCode(SSOClientConstant.LOGIN_CHECK_FAILED_CODE);
        result.setMsg("login check failed,sso-client application must delete the sessionId and  must relogin");
        ResponseUtil.responseJson(response, JSON.toJSONString(result), 200);
    }

    @Override
    public void logOutSuccessHandler(HttpServletRequest request, HttpServletResponse response) {
        Result result = new Result();
        result.setCode(Result.SUCCESS_CODE);
        result.setMsg("logout success");
        ResponseUtil.responseJson(response, JSON.toJSONString(result), 200);
    }

}
