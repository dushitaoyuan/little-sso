package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.impl.SSOClient;
import com.taoyuanx.sso.client.impl.SSOTokenClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dushitaoyuan
 * @desc 接入sso的应用api
 * @date 2020/12/31
 */
@RequestMapping
@Controller
public class MyClientController {
    @Autowired
    SSOTokenClient ssoTokenClient;

    /**
     * when sso redirect to this url ,the sso-client applicaion must store the sso sessionId
     * <p>
     * see example: index.html
     */
    @GetMapping
    public String ssoUser(HttpServletRequest request, Model model) {
        SSOUser ssoUser = ssoTokenClient.getSSOUser(ssoTokenClient.getSessionToken(request));
        String sessionToken = request.getParameter(SSOClientConstant.SSO_SESSION_TOKEN);
        String refreshToken = request.getParameter(SSOClientConstant.SSO_REFRESH_TOKEN);
        Integer expire = Integer.parseInt(request.getParameter(SSOClientConstant.SSO_TOKEN_EXPIRE));
        model.addAttribute("ssoUser", ssoUser);
        model.addAttribute("sessionToken", sessionToken);
        model.addAttribute("refreshToken", refreshToken);
        model.addAttribute("expire", expire);
        return "index";
    }

    @GetMapping("user")
    @ResponseBody
    public SSOUser mySSoUser(HttpServletRequest request) {
        return ssoTokenClient.getSSOUser(ssoTokenClient.getSessionToken(request));
    }
    @GetMapping("userDetail")
    @ResponseBody
    public String mySSoToeknUserDetail(HttpServletRequest request) {
        return ssoTokenClient.getSSOTokenUserDetail(ssoTokenClient.getSessionToken(request));
    }

    @GetMapping("logoutPage")
    public String logoutPage(HttpServletRequest request) {
        return "logout";
    }
}
