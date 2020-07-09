package com.taoyuanx.littlesso.server.web.controller;

import com.google.code.kaptcha.Producer;
import com.taoyuanx.littlesso.server.commons.AccountConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author lianglei
 * @date 2019/1/8 16:30
 * @desc 验证码
 **/
@Controller
public class VafyCodeController {
    @Autowired
    private Producer captchaProducer;
    @GetMapping("vafycode")
    public void verification(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        String capText = captchaProducer.createText();
        request.getSession().setAttribute(AccountConstant.VAFY_CODE_SESSION_KEY, capText);
        BufferedImage image = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        out.flush();
    }
}
