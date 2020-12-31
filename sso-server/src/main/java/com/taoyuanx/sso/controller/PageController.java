package com.taoyuanx.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author dushitaoyuan
 * @date 2020/12/31
 */
@Controller
public class PageController {
    @GetMapping("/sso/login")
    public String login(String redirectUrl, Model model) {
        model.addAttribute("redirectUrl", redirectUrl);
        return "login";
    }
}
