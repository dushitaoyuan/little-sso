package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.impl.SSOClient;
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
    SSOClient ssoClient;

    /**
     * when sso redirect to this url ,the sso-client applicaion must store the sso sessionId
     * <p>
     * see example: index.html
     */
    @GetMapping
    public String ssoUser(HttpServletRequest request, Model model) {
        SSOUser ssoUser = ssoClient.getSSOUser(ssoClient.getSessionId(request));
        model.addAttribute("ssoUser", ssoUser);
        return "index";
    }

    @GetMapping("user")
    @ResponseBody
    public SSOUser mySSoUser(HttpServletRequest request) {
        return ssoClient.getSSOUser(ssoClient.getSessionId(request));
    }

}
