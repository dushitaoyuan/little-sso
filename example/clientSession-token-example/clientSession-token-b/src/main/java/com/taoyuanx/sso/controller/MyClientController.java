package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.impl.SSOClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

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
