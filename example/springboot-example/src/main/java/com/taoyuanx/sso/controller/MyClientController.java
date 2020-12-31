package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.impl.SSOClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc 接入sso的应用api
 * @date 2020/12/31
 */
@RequestMapping("/app1")
@ResponseBody
@Controller
public class MyClientController {
    @Autowired
    SSOClient ssoClient;

    @GetMapping
    public SSOUser ssoUser(HttpServletRequest request) {
        Object attribute = request.getAttribute(SSOClientConstant.SESSION_KEY_NAME);
        if (Objects.isNull(attribute)) {
            throw new RuntimeException("user not login");
        }
        return ssoClient.getSSOUser(attribute.toString());
    }

}
