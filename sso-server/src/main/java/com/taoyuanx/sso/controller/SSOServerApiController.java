package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.dto.Result;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SSOTokenException;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dushitaoyuan
 * @desc sso api接口 对应 sso-client
 * @date 2021/1/323:56
 */
@RequestMapping("/sso")
@ResponseBody
@Controller
@Slf4j
public class SSOServerApiController {
    @Autowired
    SessionManager sessionManager;
    @Autowired
    SSOProperties ssoProperties;


    @GetMapping("logout")
    @ResponseBody
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getParameter(ssoProperties.getSessionKeyName());
        try {
            sessionManager.logout(sessionId);
            return ResultBuilder.success();
        } catch (SessionIdInvalidException e) {
            log.error("sessionId is invalid", e);
            return ResultBuilder.failed(SSOConst.LOGIN_CHECK_FAILED_CODE, e.getMessage());
        }

    }

    @GetMapping("loginCheck")
    @ResponseBody

    public Result loginCheck(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getParameter(ssoProperties.getSessionKeyName());
        try {
            if (!sessionManager.isLogin(sessionId)) {
                return ResultBuilder.failed(SSOConst.LOGIN_CHECK_FAILED_CODE, "loginCheck false");
            }
            return ResultBuilder.success();
        } catch (SessionIdInvalidException e) {
            log.error("sessionId is invalid", e);
            return ResultBuilder.failed(SSOConst.LOGIN_CHECK_FAILED_CODE, e.getMessage());
        }

    }

    @GetMapping("user")
    @ResponseBody
    public Result getSSOUser(HttpServletRequest request) {
        try {
            String sessionId = request.getParameter(ssoProperties.getSessionKeyName());
            return ResultBuilder.success(sessionManager.getSSOUser(sessionId));
        } catch (SessionIdInvalidException e) {
            log.error("sessionId is invalid", e);
            return ResultBuilder.failed(SSOConst.LOGIN_CHECK_FAILED_CODE, e.getMessage());
        }
    }

    /**
     * 获取tokenUser的详细信息
     */
    @GetMapping("/token/userDetail")
    @ResponseBody
    public Result getSSOUserDetail(HttpServletRequest request) {
        try {
            if (ssoProperties.getSessionMode().equals(SSOConst.SESSION_MODE_CLIENT)) {
                String sessionToken = RequestUtil.getHeaderOrParamValue(request, SSOConst.SSO_SESSION_TOKEN);
                if (sessionManager.isLogin(sessionToken)) {
                    /**
                     * can load userDetail
                     * and convert to json
                     */
                    return ResultBuilder.successData("load userDetail");
                }
            }
            return ResultBuilder.failed("接口未启用");
        } catch (SSOTokenException e) {
            log.debug("token is error", e);
            return ResultBuilder.failed(SSOConst.LOGIN_CHECK_FAILED_CODE, e.getMessage());
        }
    }
}
